package io.boiteencarton.boiteaoutils.components

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore

class BlockRandomReplace(
    var replaceList: Array<String> = arrayOf()
) : Component<ChunkStore> {
    override fun clone(): Component<ChunkStore> {
        return BlockRandomReplace(replaceList.clone())
    }

    companion object {
        val CODEC: BuilderCodec<BlockRandomReplace> =
            BuilderCodec.builder(BlockRandomReplace::class.java) { BlockRandomReplace() }
                .append(
                    KeyedCodec("ReplaceList", Codec.STRING_ARRAY),
                    { obj: BlockRandomReplace, v: Array<String> -> obj.replaceList = v },
                    { obj: BlockRandomReplace -> obj.replaceList })
                .add()
                .build()
    }
}