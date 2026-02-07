package io.boiteencarton.boiteaoutils.systems

import com.hypixel.hytale.component.AddReason
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.RemoveReason
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.RefSystem
import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.server.core.modules.block.BlockModule
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
import io.boiteencarton.boiteaoutils.components.componentTypes

class HolographicContainerQuantityCounterInitializer : RefSystem<ChunkStore>() {
    override fun getQuery(): Query<ChunkStore> {
        return Query.and(
            componentTypes.holographicContainerQuantityCounter,
            BlockModule.BlockStateInfo.getComponentType(),
        )
    }

    // Set block as ticking.
    override fun onEntityAdded(
        ref: Ref<ChunkStore>,
        reason: AddReason,
        store: Store<ChunkStore>,
        cmdBuffer: CommandBuffer<ChunkStore>
    ) {
        val info = cmdBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType()) ?: return
        cmdBuffer.getComponent(ref, componentTypes.holographicContainerQuantityCounter) ?: return

        val x = ChunkUtil.xFromBlockInColumn(info.index)
        val y = ChunkUtil.yFromBlockInColumn(info.index)
        val z = ChunkUtil.zFromBlockInColumn(info.index)

        val worldChunk = cmdBuffer.getComponent(info.chunkRef, WorldChunk.getComponentType()) ?: return
        worldChunk.setTicking(x, y, z, true)
    }

    // Removing hologram when unload or deleted.
    override fun onEntityRemove(
        ref: Ref<ChunkStore>,
        removeReason: RemoveReason,
        store: Store<ChunkStore>,
        cmdBuffer: CommandBuffer<ChunkStore>
    ) {
        val holographicContainerSizeCounter = cmdBuffer.getComponent(ref, componentTypes.holographicContainerQuantityCounter) ?: return
        val refUUID = holographicContainerSizeCounter.hologramEntityUUID ?: return
        cmdBuffer.externalData.world.execute {
            val ref = cmdBuffer.externalData.world.entityStore.getRefFromUUID(refUUID) ?: return@execute
            cmdBuffer.externalData.world.entityStore.store.removeEntity(ref, RemoveReason.REMOVE)
        }
    }
}