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
package org.kotlincrypto.random

import org.kotlincrypto.random.internal.HAS_GET_RANDOM
import org.kotlincrypto.random.internal.cryptoRandFillURandom
import kotlin.test.Test
import kotlin.test.assertEquals

class CryptoRandURandomUnitTest: CryptoRandUnitTest() {

    override val cryptoRand: CryptoRand = URandom

    @Test
    fun givenPlatform_whenHasGetRandom_thenIsAsExpected() {
        assertEquals(SHOULD_HAVE_GET_RANDOM, HAS_GET_RANDOM)
    }

    @OptIn(DelicateCryptoRandApi::class)
    private companion object URandom: CryptoRand() {
        override fun nextBytes(buf: ByteArray): ByteArray {
            if (buf.isEmpty()) return buf
            buf.cryptoRandFillURandom()
            return buf
        }
    }
}
