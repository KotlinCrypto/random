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
@file:Suppress("OPT_IN_USAGE")

package org.kotlincrypto.crypto.rand

import org.kotlincrypto.crypto.rand.internal.cryptoRandFill

/**
 * A random number generator (RNG), suitable for use in cryptographic operations and/or
 * seeding other random number generators.
 *
 * e.g. (Seeding a Random Number Generator)
 *
 *     val rng = Random(
 *         seed = CryptoRand.Default.nextBytes(ByteArray(Long.SIZE_BYTES))
 *             // Using KotlinCrypto/bitops endian library
 *             // to convert 8 bytes to a Long
 *             .beLongAt(0)
 *     )
 *
 * @see [Default]
 * */
public abstract class CryptoRand @DelicateCryptoRandApi protected constructor() {

    /**
     * The default implementation of [CryptoRand].
     *
     * The following APIs are used for procuring cryptographically secure random data:
     *  - JVM: [java.security.SecureRandom](https://docs.oracle.com/javase/8/docs/api/java/security/SecureRandom.html)
     *  - JS:
     *      - Browser: [Crypto.getRandomValues()](https://developer.mozilla.org/docs/Web/API/Crypto/getRandomValues)
     *      - Node: [Crypto.randomFillSync()](https://nodejs.org/api/crypto.html#cryptorandomfillsyncbuffer-offset-size)
     *  - WasmJs:
     *      - Browser: [Crypto.getRandomValues()](https://developer.mozilla.org/docs/Web/API/Crypto/getRandomValues)
     *      - Node: [Crypto.randomFillSync()](https://nodejs.org/api/crypto.html#cryptorandomfillsyncbuffer-offset-size)
     *  - WasmWasi: [random_get](https://github.com/WebAssembly/WASI/blob/main/legacy/preview1/docs.md#random_get)
     *  - Native:
     *      - Android Native targets: [getrandom(2)](https://www.man7.org/linux/man-pages/man2/getrandom.2.html). If unavailable, `/dev/urandom` is used.
     *      - Linux targets: [getrandom(2)](https://www.man7.org/linux/man-pages/man2/getrandom.2.html). If unavailable, `/dev/urandom` is used.
     *      - Apple targets: [SecRandomCopyBytes](https://developer.apple.com/documentation/security/1399291-secrandomcopybytes)
     *      - Windows targets: [BCryptGenRandom](https://learn.microsoft.com/en-us/windows/win32/api/bcrypt/nf-bcrypt-bcryptgenrandom)
     * */
    public companion object Default: CryptoRand() {

        public override fun nextBytes(buf: ByteArray): ByteArray {
            if (buf.isEmpty()) return buf
            buf.cryptoRandFill()
            return buf
        }
    }

    /**
     * Fills the provided [buf] array with securely generated random data, procured from system
     * sources. If the underlying system has not collected enough entropy yet, this function
     * will block.
     *
     * **NOTE:** [buf] is modified in place, whereby any errors resulting in an [RNGException] may
     * leave [buf] partially filled. It **must** be discarded in that event.
     *
     * @param [buf] the array to fill
     * @return [buf]
     * @throws [RNGException] if the underlying API fails.
     * */
    public abstract fun nextBytes(buf: ByteArray): ByteArray
}
