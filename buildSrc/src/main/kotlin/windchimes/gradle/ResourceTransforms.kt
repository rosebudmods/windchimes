package windchimes.gradle

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File
import java.io.Serializable

class RewriteLegacyRecipeIngredientsAction(private val modId: String) : Action<Task>, Serializable {
    override fun execute(task: Task) = rewriteLegacyRecipeIngredients((task as ProcessResources).destinationDir, modId)
}

class RewriteFabricMetadataAction(private val includeCreativeTabApi: Boolean) : Action<Task>, Serializable {
    override fun execute(task: Task) =
        rewriteFabricMetadata((task as ProcessResources).destinationDir, includeCreativeTabApi)
}

private fun rewriteFabricMetadata(resourcesDir: File, includeCreativeTabApi: Boolean) {
    val fabricJson = resourcesDir.resolve("fabric.mod.json")
    rewriteJsonObject(fabricJson) { data ->
        @Suppress("UNCHECKED_CAST")
        val depends = data["depends"] as? MutableMap<String, Any?> ?: return@rewriteJsonObject
        if (includeCreativeTabApi) {
            depends["fabric-creative-tab-api-v1"] = "*"
            depends.remove("fabric-item-group-api-v1")
        } else {
            depends.remove("fabric-creative-tab-api-v1")
        }
    }
}

private fun rewriteLegacyRecipeIngredients(resourcesDir: File, modId: String) {
    val recipeDir = resourcesDir.resolve("data/$modId/recipe")
    recipeDir.listFiles { file -> file.extension == "json" }?.forEach { recipe ->
        rewriteJsonObject(recipe) { data ->
            @Suppress("UNCHECKED_CAST")
            val key = data["key"] as? MutableMap<String, Any?> ?: return@rewriteJsonObject
            key.replaceAll { _, value ->
                when (value) {
                    is String -> if (value.startsWith("#")) mapOf("tag" to value.drop(1)) else mapOf("item" to value)
                    else -> value
                }
            }
        }
    }
}

private fun rewriteJsonObject(file: File, transform: (MutableMap<String, Any?>) -> Unit) {
    if (!file.isFile) return

    @Suppress("UNCHECKED_CAST")
    val data = JsonSlurper().parse(file) as? MutableMap<String, Any?> ?: return
    transform(data)
    file.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(data)))
}