plugins {
    id("org.jetbrains.intellij.platform") version "2.10.5"
    java
}

repositories {
    mavenCentral()
    intellijPlatform.defaultRepositories()
}

dependencies {
    intellijPlatform {
        intellijIdea("2026.1.2")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.named("buildSearchableOptions") {
    enabled = false
}
