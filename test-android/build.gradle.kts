/*
 * Copyright (c) 2025 KotlinCrypto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
import com.android.build.gradle.tasks.MergeSourceSetFolders

plugins {
    id("configuration")
}

repositories { google() }

kmpConfiguration {
    configure {
        val jniLibsDir = project
            .projectDir
            .resolve("src")
            .resolve("androidInstrumentedTest")
            .resolve("jniLibs")

        project.tasks.all {
            if (name != "clean") return@all
            doLast { jniLibsDir.deleteRecursively() }
        }

        androidLibrary {
            android {
                buildToolsVersion = "35.0.1"
                compileSdk = 35
                namespace = "org.kotlincrypto.random.test.android"

                defaultConfig {
                    minSdk = 21

                    testInstrumentationRunnerArguments["disableAnalytics"] = true.toString()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                packaging.jniLibs.useLegacyPackaging = true

                sourceSets["androidTest"].jniLibs.srcDir(jniLibsDir)
            }

            sourceSetTestInstrumented {
                dependencies {
                    implementation(libs.androidx.test.core)
                    implementation(libs.androidx.test.runner)
                    implementation(libs.kmp.process)
                }
            }
        }

        common {
            sourceSetTest {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        }

        kotlin {
            if (!project.plugins.hasPlugin("com.android.base")) return@kotlin

            try {
                project.evaluationDependsOn(":library:crypto-rand")
            } catch (_: Throwable) {}

            val cryptoRandProject = project(":library:crypto-rand")

            val cryptoRandBuildDir = cryptoRandProject
                .layout
                .buildDirectory
                .asFile.get()

            val nativeTestBinariesTasks = listOf(
                "Arm32" to "armeabi-v7a",
                "Arm64" to "arm64-v8a",
                "X64" to "x86_64",
                "X86" to "x86",
            ).mapNotNull { (arch, abi) ->
                val nativeTestBinariesTask = cryptoRandProject
                    .tasks
                    .findByName("androidNative${arch}TestBinaries")
                    ?: return@mapNotNull null

                val abiDir = jniLibsDir.resolve(abi)
                if (!abiDir.exists() && !abiDir.mkdirs()) throw RuntimeException("mkdirs[$abiDir]")

                val testExecutable = cryptoRandBuildDir
                    .resolve("bin")
                    .resolve("androidNative$arch")
                    .resolve("debugTest")
                    .resolve("test.kexe")

                nativeTestBinariesTask.doLast {
                    testExecutable.copyTo(abiDir.resolve("libTestExec.so"), overwrite = true)
                }

                nativeTestBinariesTask
            }

            project.tasks.withType(MergeSourceSetFolders::class.java) {
                if (name != "mergeDebugAndroidTestJniLibFolders") return@withType
                nativeTestBinariesTasks.forEach { task -> this.dependsOn(task) }
            }
        }
    }
}
