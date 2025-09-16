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
@file:Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")

package org.kotlincrypto.random.internal

import org.kotlincrypto.random.RandomnessProcurementException

private const val BUFFER_SIZE = 1024 * 8

private external interface Crypto {
    // Browser
    fun getRandomValues(array: dynamic)
    // Node.js
    fun randomFillSync(buf: dynamic)
}

private open external class Uint8Array(length: Int) {
    fun subarray(start: Int, end: Int): Uint8Array
}

private fun isNodeJs(): Boolean = js(
"""
(typeof process !== 'undefined' 
    && process.versions != null 
    && process.versions.node != null) ||
(typeof window !== 'undefined' 
    && typeof window.process !== 'undefined' 
    && window.process.versions != null 
    && window.process.versions.node != null)
"""
) as Boolean

private fun cryptoNode(): Crypto = js("eval('require')('crypto')").unsafeCast<Crypto>()
private fun cryptoBrowser(): Crypto = js("(window ? (window.crypto ? window.crypto : window.msCrypto) : self.crypto)").unsafeCast<Crypto>()

private val IS_NODE_JS: Boolean by lazy { isNodeJs() }
private val CRYPTO: Crypto by lazy { if (IS_NODE_JS) cryptoNode() else cryptoBrowser() }

//@Throws(RandomnessProcurementException::class)
internal actual fun ByteArray.cryptoRandFill() {
    try {
        val jsCryptoFill = if (IS_NODE_JS) CRYPTO::randomFillSync else CRYPTO::getRandomValues

        // Cannot simply use the ByteArray when calling the supplied Crypto function.
        // Must utilize Uint8Array and then copy over results (See Issue #8). Also,
        // by chunking in a size less than 65536, it avoids hitting the ceiling imposed
        // on JS Browser (See issue #9).
        if (size <= BUFFER_SIZE) {
            // 1 shot it
            val bufDynamic = Uint8Array(size).asDynamic()
            jsCryptoFill(bufDynamic)
            for (i in indices) {
                this[i] = (bufDynamic[i] as Number).toByte()
                bufDynamic[i] = 0 // Always ensure buffer is zeroed out
            }
            return
        }

        val buf = Uint8Array(BUFFER_SIZE)
        val bufDynamic = buf.asDynamic()

        var needed = size
        var pos = 0

        // chunk
        while (needed > BUFFER_SIZE) {
            jsCryptoFill(bufDynamic)
            for (i in 0 until BUFFER_SIZE) {
                this[pos++] = (bufDynamic[i] as Number).toByte()
            }
            needed -= BUFFER_SIZE
        }

        // remainder
        if (needed > 0) {
            jsCryptoFill(buf.subarray(0, needed).asDynamic())

            for (i in 0 until needed) {
                this[pos++] = (bufDynamic[i] as Number).toByte()
                bufDynamic[i] = 0 // Always ensure buffer is zeroed out
            }
        }

        for (i in needed until BUFFER_SIZE) {
            bufDynamic[i] = 0 // Always ensure buffer is zeroed out
        }
    } catch (t: Throwable) {
        val fn = if (IS_NODE_JS) "randomFillSync" else "getRandomValues"
        throw RandomnessProcurementException("Failed to obtain bytes from [crypto.$fn]", t)
    }
}
