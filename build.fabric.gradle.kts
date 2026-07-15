import windchimes.gradle.RewriteFabricMetadataAction
import windchimes.gradle.RewriteLegacyRecipeIngredientsAction

plugins {
    id("dev.kikugie.loom-back-compat")
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-fabric"
val modId = property("mod.id") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

dependencies {
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"]))
    }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    fapi(
        "fabric-lifecycle-events-v1",
        "fabric-resource-loader-v0",
        "fabric-content-registries-v0",
        "fabric-registry-sync-v0",
        "fabric-object-builder-api-v1",
        "fabric-rendering-v1"
    )
    if (sc.current.parsed >= "26.1") fapi("fabric-creative-tab-api-v1") else fapi("fabric-item-group-api-v1")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        displayName.set("${if (name == "client") "FC" else "FS"} - ${sc.current.version}")
        appendProjectPathToDisplayName.set(false)
        preferGradleTask = false
        generateRunConfig = true
        runDirectory = rootProject.file("run")
        jvmArguments.add("-Dmixin.debug.export=true")
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
            register("sources", "mod.sources")
            register("issues", "mod.issues")
            register("discord", "mod.discord")
            register("fabric_loader", "deps.fabric_loader")
        }

        filesMatching("fabric.mod.json") { expand(props) }

        exclude("META-INF/neoforge.mods.toml")

        doLast(RewriteFabricMetadataAction(sc.current.parsed >= "26.1"))
        if (sc.current.parsed < "1.21.2") doLast(RewriteLegacyRecipeIngredientsAction(modId))
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}