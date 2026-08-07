plugins {
    id("java-library")
}

dependencies {
    compileOnly(libs.paperApi120)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}
