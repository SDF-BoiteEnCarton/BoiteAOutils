package io.boiteencarton.boiteaoutils.events.ecs

import com.hypixel.hytale.component.AddReason
import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Holder
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.EntityEventSystem
import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.boiteencarton.boiteaoutils.components.componentTypes
import io.boiteencarton.boiteaoutils.types.KeptContainer
import io.boiteencarton.boiteaoutils.types.KeptContainerEntry
import io.boiteencarton.boiteaoutils.utils.ItemContainerStateType
import io.boiteencarton.boiteaoutils.utils.getComponent

class OnKeepItemContainerBreak : EntityEventSystem<EntityStore, BreakBlockEvent>(BreakBlockEvent::class.java) {
    override fun handle(
        id: Int,
        archetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        cmdBuffer: CommandBuffer<EntityStore>,
        event: BreakBlockEvent
    ) {
        val chunk: WorldChunk = cmdBuffer.externalData.world.getChunk(ChunkUtil.indexChunkFromBlock(event.targetBlock.x, event.targetBlock.z)) ?: return
        val blockType = chunk.getBlockType(event.targetBlock) ?: return

        chunk.getBlockComponentEntity(event.targetBlock.x, event.targetBlock.y, event.targetBlock.z)?.getComponent(
            componentTypes.keepItemContainer
        ) ?: return


        val container = chunk.getBlockComponentEntity(event.targetBlock.x, event.targetBlock.y, event.targetBlock.z)?.getComponent(
            ItemContainerStateType ?: return
        ) ?: return


        val containerEntries = mutableListOf<KeptContainerEntry>()
        container.itemContainer.forEach { slot, stack -> containerEntries.add(KeptContainerEntry(slot, stack)) }
        val itemStack = blockType.item?.id?.let { ItemStack(it, 1) }?.withMetadata("KeptContainer", KeptContainer.CODEC,
            KeptContainer(containerEntries.toTypedArray())
        ) ?: return

        container.itemContainer.clear() // TODO verify it doesn't delete itemstack itself
        chunk.setBlock(event.targetBlock.x,event.targetBlock.y,event.targetBlock.z, 0)

        val pickupItemHolder: Holder<EntityStore> = ItemComponent.generateItemDrop(
            cmdBuffer.externalData.world.entityStore.store,
            itemStack,
            event.targetBlock.toVector3d().add(Vector3d(0.5, 0.0, 0.5)),
            Vector3f(0F, 0F, 0F), 0F, 0F, 0F
        ) ?: return

        cmdBuffer.addEntity(pickupItemHolder, AddReason.SPAWN)
    }

    override fun getQuery(): Query<EntityStore> {
        return Query.any()
    }
}