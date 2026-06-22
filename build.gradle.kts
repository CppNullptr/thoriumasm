plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.2"
}

group = "net.wvh"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "net.wvh.thoriumasm.main.Main"
        attributes["Implementation-Version"] = version
    }

    destinationDirectory.set(file("build/"))

    archiveFileName.set("tasm.jar")
}