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
@file:Suppress("OPT_IN_USAGE")

package org.kotlincrypto.random.internal.js

import kotlin.math.min

internal actual val IS_NODE_JS: Boolean by lazy { isNodeJs() }

internal actual fun jsCryptoBrowser(): JsCrypto = js(CODE_JS_CRYPTO_BROWSER)
internal actual fun jsCryptoNode(): JsCrypto = js(CODE_JS_CRYPTO_NODE)

private fun isNodeJs(): Boolean = js(CODE_IS_NODE_JS)

private const val BUFFER_SIZE = 1024 * 8

internal actual fun ByteArray.cryptoRandFill(procure: (JsUint8Array) -> Unit) {
    // Kotlin/WasmJs does not provide access to ByteArray.storage
    // (i.e. the WasmByteArray), so we must always use a buffer and
    // copy back and forth.
    val buf = JsUint8Array.new(length = min(size, BUFFER_SIZE))

    try {
        var remainder = size
        var offset = 0
        while (remainder > 0) {
            val len = if (remainder >= buf.length) {
                procure(buf)
                buf.length
            } else {
                procure(buf.subarray(0, remainder))
                remainder
            }
            repeat(len) { i -> this[offset++] = buf[i] }
            remainder -= len
        }
    } finally {
        // Always ensure the buffer gets zeroed out when done.
        repeat(buf.length) { i -> buf[i] = 0u }
    }
}
