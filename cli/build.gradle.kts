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
    archiveBaseName.set("kaftools-cli")
    destinationDirectory.set(file(distriubtionDirectory))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(slf4j.api)
    implementation(slf4j.simple)
    implementation(application.picocli)
    implementation(kafka.clients)
    implementation(utils.gson)
}

task<Copy>("copyDependencies") {
    from(configurations.default)
    into(distriubtionDirectory)
}

task<CreateStartScripts>("createStartScripts") {
    outputDir = file(scriptsDirectory)
    mainClass.set("tglanz.kaftools.cli.Cli")
    applicationName = "cli"
    classpath = files("*")
}

task<JavaExec>("runCli") {
    classpath(sourceSets["main"].runtimeClasspath)
    mainClass.set("tglanz.kaftools.cli.Cli")
}

tasks.named("build") {
    finalizedBy("copyDependencies")
    finalizedBy("createStartScripts")
}