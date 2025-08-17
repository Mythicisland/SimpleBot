plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.6"
}

group = "com.mythicisland.bot"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "PaperMC"
    }
    maven("https://libraries.minecraft.net") {
        name = "Minecraft"
    }
    maven("https://jitpack.io") {
        name = "JitPack"
    }
    maven("https://repo.opencollab.dev/maven-snapshots/") {
        name = "OpenCollab-Snapshots"
    }
    maven("https://repo.opencollab.dev/maven-releases/") {
        name = "OpenCollab-Releases"
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation("com.github.steveice10:mcprotocollib:1.20-1")
    implementation("com.mojang:authlib:6.0.54")
    implementation("net.raphimc:MinecraftAuth:4.1.1")
    implementation("io.netty:netty-all:4.1.115.Final")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.21"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        relocate("com.google.gson", "com.mythicisland.island.bot.libs.gson")
        relocate("okhttp3", "com.mythicisland.island.bot.libs.okhttp")
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }

}