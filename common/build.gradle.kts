plugins {
    id("java-library")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paperApi120)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}
