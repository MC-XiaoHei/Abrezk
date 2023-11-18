pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "Abrezk"
include("src:resourcepack-converter")
findProject(":src:resourcepack-converter")?.name = "resourcepack-converter"
