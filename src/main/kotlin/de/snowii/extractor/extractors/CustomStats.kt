package de.snowii.extractor.extractors

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import de.snowii.extractor.Extractor
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.MinecraftServer

class CustomStats : Extractor.Extractor {
    override fun fileName(): String {
        return "custom_stats.json"
    }

    override fun extract(server: MinecraftServer): JsonElement {
        val statsObject = JsonArray()

        for (stat in BuiltInRegistries.CUSTOM_STAT) {
            // As block names: vanilla keeps the bare path, any other namespace is kept.
            statsObject.add(if (stat.namespace == "minecraft") stat.path else stat.toString())
        }

        return statsObject
    }
}