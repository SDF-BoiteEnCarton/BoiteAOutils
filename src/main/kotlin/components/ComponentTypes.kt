package io.boiteencarton.boiteaoutils.components

import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

data class ComponentTypes(
    val holographicContainerQuantityCounter: ComponentType<ChunkStore, HolographicContainerQuantityCounter>
)

lateinit var componentTypes: ComponentTypes

fun registerComponentTypes(chunkStoreRegistry: ComponentRegistryProxy<ChunkStore>, entityStoreRegistry: ComponentRegistryProxy<EntityStore>) {
    componentTypes = ComponentTypes(
        chunkStoreRegistry.registerComponent(HolographicContainerQuantityCounter::class.java, "HolographicContainerQuantityCounter", HolographicContainerQuantityCounter.CODEC)
    )
}