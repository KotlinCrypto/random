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
@file:Suppress("NOTHING_TO_INLINE", "RemoveRedundantCallsOfConversionMethods")

package org.kotlincrypto.random.internal

import kotlinx.cinterop.*
import org.kotlincrypto.random.RandomnessProcurementException
import platform.posix.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// getrandom(2) available for Linux Kernel 3.17+ (Android API 26+)
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal val HAS_GET_RANDOM: Boolean by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    val buf = ByteArray(1)
    val result = buf.usePinned { pinned ->
        __SYS_getrandom(__buf = pinned.addressOf(0), __len = buf.size.convert(), __is_nonblock = 1).toInt()
    }
    if (result >= 0) return@lazy true

    when (errno) {
        ENOSYS, // No kernel support
        EPERM, // Blocked by seccomp
        -> false
        else
        -> true
    }
}

@Throws(RandomnessProcurementException::class)
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal actual fun ByteArray.cryptoRandFill() {
    if (HAS_GET_RANDOM) {
        cryptoRandFill { ptr, len -> __SYS_getrandom(__buf = ptr, __len = len.convert(), __is_nonblock = 0).toInt() }
    } else {
        cryptoRandFillURandom()
    }
}

@Throws(RandomnessProcurementException::class)
@OptIn(ExperimentalContracts::class, ExperimentalForeignApi::class)
internal inline fun ByteArray.cryptoRandFill(procure: (ptr: CPointer<ByteVar>, len: Int) -> Int) {
    contract {
        callsInPlace(procure, InvocationKind.UNKNOWN)
    }

    usePinned { pinned ->
        var pos = 0
        while (pos < size) {
            val numBytes = procure(pinned.addressOf(pos), size - pos)
            if (numBytes >= 0) {
                pos += numBytes
                continue
            }
            when (val e = errno) {
                EINTR -> continue // retry
                else -> throw errnoToRandomnessProcurementException(e)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal inline fun errnoToRandomnessProcurementException(errno: Int): RandomnessProcurementException {
    val message = strerror(errno)?.toKStringFromUtf8() ?: "errno: $errno"
    return RandomnessProcurementException(message)
}
