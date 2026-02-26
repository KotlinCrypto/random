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
@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package org.kotlincrypto.random.internal.js

import kotlin.js.JsName

@JsName("Int8Array")
internal open external class JsInt8Array: JsTypedArrayLike {
    override val buffer: JsArrayBufferLike
    override val length: Int
    override fun subarray(start: Int, end: Int): JsInt8Array
    internal companion object
}

internal inline fun JsInt8Array.Companion.new(length: Int): JsInt8Array = jsInt8Array(length)
internal inline fun JsInt8Array.Companion.new(buffer: JsArrayBufferLike): JsInt8Array = jsInt8Array(buffer)

internal inline operator fun JsInt8Array.get(index: Int): Byte = jsInt8ArrayGet(this, index)
internal inline operator fun JsInt8Array.set(index: Int, value: Byte) { jsInt8ArraySet(this, index, value) }

internal inline fun JsInt8Array.asJsUint8Array(): JsUint8Array = JsUint8Array.new(buffer)

internal const val CODE_JS_NEW_INT8_LENGTH = "new Int8Array(length)"
internal const val CODE_JS_NEW_INT8_BUFFER = "new Int8Array(buffer)"

internal expect fun jsInt8Array(length: Int): JsInt8Array
internal expect fun jsInt8Array(buffer: JsArrayBufferLike): JsInt8Array

internal expect fun jsInt8ArrayGet(array: JsInt8Array, index: Int): Byte
internal expect fun jsInt8ArraySet(array: JsInt8Array, index: Int, value: Byte)
