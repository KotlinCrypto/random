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
package org.kotlincrypto.random.internal.js

internal external interface JsCrypto {
    // Browser
    fun getRandomValues(array: JsUint8Array)
    // Node.js
    fun randomFillSync(buf: JsUint8Array)
}

internal const val CODE_IS_NODE_JS: String =
"""
(typeof process !== 'undefined' 
    && process.versions != null 
    && process.versions.node != null) ||
(typeof window !== 'undefined' 
    && typeof window.process !== 'undefined' 
    && window.process.versions != null 
    && window.process.versions.node != null)
"""

internal const val CODE_JS_CRYPTO_BROWSER: String =
"(window ? (window.crypto ? window.crypto : window.msCrypto) : self.crypto)"

internal const val CODE_JS_CRYPTO_NODE: String =
"eval('require')('crypto')"

internal expect val IS_NODE_JS: Boolean

internal expect fun jsCryptoBrowser(): JsCrypto
internal expect fun jsCryptoNode(): JsCrypto
