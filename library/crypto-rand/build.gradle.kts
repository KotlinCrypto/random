/*
 * Copyright (c) 2025 KotlinCrypto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
import co.touchlab.cklib.gradle.CKlibGradleExtension
import co.touchlab.cklib.gradle.CompileToBitcode
import co.touchlab.cklib.gradle.CompileToBitcodeExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.TargetSupportException
import org.jetbrains.kotlin.konan.util.ArchiveType
import org.jetbrains.kotlin.konan.util.DependencyProcessor
import org.jetbrains.kotlin.konan.util.DependencySource

plugins {
    id("configuration")
}

kmpConfiguration {
    configureShared(java9ModuleName = "org.kotlincrypto.random", publish = true) {
        common {
            pluginIds(libs.plugins.cklib.get().pluginId)

            sourceSetMain {
                dependencies {
                    api(libs.kotlincrypto.error)
                }
            }
        }

        kotlin {
            with(sourceSets) {
                val linuxMain = findByName("linuxMain")
                val androidNativeMain = findByName("androidNativeMain")

                if (linuxMain != null || androidNativeMain != null) {
                    val linuxAndroidMain = maybeCreate("linuxAndroidMain").apply {
                        dependsOn(getByName("unixMain"))
                    }
                    val linuxAndroidTest = maybeCreate("linuxAndroidTest").apply {
                        dependsOn(getByName("unixTest"))
                    }

                    linuxMain?.apply { dependsOn(linuxAndroidMain) }
                    findByName("linuxTest")?.apply { dependsOn(linuxAndroidTest) }

                    androidNativeMain?.apply { dependsOn(linuxAndroidMain) }
                    findByName("androidNativeTest")?.apply { dependsOn(linuxAndroidTest) }
                }
            }
        }

        kotlin {
            val cInteropDir = projectDir
                .resolve("src")
                .resolve("nativeInterop")
                .resolve("cinterop")

            val interopTaskInfo = targets.filterIsInstance<KotlinNativeTarget>().map { target ->
                if (target.konanTarget.family == Family.ANDROID) {
                    target.compilations["main"].cinterops.create("crypto_rand_sys") {
                        definitionFile.set(cInteropDir.resolve("$name.def"))
                        includeDirs(cInteropDir)
                    }
                }

                target.compilations["test"].cinterops.create("syscall") {
                    definitionFile.set(cInteropDir.resolve("$name.def"))
                }.interopProcessingTaskName to target.konanTarget
            }

            project.extensions.configure<CompileToBitcodeExtension>("cklib") {
                config.configure(libs)

                create("crypto_rand_sys") {
                    language = CompileToBitcode.Language.C
                    srcDirs = project.files(cInteropDir)
                    includeFiles = listOf("$compileName.c")

                    listOf(
                        "-Wno-unused-command-line-argument",
                    ).let { compilerArgs.addAll(it) }

                    val kt = KonanTarget.predefinedTargets[target]!!

                    // Must add dependency on the test cinterop task to ensure
                    // that Kotlin/Native dependencies get downloaded beforehand
                    interopTaskInfo.forEach { (interopTaskName, konanTarget) ->
                        if (kt != konanTarget) return@forEach
                        this.dependsOn(interopTaskName)
                    }
                }
            }
        }
    }
}

// CKLib uses too old of a version of LLVM for current version of Kotlin which produces errors for android
// native due to unsupported link arguments. Below is a supplemental implementation to download and use
// the -dev llvm compiler for the current kotlin version.
//
// The following info can be found in ~/.konan/kotlin-native-prebuild-{os}-{arch}-{kotlin version}/konan/konan.properties
private object LLVM {
    const val URL: String = "https://download.jetbrains.com/kotlin/native/resources/llvm"
    const val VERSION: String = "16.0.0"

    // llvm-{llvm version}-{arch}-{host}-dev-{id}
    object DevID {
        object Linux {
            const val x86_64: Int = 80
        }
        object MacOS {
            const val aarch64: Int = 63
            const val x86_64: Int = 50
        }
        object MinGW {
            const val x86_64: Int = 56
        }
    }
}

private fun CKlibGradleExtension.configure(libs: LibrariesForLibs) {
    kotlinVersion = libs.versions.gradle.kotlin.get()
    check(kotlinVersion == "2.1.10") {
        "Kotlin version out of date! Download URLs for LLVM need to be updated for ${project.path}"
    }

    val host = HostManager.simpleOsName()
    val arch = HostManager.hostArch()
    val (id, archive) = when (host) {
        "linux" -> when (arch) {
            "x86_64" -> LLVM.DevID.Linux.x86_64 to ArchiveType.TAR_GZ
            else -> null
        }
        "macos" -> when (arch) {
            "aarch64" -> LLVM.DevID.MacOS.aarch64 to ArchiveType.TAR_GZ
            "x86_64" -> LLVM.DevID.MacOS.x86_64 to ArchiveType.TAR_GZ
            else -> null
        }
        "windows" -> when (arch) {
            "x86_64" -> LLVM.DevID.MinGW.x86_64 to ArchiveType.ZIP
            else -> null
        }
        else -> null
    } ?: throw TargetSupportException("Unsupported host[$host] or arch[$arch]")

    val llvmDev = "llvm-${LLVM.VERSION}-${arch}-${host}-dev-${id}"
    val cklibDir = File(System.getProperty("user.home")).resolve(".cklib")
    llvmHome = cklibDir.resolve(llvmDev).path

    val source = DependencySource.Remote.Public(subDirectory = "${LLVM.VERSION}-${arch}-${host}")

    DependencyProcessor(
        dependenciesRoot = cklibDir,
        dependenciesUrl = LLVM.URL,
        dependencyToCandidates = mapOf(llvmDev to listOf(source)),
        homeDependencyCache = cklibDir.resolve("cache"),
        customProgressCallback = { _, currentBytes, totalBytes ->
            val total = totalBytes.toString()
            var current = currentBytes.toString()
            while (current.length < 15 && current.length < total.length) {
                current = " $current"
            }

            println("Downloading[$llvmDev] - $current / $total")
        },
        archiveType = archive,
    ).run()
}
