val distriubtionDirectory = "build/lib"
val scriptsDirectory = "build/scripts"

plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

sourceSets {
    main {
        resources {
            srcDirs.add(file("src/main/resources"))
        }
    }
}

tasks.jar {
    archiveBaseName.set("kaftools-file-connector")
    destinationDirectory.set(file(distriubtionDirectory))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(slf4j.api)
    implementation(slf4j.simple)
    implementation(kafka.connect)
}

task<Copy>("copyDependencies") {
    from(configurations.default)
    into(distriubtionDirectory)
}
