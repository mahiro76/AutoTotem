plugins {
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.shadow) apply false
}

allprojects {
    group = "top.mahiro76"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
