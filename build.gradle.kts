plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.3"
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
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
    implementation("commons-codec:commons-codec:1.16.0")
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
            "description" to "Minecraft Bot Plugin",
            "apiVersion" to "1.21"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        relocate("com.google.gson", "com.mythicisland.bot.libs.gson")
        relocate("okhttp3", "com.mythicisland.bot.libs.okhttp")
        relocate("org.apache.commons", "com.mythicisland.bot.libs.commons")
        relocate("com.github.benmanes.caffeine", "com.mythicisland.bot.libs.caffeine")
        relocate("org.bouncycastle", "com.mythicisland.bot.libs.bouncycastle")
        relocate("org.apache.commons.codec", "com.mythicisland.bot.libs.codec")

        archiveClassifier.set("")
        dependencies {
            exclude(dependency("org.slf4j:.*"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
