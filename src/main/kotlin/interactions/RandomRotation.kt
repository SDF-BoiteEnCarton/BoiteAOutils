package me.clcondorcet.boiteaoutils.interactions

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
import me.clcondorcet.boiteaoutils.utils.*
import me.clcondorcet.boiteaoutils.utils.interactions.CheckFalse
import me.clcondorcet.boiteaoutils.utils.interactions.KotlinSimpleInstantInteraction
import me.clcondorcet.boiteaoutils.utils.interactions.KotlinSimpleInstantInteractionRan


class RandomRotation : KotlinSimpleInstantInteraction() {

    val LOGGER: HytaleLogger = HytaleLogger.forEnclosingClass()

    companion object {
        val CODEC: BuilderCodec<RandomRotation?> = BuilderCodec.builder(
            RandomRotation::class.java, { RandomRotation() }, SimpleInstantInteraction.CODEC
        ).build()
    }

    // Valid rotations for the top face of the block to be different each time.
    val possibleRotations = listOf(0, 4, 8, 12, 16, 24)

    override var interaction: KotlinSimpleInstantInteractionRan.() -> Unit = {
        val chunk: WorldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z)) ?: throw CheckFalse()
        val blockType = chunk.getBlockType(targetBlock.toVector3i()) ?: throw CheckFalse()

        chunk.setBlock(targetBlock.x,
            targetBlock.y,
            targetBlock.z,
            chunk.getBlock(targetBlock.toVector3i()),
            blockType,
            possibleRotations.random(),
            chunk.getFiller(targetBlock.x, targetBlock.y, targetBlock.z),
            chunk.getRotationIndex(targetBlock.x, targetBlock.y, targetBlock.z)
        )
    }
}
