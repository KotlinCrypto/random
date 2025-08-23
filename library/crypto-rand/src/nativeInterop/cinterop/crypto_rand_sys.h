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

#ifndef CRYPTO_RAND_SYS_H
#define CRYPTO_RAND_SYS_H

#ifndef CRYPTO_RAND_HAS_SYS_GETRANDOM
#define CRYPTO_RAND_HAS_SYS_GETRANDOM 0
#endif // CRYPTO_RAND_HAS_SYS_GETRANDOM

#if CRYPTO_RAND_HAS_SYS_GETRANDOM
#include <sys/types.h>

/**
 * Performs syscall using SYS_getrandom and provided arguments.
 *
 * If __is_nonblock > 0, will use flag GRND_NONBLOCK, otherwise will use 0.
 * */
ssize_t __getrandom(void *__buf, size_t __len, int __is_nonblock);
#endif // CRYPTO_RAND_HAS_SYS_GETRANDOM

#endif // CRYPTO_RAND_SYS_H
