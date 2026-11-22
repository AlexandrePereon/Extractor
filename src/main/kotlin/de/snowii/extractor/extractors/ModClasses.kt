package de.snowii.extractor.extractors

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import de.snowii.extractor.Extractor
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.entity.BlockEntityType

/**
 * The Java class behind every registry entry outside the `minecraft`
 * namespace, with its superclasses: what a port tool needs to tell a plain
 * vanilla subclass from a class with logic of its own, whatever way the mod
 * registered it (a static field, a loader wrapper, a loop over an enum).
 *
 * `{"<registry>": {"<entry>": {"class": [...], "instance": [...]}}}`: `class`
 * is the registered object's class, then its superclasses up to (without)
 * `java.lang.Object`. For block entity and entity types, whose registered
 * object is the vanilla `*Type`, `instance` is the same for an instance the
 * type creates. Registries without a non-vanilla entry are left out.
 */
class ModClasses : Extractor.Extractor {
    override fun fileName(): String {
        return "mod_classes.json"
    }

    override fun extract(server: MinecraftServer): JsonElement {
        val json = JsonObject()
        for (registry in BuiltInRegistries.REGISTRY) {
            val entries = JsonObject()
            @Suppress("UNCHECKED_CAST")
            val typed = registry as Registry<Any>
            for (value in typed) {
                val id = typed.getKey(value) ?: continue
                if (id.namespace == "minecraft") continue
                val entry = JsonObject()
                entry.add("class", chain(value.javaClass))
                instanceOf(server, value)?.let { entry.add("instance", chain(it)) }
                entries.add(id.toString(), entry)
            }
            if (entries.size() > 0) {
                json.add(registry.key().identifier().toString(), entries)
            }
        }
        return json
    }

    private fun instanceOf(server: MinecraftServer, value: Any): Class<*>? = try {
        when (value) {
            is EntityType<*> -> value.create(server.overworld(), EntitySpawnReason.LOAD)?.javaClass
            is BlockEntityType<*> -> BuiltInRegistries.BLOCK
                .map { it.defaultBlockState() }
                .firstOrNull { value.isValid(it) }
                ?.let { value.create(BlockPos.ZERO, it).javaClass }
            else -> null
        }
    } catch (e: Exception) {
        null
    }

    private fun chain(start: Class<*>): JsonArray {
        val classes = JsonArray()
        var current: Class<*>? = start
        while (current != null && current != Any::class.java) {
            classes.add(current.name)
            current = current.superclass
        }
        return classes
    }
}
