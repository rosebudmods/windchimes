import me.modmuss50.mpp.ReleaseType

plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

stonecutter active "1.21-fabric"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    properties {
        tags(version, loader)
    }

    constants {
        match(loader, "fabric", "neoforge")
    }

    swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    constants["release"] = true
    dependencies["fapi"] = properties.getOrNull<String>("deps.fabric_api") ?: "0"

    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
            replace("net.minecraft.client.renderer.RenderType", "net.minecraft.client.renderer.rendertype.RenderTypes")
            replace("RenderType.entityCutout", "RenderTypes.entityCutout")
        }

        string(current.parsed >= "1.21.5") {
            replace("level.isDay()", "level.isBrightOutside()")
        }

        string(current.parsed >= "1.21.9") {
            replace("level.isClientSide", "level.isClientSide()")
            replace(".noCollission()", ".noCollision()")
            replace("FMLEnvironment.dist", "FMLEnvironment.getDist()")
        }

        string(current.parsed >= "26.1") {
            replace("level.random", "level.getRandom()")
            replace("renderer.state.CameraRenderState", "renderer.state.level.CameraRenderState")
        }
    }
}

val versionTargets = stonecutter.tree.nodes.map { node ->
    node.metadata.version to stonecutter.properties.raw(node.metadata.version, "mod", "mc_releases").asList().map { it.toString() }
}.distinctBy { it.first }

val modId = property("mod.id") as String
val modName = property("mod.name") as String
val modVersion = property("mod.version") as String
val loaders = listOf("fabric", "neoforge")
val publishCurseForge = providers.gradleProperty("publishCurseForge").map(String::toBoolean).orElse(true).get()
val publishModrinth = providers.gradleProperty("publishModrinth").map(String::toBoolean).orElse(true).get()
val artifactDirectory = layout.buildDirectory.dir("libs/$modVersion")

fun artifactVersion(version: String) = "$modVersion+$version"
fun artifactFile(loader: String, version: String) = "$modId-$loader-${artifactVersion(version)}.jar"
fun publishTaskSuffix(loader: String, version: String) = "$loader${version.replace('.', '_')}".replaceFirstChar(Char::uppercase)
fun artifactProvider(loader: String, version: String) = artifactDirectory.map { it.file(artifactFile(loader, version)) }

val buildAndCollect = tasks.register("buildAndCollect") {
    group = "build"
    description = "Builds all mod jars and collects them in `build/libs/$modVersion/`."
}

gradle.projectsEvaluated {
    buildAndCollect.configure {
        dependsOn(subprojects.map { it.tasks.named("buildAndCollect") })
    }
}

publishMods {
    changelog.set(providers.environmentVariable("CHANGELOG"))
    version.set(modVersion)
    displayName.set("$modName $modVersion")
    type.set(ReleaseType.STABLE)
    dryRun.set(providers.gradleProperty("publishDryRun").map(String::toBoolean).orElse(false))

    val curseForgeOptions = curseforgeOptions {
        projectId.set("1588208")
        accessToken.set(providers.environmentVariable("CURSEFORGE_API_KEY"))
        client.set(true)
        server.set(true)
    }

    val modrinthOptions = modrinthOptions {
        projectId.set("I6BSgJNV")
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
    }

    versionTargets.forEach { (mcVersion, mcReleases) ->
        loaders.forEach { loader ->
            val targetVersion = artifactVersion(mcVersion)
            val artifact = artifactProvider(loader, mcVersion)
            val taskSuffix = publishTaskSuffix(loader, mcVersion)

            if (publishCurseForge) {
                curseforge("curseforge$taskSuffix") {
                    from(curseForgeOptions)
                    file.set(artifact)
                    version.set(targetVersion)
                    displayName.set("$modName $targetVersion")
                    modLoaders.add(loader)
                    minecraftVersions.addAll(mcReleases)
                    javaVersions.add(if (mcVersion.startsWith("1.")) JavaVersion.VERSION_21 else JavaVersion.VERSION_25)
                    if (loader == "fabric") requires("fabric-api")
                }
            }

            if (publishModrinth) {
                modrinth("modrinth$taskSuffix") {
                    from(modrinthOptions)
                    file.set(artifact)
                    version.set(targetVersion)
                    displayName.set("$modName $targetVersion")
                    modLoaders.add(loader)
                    minecraftVersions.addAll(mcReleases)
                    if (loader == "fabric") requires("fabric-api")
                }
            }
        }
    }

    github {
        repository.set("rosebudmods/windchimes")
        accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
        commitish.set(providers.environmentVariable("GITHUB_REF_NAME").orElse("fresh"))
        tagName.set("v$modVersion")
        file.set(artifactProvider(loaders.first(), versionTargets.first().first))
        additionalFiles.from(versionTargets.flatMap { (mcVersion, _) ->
            loaders.map { loader -> artifactProvider(loader, mcVersion) }
        }.drop(1))
    }
}