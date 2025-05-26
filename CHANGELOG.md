# CHANGELOG

## Version 0.5.1 (2025-05-26)
 - Fixes issue on Android Native where `getrandom(2)` was not registering as available for 
   the given device API and always falling back to using `/dev/urandom` [[#40]][40]

## Version 0.5.0 (2025-02-25)
 - Replaces `SecRandomCopyBytes` usage with `CCRandomGenerateBytes` for `Darwin` [[#33]][33]
 - Updates `kotlin` to `2.1.10` [[#34]][34]
 - Updates `kotlincrypto.error` to `0.3.0` [[#34]][34]
 - Removes module `:secure-random` [[#31]][31]

## Version 0.4.0 (2025-02-09)
 - Adds module `crypto-rand` with new `CryptoRand` implementation [[#28]][28] [[#29]][29]
 - Deprecates `SecureRandom` in favor of `CryptoRand` [[#28]][28]
     - NOTE: This will be the final release for the `org.kotlincrypto:secure-random` publication.
 - Adds `dokka` documentation at `https://random.kotlincrypto.org` [[#28]][28]
 - Adds benchmarking to repository [[#26]][26]

## Version 0.3.2 (2024-08-31)
 - Updates dependencies
     - Kotlin `1.9.23` -> `1.9.24`
 - Fixes multiplatform metadata manifest `unique_name` parameter for 
   all source sets to be truly unique. [[#19]][19]
 - Updates jvm `.kotlin_module` with truly unique file name. [[#19]][19]

## Version 0.3.1 (2024-03-20)
 - Fix `js.nodejs` and `wasmJs.nodejs` array type exception [[#14]][14]
 - Ensure `wasmWasi` temporary array is back-filled when copying bytes [[#16]][16]

## Version 0.3.0 (2024-03-18)
 - Updates dependencies
     - Kotlin `1.9.21` -> `1.9.23`
 - Fix `ClassCastException` for `js.nodejs` [[#12]][12]
 - Fix max size exception for `js.browser` [[#12]][12]
 - Add experimental support for `wasmJs` & `wasmWasi` [[#12]][12]
 - Add support for Java9 `JPMS` via Multi-Release jar [#13][13]

## Version 0.2.0 (2023-11-30)
 - Updates dependencies
     - Kotlin `1.8.10` -> `1.9.21`
 - Drops support for the following deprecated targets:
     - `iosArm32`
     - `watchosX86`
     - `linuxArm32Hfp`
     - `linuxMips32`
     - `linuxMipsel32`
     - `mingwX86`
     - `wasm32`

## Version 0.1.0 (2023-03-07)
 - Initial Release

[12]: https://github.com/KotlinCrypto/random/pull/12
[13]: https://github.com/KotlinCrypto/random/pull/13
[14]: https://github.com/KotlinCrypto/random/pull/14
[16]: https://github.com/KotlinCrypto/random/pull/16
[19]: https://github.com/KotlinCrypto/random/pull/19
[26]: https://github.com/KotlinCrypto/random/pull/26
[28]: https://github.com/KotlinCrypto/random/pull/28
[29]: https://github.com/KotlinCrypto/random/pull/29
[31]: https://github.com/KotlinCrypto/random/pull/31
[33]: https://github.com/KotlinCrypto/random/pull/33
[34]: https://github.com/KotlinCrypto/random/pull/34
[40]: https://github.com/KotlinCrypto/random/pull/40

