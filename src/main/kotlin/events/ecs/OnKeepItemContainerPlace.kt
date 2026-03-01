package io.boiteencarton.boiteaoutils.events.ecs

import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.EntityEventSystem
import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.boiteencarton.boiteaoutils.types.KeptContainer
import io.boiteencarton.boiteaoutils.utils.ItemContainerStateType
import io.boiteencarton.boiteaoutils.utils.getComponent

class OnKeepItemContainerPlace : EntityEventSystem<EntityStore, PlaceBlockEvent>(PlaceBlockEvent::class.java) {
    override fun handle(
        id: Int,
        archetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        cmdBuffer: CommandBuffer<EntityStore>,
        event: PlaceBlockEvent
    ) {
        val item = event.itemInHand ?: return

        val chunk: WorldChunk = cmdBuffer.externalData.world.getChunk(ChunkUtil.indexChunkFromBlock(event.targetBlock.x, event.targetBlock.z)) ?: return

        val keptContainer = item.getFromMetadataOrNull("KeptContainer", KeptContainer.CODEC) ?: return

        cmdBuffer.externalData.world.execute {
            val container = chunk.getBlockComponentEntity(event.targetBlock.x, event.targetBlock.y, event.targetBlock.z)?.getComponent(
                ItemContainerStateType ?: return@execute
            ) ?: return@execute

            keptContainer.items!!.forEach {
                if (it.slot != null && it.itemStack != null) {
                    container.itemContainer.setItemStackForSlot(it.slot!!, it.itemStack!!)
                }
            }
        }
    }

    override fun getQuery(): Query<EntityStore> {
        return Query.any()
    }
}