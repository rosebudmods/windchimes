import windchimes.gradle.RewriteLegacyRecipeIngredientsAction

plugins {
    id("net.neoforged.moddev") version "2.0.141"
    id("neoforge-mutex")
}

repositories {
    exclusiveContent {
        forRepository {
            maven("https://maven.ryanhcode.dev/releases") { name = "RyanHCode" }
        }
        filter { includeGroup("dev.ryanhcode.sable-companion") }
    }
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-neoforge"
val modId = property("mod.id") as String

dependencies {
    if (sc.current.parsed >= "1.21.1" && sc.current.parsed < "1.21.2") {
        val companionVersion: String = sc.properties["deps.sable_companion"]
        jarJar(api("dev.ryanhcode.sable-companion:sable-companion-common-1.21.1:[$companionVersion,1.6.1)")!!)
    }
}

val requiredJava = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

neoForge {
    version = property("deps.neo_loader") as String

    mods {
        register(property("mod.id") as String) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            ideName = "NC - ${sc.current.version}"
            gameDirectory = file("../../run/")
            client()
        }

        register("server") {
            ideName = "NS - ${sc.current.version}"
            gameDirectory = file("../../run/")
            server()
        }
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
            register("description", "mod.description")
            register("authors", "mod.authors")
            register("license", "mod.license")
            register("homepage", "mod.homepage")
            register("issues", "mod.issues")
            register("neoforge_loader", "deps.neoforge_loader")
        }

        filesMatching("META-INF/neoforge.mods.toml") { expand(props) }

        exclude("fabric.mod.json", "*.ct", "*.classtweaker")

        if (sc.current.parsed < "1.21.2") doLast(RewriteLegacyRecipeIngredientsAction(modId))
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(jar.flatMap { it.archiveFile }, named<Jar>("sourcesJar").flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}