package io.boiteencarton.boiteaoutils.components

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.codec.codecs.array.ArrayCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore

class ContainerFilterTag(
    var allowedTags : Array<TagEntry>? = null,
) : Component<ChunkStore> {
    override fun clone(): Component<ChunkStore> {
        return ContainerFilterTag(allowedTags)
    }

    companion object {
        val CODEC: BuilderCodec<ContainerFilterTag> =
            BuilderCodec.builder(ContainerFilterTag::class.java) { ContainerFilterTag() }
            .append(
                KeyedCodec("AllowedTags",
                    ArrayCodec(TagEntry.CODEC) { size: Int -> arrayOfNulls<TagEntry>(size) }),
                { c: ContainerFilterTag, v: Array<TagEntry> -> c.allowedTags = v },
                { c: ContainerFilterTag -> c.allowedTags })
            .add()
            .build()
    }
}

data class TagEntry(
    var key : String? = null,
    var value : String? = null,
) {
    companion object {
        val CODEC: BuilderCodec<TagEntry> =
            BuilderCodec.builder(TagEntry::class.java) { TagEntry() }
            .append(
                KeyedCodec("Key", Codec.STRING),
                { c: TagEntry, v: String -> c.key = v },
                { c: TagEntry -> c.key })
            .add()
            .append(
                KeyedCodec("Value", Codec.STRING),
                { c: TagEntry, v: String -> c.value = v },
                { c: TagEntry -> c.value })
            .add()
            .build()
    }
}