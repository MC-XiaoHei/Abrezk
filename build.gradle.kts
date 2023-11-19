import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("java")
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.0.0"
}


group = "cn.xor7.abrezk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":src:resourcepack-converter"))
}

kotlin {
    jvmToolchain(8)
}

tasks.withType<ShadowJar> {
    minimize()
    archiveFileName.set("Abrezk-${project.version}.jar")
}

application {
    mainClass.set("cn.xor7.abrezk.MainKt")
}