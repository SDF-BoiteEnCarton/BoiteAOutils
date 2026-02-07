package io.boiteencarton.boiteaoutils.events.ecs

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.EmptyExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.*
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.EntityEventSystem
import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.boiteencarton.boiteaoutils.components.componentTypes
import io.boiteencarton.boiteaoutils.utils.getComponent

data class KeptRotation (
    var rotation: Int = 0,
) {
    companion object {
        val CODEC : BuilderCodec<KeptRotation> =
        BuilderCodec.builder(KeptRotation::class.java) { KeptRotation() }
        .append(
            KeyedCodec("Rotation", Codec.INTEGER),
        { c: KeptRotation, v: Int -> c.rotation = v },
        { c: KeptRotation -> c.rotation })
        .add()
        .build()
    }
}

class BlockRotationKeeperBreakEvent : EntityEventSystem<EntityStore, BreakBlockEvent>(BreakBlockEvent::class.java) {
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
            componentTypes.blockRotationKeeper
        ) ?: return


        val rotation = chunk.getRotationIndex(event.targetBlock.x, event.targetBlock.y, event.targetBlock.z)

        chunk.setBlock(event.targetBlock.x,event.targetBlock.y,event.targetBlock.z, 0)

        val itemStack = blockType.item?.id?.let { ItemStack(it, 1) }?.withMetadata("KeptRotation", KeptRotation.CODEC, KeptRotation(rotation)) ?: return

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

class BlockRotationKeeperPlaceEvent : EntityEventSystem<EntityStore, PlaceBlockEvent>(PlaceBlockEvent::class.java) {
    override fun handle(
        id: Int,
        archetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        cmdBuffer: CommandBuffer<EntityStore>,
        event: PlaceBlockEvent
    ) {
        val item = event.itemInHand ?: return

        val chunk: WorldChunk = cmdBuffer.externalData.world.getChunk(ChunkUtil.indexChunkFromBlock(event.targetBlock.x, event.targetBlock.z)) ?: return

        val rotation = item.metadata?.get("KeptRotation")?.let { KeptRotation.CODEC.decode(it, EmptyExtraInfo.EMPTY) } ?: return

        item.blockKey?.let { BlockType.fromString(it) }?.let {
            cmdBuffer.externalData.world.execute {
                chunk.setBlock(event.targetBlock.x,
                    event.targetBlock.y,
                    event.targetBlock.z,
                    chunk.getBlock(event.targetBlock),
                    it,
                    rotation.rotation,
                    chunk.getFiller(event.targetBlock.x, event.targetBlock.y, event.targetBlock.z),
                    chunk.getRotationIndex(event.targetBlock.x, event.targetBlock.y, event.targetBlock.z)
                )
            }
        }
    }

    override fun getQuery(): Query<EntityStore> {
        return Query.any()
    }
}