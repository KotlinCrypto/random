/*
 * Copyright (c) 2026 KotlinCrypto
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
package org.kotlincrypto.random.internal.js

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CryptoRandFillJsWasmJsUnitTest {

    @Test
    fun givenArray_whenLessThanMax_thenFillIsInvokedOnce() {
        val ba = ByteArray(20)
        var invocations = 0
        ba.cryptoRandFill { jsArray ->
            for (i in 0 until jsArray.length) { jsArray[i] = 1.toUByte() }
            invocations++
        }
        assertEquals(1, invocations)
        ba.forEachIndexed { i, b ->
            assertEquals(1.toByte(), b, "index[$i]")
        }
    }

    @Test
    fun givenArray_whenGreaterThanLimit_thenFillIsMultipleTimes() {
        val ba = ByteArray((JS_CRYPTO_MAX_FILL * 3) + 25)
        var invocations = 0
        ba.cryptoRandFill { jsArray ->
            for (i in 0 until jsArray.length) { jsArray[i] = 1.toUByte() }
            invocations++
        }
        assertTrue(invocations > 3, "invocations[$invocations]")
        ba.forEachIndexed { i, b ->
            assertEquals(1.toByte(), b, "index[$i]")
        }
    }
}
