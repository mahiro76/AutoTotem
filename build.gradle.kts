plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

group = "top.mahiro76"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":paper-1-20"))
    implementation(project(":paper-1-21"))
    implementation(project(":paper-26-1"))
    implementation(project(":paper-26-2"))
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("AutoTotem.jar")
        // 版本适配器实现由运行时按需反射加载，不进行类冲突合并
        exclude("module-info.class")
    }

    processResources {
        val props = mapOf("version" to project.version.toString())
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    runServer {
        minecraftVersion(libs.versions.minecraft120.get())
        jvmArgs("-Xms2G", "-Xmx2G")
    }
}
