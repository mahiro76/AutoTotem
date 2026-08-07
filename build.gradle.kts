plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

// group 和 version 从 gradle.properties 读取（Gradle 自动加载为项目属性）

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
        archiveBaseName.set("AutoTotem-Plugin-${providers.gradleProperty("serverPlatform").get()}")
        archiveClassifier.set("")
        // 命名 AutoTotem-<serverPlatform>-<version>.jar
        // serverPlatform 在 gradle.properties 中定义，改服务端适配只需改那一处
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
