plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.paperApi262)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("AutoTotem-26.2.jar")
    }

    processResources {
        val props = mapOf("version" to project.version.toString())
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    runServer {
        minecraftVersion(libs.versions.minecraft262.get())
        jvmArgs("-Xms2G", "-Xmx2G")
    }
}
