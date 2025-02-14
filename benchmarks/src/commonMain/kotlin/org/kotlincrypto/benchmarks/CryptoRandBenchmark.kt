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
@file:Suppress("unused")

package org.kotlincrypto.benchmarks

import kotlinx.benchmark.*
import org.kotlincrypto.random.CryptoRand

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 3)
open class CryptoRandBenchmark {

    private val buf = ByteArray(20)

    @Setup
    fun setup() {
        CryptoRand.Default.nextBytes(buf)
    }

    @Benchmark
    fun nextBytes() {
        CryptoRand.Default.nextBytes(buf)
    }
}
