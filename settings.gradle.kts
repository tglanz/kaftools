rootProject.name = "kaftools"

include("cli", "file-connector")

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("application") {
            alias("picocli").to("info.picocli:picocli:4.6.2")
        }
        create("slf4j") {
            alias("api").to("org.slf4j:slf4j-api:2.0.0-alpha5")
            alias("simple").to("org.slf4j:slf4j-simple:2.0.0-alpha5")
        }
        create("kafka") {
            alias("clients").to("org.apache.kafka:kafka-clients:3.0.0")
            alias("connect").to("org.apache.kafka:connect-api:3.0.0")
        }
        create("utils") {
            alias("gson").to("com.google.code.gson:gson:2.8.6")
        }
    }
}
