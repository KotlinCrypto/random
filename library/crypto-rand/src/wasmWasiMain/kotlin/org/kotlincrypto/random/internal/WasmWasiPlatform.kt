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
@file:Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT", "OPT_IN_USAGE")

package org.kotlincrypto.random.internal

import org.kotlincrypto.random.RandomnessProcurementException
import kotlin.wasm.WasmImport
import kotlin.wasm.unsafe.UnsafeWasmMemoryApi
import kotlin.wasm.unsafe.withScopedMemoryAllocator

/** [docs](https://github.com/WebAssembly/WASI/blob/main/legacy/preview1/docs.md#-random_getbuf-pointeru8-buf_len-size---result-errno) */
@Suppress("LocalVariableName")
@WasmImport("wasi_snapshot_preview1", "random_get")
private external fun randomGet(buf: /* Pointer<u8> */ Int, buf_len: /* u32 */ Int): Int

//@Throws(RandomnessProcurementException::class)
internal actual fun ByteArray.cryptoRandFill() {
    @OptIn(UnsafeWasmMemoryApi::class)
    withScopedMemoryAllocator { alloc ->
        val mem = alloc.allocate(size)
        val code = randomGet(mem.address.toInt(), size)
        if (code != 0) throw RandomnessProcurementException("Failed to obtain bytes from [random_get]. ErrorCode: $code")

        for (i in indices) {
            val ptr = mem.plus(i)
            this[i] = ptr.loadByte()
            ptr.storeByte(0) // Always ensure buffer is zeroed out
        }
    }
}
