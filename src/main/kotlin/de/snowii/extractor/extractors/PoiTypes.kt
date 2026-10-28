package de.snowii.extractor.extractors

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import de.snowii.extractor.Extractor
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.MinecraftServer

// Point of interest types in raw-id order. A mod adding one makes Fabric sync
// the registry, so a server dictating ids needs the full list.
class PoiTypes : Extractor.Extractor {
    override fun fileName(): String {
        return "poi_types.json"
    }

    override fun extract(server: MinecraftServer): JsonElement {
        val poiTypesJson = JsonArray()
        for (poiType in BuiltInRegistries.POINT_OF_INTEREST_TYPE) {
            // As block names: vanilla keeps the bare path, any other namespace is kept.
            val poiTypeId = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getKey(poiType)!!
            poiTypesJson.add(if (poiTypeId.namespace == "minecraft") poiTypeId.path else poiTypeId.toString())
        }

        return poiTypesJson
    }
}
