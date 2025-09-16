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

@JsName("Uint8Array")
internal open external class JsUint8Array(length: Int) {
    fun subarray(start: Int, end: Int): JsUint8Array
}

internal inline operator fun JsUint8Array.get(index: Int): Byte = jsUint8ArrayGet(this, index)
internal inline operator fun JsUint8Array.set(index: Int, value: Byte) { jsUint8ArraySet(this, index, value) }

internal expect fun jsUint8ArrayGet(array: JsUint8Array, index: Int): Byte
internal expect fun jsUint8ArraySet(array: JsUint8Array, index: Int, value: Byte)
