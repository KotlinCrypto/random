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

internal actual fun jsInt8Array(length: Int): JsInt8Array = js(CODE_JS_NEW_INT8_LENGTH)
internal actual fun jsInt8Array(buffer: JsArrayBufferLike): JsInt8Array = js(CODE_JS_NEW_INT8_BUFFER)

internal actual fun jsInt8ArrayGet(array: JsInt8Array, index: Int): Byte = js(CODE_JS_ARRAY_GET)
internal actual fun jsInt8ArraySet(array: JsInt8Array, index: Int, value: Byte) { js(CODE_JS_ARRAY_SET) }
