package io.boiteencarton.boiteaoutils.components

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore

class BlockRotationKeeper : Component<ChunkStore> {
    override fun clone(): Component<ChunkStore> {
        return BlockRotationKeeper()
    }

    companion object {
        val CODEC: BuilderCodec<BlockRotationKeeper> =
            BuilderCodec.builder(BlockRotationKeeper::class.java) { BlockRotationKeeper() }
                .build()
    }
}