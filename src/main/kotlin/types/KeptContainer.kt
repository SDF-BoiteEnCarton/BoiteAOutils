package io.boiteencarton.boiteaoutils.types

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.codec.codecs.array.ArrayCodec
import com.hypixel.hytale.server.core.inventory.ItemStack

data class KeptContainer (
    var items: Array<KeptContainerEntry>? = null,
) {
    companion object {
        val CODEC : BuilderCodec<KeptContainer> =
            BuilderCodec.builder(KeptContainer::class.java) { KeptContainer() }
                .append(
                    KeyedCodec("Items",
                        ArrayCodec(KeptContainerEntry.CODEC) { size: Int -> arrayOfNulls<KeptContainerEntry>(size) }),
                    { c: KeptContainer, v: Array<KeptContainerEntry> -> c.items = v },
                    { c: KeptContainer -> c.items })
                .add()
                .build()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeptContainer

        if (!items.contentEquals(other.items)) return false

        return true
    }

    override fun hashCode(): Int {
        return items?.contentHashCode() ?: 0
    }
}

data class KeptContainerEntry(
    var slot: Short? = null,
    var itemStack: ItemStack? = null,
) {
    companion object {
        val CODEC : BuilderCodec<KeptContainerEntry> =
            BuilderCodec.builder(KeptContainerEntry::class.java) { KeptContainerEntry() }
                .append(
                    KeyedCodec("Slot", Codec.SHORT),
                    { c: KeptContainerEntry, v: Short -> c.slot = v },
                    { c: KeptContainerEntry -> c.slot })
                .add()
                .append(
                    KeyedCodec("ItemStack", ItemStack.CODEC),
                    { c: KeptContainerEntry, v: ItemStack -> c.itemStack = v },
                    { c: KeptContainerEntry -> c.itemStack })
                .add()
                .build()
    }
}
