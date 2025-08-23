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

#include "crypto_rand_sys.h"

#ifdef __CRYPTO_RAND_HAS_SYS_RANDOM__
#include <sys/syscall.h>
#include <unistd.h>

#if __has_include(<sys/random.h>)
#include <sys/random.h>
#endif // __has_include(<sys/random.h>)

#ifndef GRND_NONBLOCK
#define GRND_NONBLOCK 0x01
#endif // GRND_NONBLOCK

ssize_t
__SYS_getrandom(void *__buf, size_t __len, int __is_nonblock)
{
  unsigned int flags = 0;
  if (__is_nonblock) {
    flags = GRND_NONBLOCK;
  }
  return syscall(SYS_getrandom, __buf, __len, flags);
}
#endif // __CRYPTO_RAND_HAS_SYS_RANDOM__
