# Module crypto-rand

Procure cryptographically secure random data from system sources.

```kotlin
fun main() {
    val bytes = try {
        CryptoRand.Default.nextBytes(ByteArray(16))
    } catch (e: RandomnessProcurementException) {
        // Underlying platform API failed to procure data from system sources.
        e.printStackTrace()
        return
    }

    println(bytes.toList())
}
```
