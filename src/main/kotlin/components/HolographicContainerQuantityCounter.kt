package io.boiteencarton.boiteaoutils.components

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.math.vector.Vector3i
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
import java.util.UUID

class HolographicContainerQuantityCounter(
    var hologramEntityUUID: UUID? = null,
    var containerOffsetPosition: Vector3i = Vector3i(0, 0, 0),
) : Component<ChunkStore> {
    override fun clone(): Component<ChunkStore> {
        return HolographicContainerQuantityCounter(hologramEntityUUID, containerOffsetPosition)
    }

    companion object {
        val vector3iCodec: BuilderCodec<Vector3i> =
            BuilderCodec.builder(Vector3i::class.java) { Vector3i() }
            .append(
                KeyedCodec("X", Codec.INTEGER),
                { bp: Vector3i, v: Int -> bp.x = v },
                { bp: Vector3i -> bp.x })
            .add()
            .append(
                KeyedCodec("Y", Codec.INTEGER),
                { bp: Vector3i, v: Int -> bp.y = v },
                { bp: Vector3i -> bp.y })
            .add()
            .append(
                KeyedCodec("Z", Codec.INTEGER),
                { bp: Vector3i, v: Int -> bp.z = v },
                { bp: Vector3i -> bp.z })
            .add()
            .build()

        val CODEC: BuilderCodec<HolographicContainerQuantityCounter> =
            BuilderCodec.builder(HolographicContainerQuantityCounter::class.java) { HolographicContainerQuantityCounter() }
            .append(
                KeyedCodec("ContainerOffsetPosition", vector3iCodec),
                { c: HolographicContainerQuantityCounter, v: Vector3i -> c.containerOffsetPosition = v },
                { c: HolographicContainerQuantityCounter -> c.containerOffsetPosition })
            .add()
            .append(
                KeyedCodec("HologramEntityUUID", Codec.UUID_BINARY),
                { c: HolographicContainerQuantityCounter, v: UUID? -> v?.let { c.hologramEntityUUID = v } ?: run { c.hologramEntityUUID = null }},
                { c: HolographicContainerQuantityCounter -> c.hologramEntityUUID })
            .add()
            .build()
    }
}