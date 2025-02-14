# CryptoRand Sample App

Simple application that will use `CryptoRand.Default` to generate and then print random bytes.

### Running

Java:
```shell
./gradlew :sample:jvmRun -PKMP_TARGETS="JVM"
```

Native:
```shell
./gradlew :sample:runDebugExecutableNativeHost -PKMP_TARGETS="LINUX_ARM64,LINUX_X64,MACOS_ARM64,MACOS_X64,MINGW_X64"
```
