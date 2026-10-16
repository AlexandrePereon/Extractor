package de.snowii.extractor.extractors

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import de.snowii.extractor.Extractor
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.MinecraftServer


class Particles : Extractor.Extractor {
    override fun fileName(): String {
        return "particles.json"
    }

    override fun extract(server: MinecraftServer): JsonElement {
        val particlesJson = JsonArray()
        for (particle in BuiltInRegistries.PARTICLE_TYPE) {
            // As block names: vanilla keeps the bare path, any other namespace is kept.
            val id = BuiltInRegistries.PARTICLE_TYPE.getKey(particle)!!
            particlesJson.add(if (id.namespace == "minecraft") id.path else id.toString())
        }

        return particlesJson
    }
}
