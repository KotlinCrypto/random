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
@file:Suppress("UnnecessaryOptInAnnotation")

package org.kotlincrypto.random.internal

import kotlinx.cinterop.*
import org.kotlincrypto.random.RandomnessProcurementException
import platform.CoreCrypto.CCRandomGenerateBytes
import platform.CoreCrypto.kCCSuccess

@Throws(RandomnessProcurementException::class)
internal actual fun ByteArray.cryptoRandFill() {
    @OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
    val status = usePinned { pinned ->
        CCRandomGenerateBytes(pinned.addressOf(0), size.toUInt().convert())
    }

    if (status == kCCSuccess) return
    throw RandomnessProcurementException("Failed to obtain bytes from [CCRandomGenerateBytes]. kCCStatus: $status")
}
