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
@file:Suppress("NOTHING_TO_INLINE")

package org.kotlincrypto.random.internal

import kotlinx.cinterop.*
import org.kotlincrypto.random.RandomnessProcurementException
import platform.posix.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private const val INFINITE_TIMEOUT: Int = -1

@Throws(RandomnessProcurementException::class)
@OptIn(ExperimentalContracts::class, ExperimentalForeignApi::class)
private inline fun <T: Any?> openRDONLY(path: String, block: (fd: Int) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    var fd: Int
    do {
        fd = open(path, O_RDONLY or O_CLOEXEC, 0)
    } while (fd == -1 && errno == EINTR)
    if (fd == -1) throw errnoToRandomnessProcurementException(errno)

    try {
        return block(fd)
    } finally {
        close(fd)
    }
}

// Only ever need to do once.
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
private val ENSURE_URANDOM_IS_SEEDED: Unit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    openRDONLY(path = "/dev/random") { fd ->
        memScoped {
            val pollFd = alloc<pollfd> {
                this.fd = fd
                this.events = POLLIN.convert()
                this.revents = 0
            }

            while (true) {
                if (poll(pollFd.ptr, 1u, INFINITE_TIMEOUT) >= 0) break

                when (val err = errno) {
                    EINTR,
                    EAGAIN -> continue
                    else -> throw errnoToRandomnessProcurementException(err)
                }
            }
        }
    }
}

// Exposed for testing purposes
@Throws(RandomnessProcurementException::class)
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal fun ByteArray.cryptoRandFillURandom() {
    ENSURE_URANDOM_IS_SEEDED

    openRDONLY(path = "/dev/urandom") { fd ->
        cryptoRandFill { ptr, len ->
            read(fd, ptr, len.toULong().convert()).convert()
        }
    }
}
