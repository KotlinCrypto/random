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
@file:Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT", "KotlinRedundantDiagnosticSuppress")

package org.kotlincrypto.random.internal

import org.kotlincrypto.random.RandomnessProcurementException

private const val BUFFER_SIZE = 1024 * 8

private external interface Crypto: JsAny {
    // Browser
    fun getRandomValues(array: Uint8Array)
    // Node.js
    fun randomFillSync(buf: Uint8Array)
}

private open external class Uint8Array(length: Int) {
    fun subarray(start: Int, end: Int): Uint8Array
}

@Suppress("UNUSED_PARAMETER")
private fun uint8ArrayGet(obj: Uint8Array, index: Int): Byte = js("obj[index]")
@Suppress("NOTHING_TO_INLINE")
private inline operator fun Uint8Array.get(index: Int): Byte = uint8ArrayGet(this, index)

@Suppress("UNUSED_PARAMETER")
private fun uint8ArraySet(obj: Uint8Array, index: Int, value: Byte) { js("obj[index] = value") }
@Suppress("NOTHING_TO_INLINE")
private inline operator fun Uint8Array.set(index: Int, value: Byte) { uint8ArraySet(this, index, value) }

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
)

private fun cryptoNode(): Crypto = js("eval('require')('crypto')")
private fun cryptoBrowser(): Crypto = js("(window ? (window.crypto ? window.crypto : window.msCrypto) : self.crypto)")

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
            val buf = Uint8Array(size)
            jsCryptoFill(buf)
            for (i in indices) {
                this[i] = buf[i]
                buf[i] = 0 // Always ensure buffer is zeroed out
            }
            return
        }

        val buf = Uint8Array(BUFFER_SIZE)

        var needed = size
        var pos = 0

        // chunk
        while (needed > BUFFER_SIZE) {
            jsCryptoFill(buf)
            for (i in 0..<BUFFER_SIZE) {
                this[pos++] = buf[i]
            }
            needed -= BUFFER_SIZE
        }

        // remainder
        if (needed > 0) {
            jsCryptoFill(buf.subarray(0, needed))

            for (i in 0..<needed) {
                this[pos++] = buf[i]
                buf[i] = 0 // Always ensure buffer is zeroed out
            }
        }

        for (i in needed..<BUFFER_SIZE) {
            buf[i] = 0 // Always ensure buffer is zeroed out
        }
    } catch (t: Throwable) {
        val fn = if (IS_NODE_JS) "randomFillSync" else "getRandomValues"
        throw RandomnessProcurementException("Failed to obtain bytes from [crypto.$fn]", t)
    }
}
