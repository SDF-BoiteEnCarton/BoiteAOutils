package io.boiteencarton.boiteaoutils.events.ecs

import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.EntityEventSystem
import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.server.core.asset.type.item.config.Item
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.boiteencarton.boiteaoutils.components.componentTypes
import io.boiteencarton.boiteaoutils.utils.ItemContainerStateType
import io.boiteencarton.boiteaoutils.utils.getComponent
import kotlin.collections.contains

class OnContainerFilterTagPlace : EntityEventSystem<EntityStore, PlaceBlockEvent>(PlaceBlockEvent::class.java) {
    override fun handle(
        id: Int,
        archetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        cmdBuffer: CommandBuffer<EntityStore>,
        event: PlaceBlockEvent
    ) {
        val chunk: WorldChunk = cmdBuffer.externalData.world.getChunk(ChunkUtil.indexChunkFromBlock(event.targetBlock.x, event.targetBlock.z)) ?: return

        cmdBuffer.externalData.world.execute {

            println("OnPlace")

            val container = chunk.getBlockComponentEntity(event.targetBlock.x, event.targetBlock.y, event.targetBlock.z)?.getComponent(
                ItemContainerStateType ?: return@execute
            ) ?: return@execute

            println("has container")

            val filterTag = chunk.getBlockComponentEntity(event.targetBlock.x, event.targetBlock.y, event.targetBlock.z)?.getComponent(
                componentTypes.containerFilterTag
            ) ?: return@execute

            println("has filter")

            if (filterTag.allowedTags == null) return@execute

            println("filter non null")

            val filter : SlotFilter = { _, _, _, itemStack ->
                println("FILTER $itemStack")
                if (itemStack == null) false
                else
                    Item.getAssetMap()?.getAsset(itemStack.itemId)?.let { asset ->
                        println("Asset got")
                        var isIn = false
                        for (tagEntry in filterTag.allowedTags!!) {
                            println("searching for ${tagEntry.key} ${tagEntry.value} in asset")
                            if (asset.data.rawTags[tagEntry.key]?.contains(tagEntry.value) == true) {
                                println("Tag kv Found !")
                                isIn = true
                                break
                            }
                        }
                        println("final result: $isIn")
                        isIn
                    } ?: false
            }

            for (slot in 0..<container.itemContainer.capacity) {
                container.itemContainer.setSlotFilter(FilterActionType.ADD, slot.toShort(), filter)
            }

            println("filter placed")
        }
    }

    override fun getQuery(): Query<EntityStore> {
        return Query.any()
    }
}