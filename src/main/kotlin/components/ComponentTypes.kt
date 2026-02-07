package io.boiteencarton.boiteaoutils.components

import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

data class ComponentTypes(
    val holographicContainerQuantityCounter: ComponentType<ChunkStore, HolographicContainerQuantityCounter>,
    val blockRotationKeeper: ComponentType<ChunkStore, BlockRotationKeeper>,
)

lateinit var componentTypes: ComponentTypes

fun registerComponentTypes(chunkStoreRegistry: ComponentRegistryProxy<ChunkStore>, entityStoreRegistry: ComponentRegistryProxy<EntityStore>) {
    componentTypes = ComponentTypes(
        holographicContainerQuantityCounter = chunkStoreRegistry.registerComponent(HolographicContainerQuantityCounter::class.java, "HolographicContainerQuantityCounter", HolographicContainerQuantityCounter.CODEC),
        blockRotationKeeper =                 chunkStoreRegistry.registerComponent(BlockRotationKeeper::class.java, "BlockRotationKeeper", BlockRotationKeeper.CODEC),
    )
}