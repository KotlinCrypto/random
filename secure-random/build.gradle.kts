/*
 * Copyright (c) 2023 Matthew Nelson
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
plugins {
    id("configuration")
}

kmpConfiguration {
    configureShared(publish = true) {
        linuxX64 {
            sourceSetTest {
                dependencies {
                    implementation(libs.kotlin.coroutines.test)
                }
            }
        }

        kotlin {
            sourceSets {
                val linuxMain = findByName("linuxMain")
                val androidNativeMain = findByName("androidNativeMain")

                if (linuxMain != null || androidNativeMain != null) {
                    val linuxAndroidMain = maybeCreate("linuxAndroidMain").apply {
                        dependsOn(getByName("nativeMain"))
                    }
                    val linuxAndroidTest = maybeCreate("linuxAndroidTest").apply {
                        dependsOn(getByName("nativeTest"))
                    }

                    linuxMain?.apply { dependsOn(linuxAndroidMain) }
                    findByName("linuxTest")?.apply { dependsOn(linuxAndroidTest) }

                    androidNativeMain?.apply { dependsOn(linuxAndroidMain) }
                    findByName("androidNativeTest")?.apply { dependsOn(linuxAndroidTest) }
                }
            }
        }
    }
}
