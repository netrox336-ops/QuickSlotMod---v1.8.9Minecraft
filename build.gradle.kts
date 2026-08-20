plugins {
    idea
    java
    id("gg.essential.loom") version "0.10.0.5"
    id("dev.architectury.architectury-pack200") version "0.1.3"
}

val modVersion: String by project
val mcVersion: String by project
val modId: String by project
val baseGroup: String by project

group = baseGroup
version = modVersion

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

repositories {
    mavenCentral()
    maven("https://repo.essential.gg/repository/maven-public")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.nea.moe/releases")
    maven("https://maven.notenoughupdates.org/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")
}

loom {
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
    }
}

tasks.processResources {
    inputs.property("version", modVersion)
    inputs.property("mcversion", mcVersion)

    filesMatching("mcmod.info") {
        expand(mapOf("version" to modVersion, "mcversion" to mcVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.jar {
    archiveBaseName.set("QuickSlot")
}
