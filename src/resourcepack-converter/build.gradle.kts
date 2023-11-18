plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.0"
}

group = "cn.xor7.abrezk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = group as String?
            artifactId = "abrezk"
            version = version
        }
    }
    repositories {
        mavenLocal()
    }
}