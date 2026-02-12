package io.boiteencarton.boiteaoutils.interactions

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
import io.boiteencarton.boiteaoutils.components.componentTypes
import io.boiteencarton.boiteaoutils.utils.*
import io.boiteencarton.boiteaoutils.utils.interactions.CheckFalse
import io.boiteencarton.boiteaoutils.utils.interactions.KotlinSimpleInstantInteraction
import io.boiteencarton.boiteaoutils.utils.interactions.KotlinSimpleInstantInteractionRan

class RandomReplace : KotlinSimpleInstantInteraction() {

    val LOGGER: HytaleLogger = HytaleLogger.forEnclosingClass()

    companion object {
        val CODEC: BuilderCodec<RandomReplace?> = BuilderCodec.builder(
            RandomReplace::class.java, { RandomReplace() }, SimpleInstantInteraction.CODEC
        ).build()
    }

    override var interaction: KotlinSimpleInstantInteractionRan.() -> Unit = {
        val chunk: WorldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z)) ?: throw CheckFalse()
        val comp = world.getBlockRef(targetBlock)?.getComponent(componentTypes.blockRandomReplace) ?: throw CheckFalse()
        if (comp.replaceList.isEmpty()) throw CheckFalse()
        val random = comp.replaceList.random()
        world.execute {
            chunk.setBlock(targetBlock.x,
                targetBlock.y,
                targetBlock.z,
                random
            )
        }
    }
}
