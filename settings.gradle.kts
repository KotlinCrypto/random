rootProject.name = "random"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("build-logic")

@Suppress("PrivatePropertyName")
private val CHECK_PUBLICATION: String? by settings

if (CHECK_PUBLICATION != null) {
    include(":tools:check-publication")
} else {
    listOf(
        "crypto-rand",
    ).forEach { name ->
        include(":library:$name")
    }

    include(":benchmarks")
    include(":sample")
}
