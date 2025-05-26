# random
[![badge-license]][url-license]
[![badge-latest-release]][url-latest-release]

[![badge-kotlin]][url-kotlin]
[![badge-error]][url-error]

![badge-platform-android]
![badge-platform-jvm]
![badge-platform-js]
![badge-platform-js-node]
![badge-platform-wasm]
![badge-platform-linux]
![badge-platform-macos]
![badge-platform-ios]
![badge-platform-tvos]
![badge-platform-watchos]
![badge-platform-windows]
![badge-support-android-native]
![badge-support-apple-silicon]
![badge-support-js-ir]
![badge-support-linux-arm]

A Kotlin Multiplatform library for procuring cryptographically secure random data.

The Linux/AndroidNative implementation was heavily inspired by [rust-random/getrandom][url-rust-random].

### Modules

 - [crypto-rand](library/crypto-rand/README.md)
 - [sample](sample/README.md)

### API Docs

 - [https://random.kotlincrypto.org][url-docs]

### Get Started

The best way to keep `KotlinCrypto` dependencies up to date is by using the 
[version-catalog][url-version-catalog]. Alternatively, see below.

<!-- TAG_VERSION -->

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.kotlincrypto.random:crypto-rand:0.5.1")
}
```

<!-- TAG_VERSION -->
[badge-latest-release]: https://img.shields.io/badge/latest--release-0.5.1-blue.svg?style=flat
[badge-license]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat

<!-- TAG_DEPENDENCIES -->
[badge-kotlin]: https://img.shields.io/badge/kotlin-2.1.10-blue.svg?logo=kotlin
[badge-error]: https://img.shields.io/badge/kotlincrypto.error-0.3.0-blue.svg

<!-- TAG_PLATFORMS -->
[badge-platform-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-platform-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-platform-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat
[badge-platform-js-node]: https://img.shields.io/badge/-nodejs-68a063.svg?style=flat
[badge-platform-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat
[badge-platform-macos]: http://img.shields.io/badge/-macos-111111.svg?style=flat
[badge-platform-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-platform-tvos]: http://img.shields.io/badge/-tvos-808080.svg?style=flat
[badge-platform-watchos]: http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat
[badge-platform-wasm]: https://img.shields.io/badge/-wasm-624FE8.svg?style=flat
[badge-platform-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat
[badge-support-android-native]: http://img.shields.io/badge/support-[AndroidNative]-6EDB8D.svg?style=flat
[badge-support-apple-silicon]: http://img.shields.io/badge/support-[AppleSilicon]-43BBFF.svg?style=flat
[badge-support-js-ir]: https://img.shields.io/badge/support-[js--IR]-AAC4E0.svg?style=flat
[badge-support-linux-arm]: http://img.shields.io/badge/support-[LinuxArm]-2D3F6C.svg?style=flat

[url-latest-release]: https://github.com/KotlinCrypto/random/releases/latest
[url-license]: https://www.apache.org/licenses/LICENSE-2.0.txt
[url-kotlin]: https://kotlinlang.org
[url-rust-random]: https://github.com/rust-random/getrandom
[url-version-catalog]: https://github.com/KotlinCrypto/version-catalog
[url-error]: https://github.com/KotlinCrypto/error
[url-docs]: https://random.kotlincrypto.org
