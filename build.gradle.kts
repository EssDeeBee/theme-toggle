plugins {
    id("org.jetbrains.intellij.platform") version "2.7.0"
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
    intellijPlatform.defaultRepositories()
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2025.2")
    }
}

kotlin {
    jvmToolchain(21)
}