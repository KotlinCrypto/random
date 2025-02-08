/*
 * Copyright (c) 2025 Matthew Nelson
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
package org.kotlincrypto.crypto.rand

import org.kotlincrypto.crypto.rand.internal.HAS_GET_RANDOM
import org.kotlincrypto.crypto.rand.internal.cryptoRandFillURandom
import kotlin.test.Test
import kotlin.test.assertTrue

class CryptoRandURandomUnitTest: CryptoRandUnitTest() {

    override val cryptoRand: CryptoRand = URandom

    @Test
    fun givenSystem_whenHasGetRandom_thenIsTrue() {
        // Should always be true, unless linux box running this test
        // is rocking LIBC 3.16 or below... Which I don't even think
        // Kotlin would run on?
        //
        // This simply confirms that the CryptoRand.Default.nextBytes
        // is working as expected using getrandom(2) to source them
        // when CryptoRandUnitTest is run from commonTest for Linux.
        assertTrue(HAS_GET_RANDOM)
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
