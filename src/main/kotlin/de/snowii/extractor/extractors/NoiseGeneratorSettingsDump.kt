package de.snowii.extractor.extractors

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import de.snowii.extractor.Extractor
import net.minecraft.core.registries.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings

// Every noise settings entry as the server holds it once started, in the
// datapack format (`worldgen/noise_settings/<id>.json`). A mod may change them
// in code: Biolith inserts mods' surface rules into the overworld's material
// rule at server start, so the effective rules exist only here.
class NoiseGeneratorSettingsDump : Extractor.Extractor {
    override fun fileName(): String {
        return "noise_settings.json"
    }

    override fun extract(server: MinecraftServer): JsonElement {
        val out = JsonObject()
        val ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE)
        val registry = server.registryAccess().lookupOrThrow(Registries.NOISE_SETTINGS)
        for (key in registry.registryKeySet().sortedBy { it.identifier().toString() }) {
            val settings = registry.getValueOrThrow(key)
            out.add(key.identifier().toString(), NoiseGeneratorSettings.DIRECT_CODEC.encodeStart(ops, settings).getOrThrow())
        }
        return out
    }
}
