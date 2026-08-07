plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.paperApi121)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("AutoTotem-1.21.jar")
    }

    processResources {
        val props = mapOf("version" to project.version.toString())
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    runServer {
        minecraftVersion(libs.versions.minecraft121.get())
        jvmArgs("-Xms2G", "-Xmx2G")
    }
}
