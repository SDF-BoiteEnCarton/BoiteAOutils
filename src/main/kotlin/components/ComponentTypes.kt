package io.boiteencarton.boiteaoutils.components

import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

data class ComponentTypes(
    val holographicContainerQuantityCounter: ComponentType<ChunkStore, HolographicContainerQuantityCounter>,
    val blockRandomReplace: ComponentType<ChunkStore, BlockRandomReplace>,
    val keepItemContainer: ComponentType<ChunkStore, KeepItemContainer>,
    val containerFilterTag: ComponentType<ChunkStore, ContainerFilterTag>,
)

lateinit var componentTypes: ComponentTypes

fun registerComponentTypes(chunkStoreRegistry: ComponentRegistryProxy<ChunkStore>, entityStoreRegistry: ComponentRegistryProxy<EntityStore>) {
    componentTypes = ComponentTypes(
        holographicContainerQuantityCounter = chunkStoreRegistry.registerComponent(HolographicContainerQuantityCounter::class.java, "HolographicContainerQuantityCounter", HolographicContainerQuantityCounter.CODEC),
        blockRandomReplace                  = chunkStoreRegistry.registerComponent(BlockRandomReplace::class.java, "BlockRandomReplace", BlockRandomReplace.CODEC),
        keepItemContainer                   = chunkStoreRegistry.registerComponent(KeepItemContainer::class.java, "KeepItemContainer", KeepItemContainer.CODEC),
        containerFilterTag                  = chunkStoreRegistry.registerComponent(ContainerFilterTag::class.java, "ContainerFilterTag", ContainerFilterTag.CODEC),
    )
}