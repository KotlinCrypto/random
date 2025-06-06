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

import kotlinx.cinterop.*
import org.kotlincrypto.random.RandomnessProcurementException
import platform.windows.BCRYPT_USE_SYSTEM_PREFERRED_RNG
import platform.windows.BCryptGenRandom

@Throws(RandomnessProcurementException::class)
@OptIn(ExperimentalForeignApi::class)
internal actual fun ByteArray.cryptoRandFill() {
    val status = asUByteArray().usePinned { pinned ->
        BCryptGenRandom(
            null,
            pinned.addressOf(0),
            size.toULong().convert(),
            BCRYPT_USE_SYSTEM_PREFERRED_RNG.convert(),
        )
    }

    if (status == 0) return
    throw RandomnessProcurementException("Failed to obtain bytes from [BCryptGenRandom]. NTSTATUS: $status")
}
