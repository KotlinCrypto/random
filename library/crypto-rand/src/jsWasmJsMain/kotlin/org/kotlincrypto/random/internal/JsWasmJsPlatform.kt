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
package org.kotlincrypto.random.internal

import org.kotlincrypto.random.RandomnessProcurementException
import org.kotlincrypto.random.internal.js.IS_NODE_JS
import org.kotlincrypto.random.internal.js.JsCrypto
import org.kotlincrypto.random.internal.js.JsUint8Array
import org.kotlincrypto.random.internal.js.get
import org.kotlincrypto.random.internal.js.jsCryptoBrowser
import org.kotlincrypto.random.internal.js.jsCryptoNode
import org.kotlincrypto.random.internal.js.set

private const val BUFFER_SIZE = 1024 * 8

private val JS_CRYPTO: JsCrypto by lazy { if (IS_NODE_JS) jsCryptoNode() else jsCryptoBrowser() }

@Throws(RandomnessProcurementException::class)
internal actual fun ByteArray.cryptoRandFill() {
    try {
        val jsCryptoFill = if (IS_NODE_JS) JS_CRYPTO::randomFillSync else JS_CRYPTO::getRandomValues

        // Cannot simply use the ByteArray when calling the supplied Crypto function.
        // Must utilize Uint8Array and then copy over results (See Issue #8). Also,
        // by chunking in a size less than 65536, it avoids hitting the ceiling imposed
        // on JS Browser (See issue #9).
        if (size <= BUFFER_SIZE) {
            // 1 shot it
            val buf = JsUint8Array(size)
            jsCryptoFill(buf)
            for (i in indices) {
                this[i] = buf[i]
                buf[i] = 0 // Always ensure buffer is zeroed out
            }
            return
        }

        val buf = JsUint8Array(BUFFER_SIZE)

        var needed = size
        var pos = 0

        // chunk
        while (needed > BUFFER_SIZE) {
            jsCryptoFill(buf)
            for (i in 0 until BUFFER_SIZE) {
                this[pos++] = buf[i]
            }
            needed -= BUFFER_SIZE
        }

        // remainder
        if (needed > 0) {
            jsCryptoFill(buf.subarray(0, needed))

            for (i in 0 until needed) {
                this[pos++] = buf[i]
                buf[i] = 0 // Always ensure buffer is zeroed out
            }
        }

        for (i in needed until BUFFER_SIZE) {
            buf[i] = 0 // Always ensure buffer is zeroed out
        }
    } catch (t: Throwable) {
        val fn = if (IS_NODE_JS) "randomFillSync" else "getRandomValues"
        throw RandomnessProcurementException("Failed to obtain bytes from [crypto.$fn]", t)
    }
}
