package windchimes.gradle

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.File

fun rewriteLegacyRecipeIngredients(resourcesDir: File, modId: String) {
    val recipeDir = resourcesDir.resolve("data/$modId/recipe")
    recipeDir.listFiles { file -> file.extension == "json" }?.forEach { recipe ->
        @Suppress("UNCHECKED_CAST")
        val data = JsonSlurper().parse(recipe) as? MutableMap<String, Any?> ?: return@forEach

        @Suppress("UNCHECKED_CAST")
        val key = data["key"] as? MutableMap<String, Any?> ?: return@forEach
        key.replaceAll { _, value ->
            when (value) {
                is String -> if (value.startsWith("#")) mapOf("tag" to value.drop(1)) else mapOf("item" to value)
                else -> value
            }
        }
        recipe.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(data)))
    }
}