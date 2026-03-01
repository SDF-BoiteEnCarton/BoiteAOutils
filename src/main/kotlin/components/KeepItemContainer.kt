package io.boiteencarton.boiteaoutils.components

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore

class KeepItemContainer : Component<ChunkStore> {
    override fun clone(): Component<ChunkStore> {
        return KeepItemContainer()
    }

    companion object {
        val CODEC: BuilderCodec<KeepItemContainer> =
            BuilderCodec.builder(KeepItemContainer::class.java) { KeepItemContainer() }.build()
    }
}
