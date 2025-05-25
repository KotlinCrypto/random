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
package org.kotlincrypto.random.test.android

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import io.matthewnelson.kmp.file.toFile
import io.matthewnelson.kmp.process.Process
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidNativeTest {

    private val ctx = ApplicationProvider.getApplicationContext<Application>().applicationContext
    private val nativeLibraryDir = ctx.applicationInfo.nativeLibraryDir.toFile().absoluteFile

    @Test
    fun givenAndroidNative_whenExecuteTestBinary_thenIsSuccessful() {
        val out = Process.Builder(executable = nativeLibraryDir.resolve("libTestExec.so"))
            .output {
                timeoutMillis = 5_000
                maxBuffer = Int.MAX_VALUE / 2
            }

        assertEquals(0, out.processInfo.exitCode, out.stdout)
        println(out.stdout)
        println(out.stderr)
    }
}
