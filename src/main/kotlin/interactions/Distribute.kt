package me.clcondorcet.boiteaoutils.interactions

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.math.vector.Vector3i
import com.hypixel.hytale.protocol.InteractionState
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent
import com.hypixel.hytale.server.core.entity.InteractionContext
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer
import com.hypixel.hytale.server.core.modules.entity.EntityModule
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction
import com.hypixel.hytale.server.core.universe.world.SoundUtil
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import java.util.function.BiConsumer

class Distribute : SimpleInstantInteraction() {

    val LOGGER: HytaleLogger = HytaleLogger.forEnclosingClass()

    protected override fun firstRun(
        interactionType: InteractionType,
        interactionContext: InteractionContext,
        cooldownHandler: CooldownHandler
    ) {
        val commandBuffer = interactionContext.commandBuffer
        if (commandBuffer == null) {
            interactionContext.state.state = InteractionState.Failed
            LOGGER.atInfo().log("CommandBuffer is null")
            return
        }

        val world = commandBuffer.getExternalData().world
        val store: Store<EntityStore?>? =
            commandBuffer.getExternalData().store // just to show how to get the store if needed
        val playerRef: Ref<EntityStore?> = interactionContext.entity
        val player: Player? = commandBuffer.getComponent(playerRef, Player.getComponentType())
        if (player == null) {
            interactionContext.state.state = InteractionState.Failed
            LOGGER.atInfo().log("Player is null")
            return
        }

//        val itemStack = interactionContext.heldItem
//        if (itemStack == null) {
//            interactionContext.state.state = InteractionState.Failed
//            LOGGER.atInfo().log("ItemStack is null")
//            return
//        }

        //player.sendMessage(Message.raw("You want to distribute right ?"))

        val blockPos = interactionContext.targetBlock
        if (blockPos == null) {
            player.sendMessage(Message.raw("Target is null"))
            LOGGER.atInfo().log("Target is null")
            return
        }

        val underBlockPos = Vector3i(blockPos.x, blockPos.y - 1, blockPos.z)

        val itemContainerStateType: ComponentType<ChunkStore, ItemContainerState>? = BlockStateModule.get().getComponentType(ItemContainerState::class.java)
        if (itemContainerStateType == null) {
            player.sendMessage(Message.raw("itemContainerStateType not found"))
            LOGGER.atInfo().log("itemContainerStateType not found")
            return
        }

        val query : Query<ChunkStore> = Query.and(itemContainerStateType) as Query<ChunkStore>
        var container : ItemContainerState? = null
        world.chunkStore.store.forEachChunk(query,
            BiConsumer { archetypeChunk, _ ->
                if (container == null)
                    for (i in 0..<archetypeChunk.size()) {
                        val ref: Ref<ChunkStore?> = archetypeChunk.getReferenceTo(i)

                        val itemContainer : ItemContainerState? = archetypeChunk.getComponent(i, itemContainerStateType)
                        if (itemContainer != null && itemContainer.blockPosition == underBlockPos) {
                            container = itemContainer
                            break
                        }
                    }
            })

        if (container == null) {
            player.sendMessage(Message.raw("container not found"))
            LOGGER.atInfo().log("container not found")
            return
        }

        //player.sendMessage(Message.raw("container found !"))

        val simpleItemContainer = container.itemContainer as SimpleItemContainer

        //simpleItemContainer.sortItems(SortType.NAME)

        if(simpleItemContainer.isEmpty) {
            player.sendMessage(Message.raw("Le coffre est vide !"))
            return
        }

        val items = mutableListOf<ItemStack>()
        simpleItemContainer.forEach { _, stack ->
            items.add(stack)
        }

        var randomItem = items.random()
        randomItem = randomItem.withQuantity(1)!!

        var transferred = false
        if(player.inventory.hotbar.canAddItemStack(randomItem)) {
            simpleItemContainer.removeItemStack(randomItem)
            player.inventory.hotbar.addItemStack(randomItem)
            transferred = true
        }

        if(!transferred && player.inventory.storage.canAddItemStack(randomItem)) {
            simpleItemContainer.removeItemStack(randomItem)
            player.inventory.storage.addItemStack(randomItem)
            transferred = true
        }

        if(!transferred) {
            player.sendMessage(Message.raw("Tu es full !"))
            return
        }

        val index: Int = SoundEvent.getAssetMap().getIndex("SFX_Cactus_Large_Hit")
        world.execute {
            if (store == null) return@execute
            val transform: TransformComponent? =
                store.getComponent(playerRef, EntityModule.get().transformComponentType)
            if (transform == null) return@execute
            SoundUtil.playSoundEvent3d(playerRef, index, transform.position, store)
        }
    }

    companion object {
        val CODEC: BuilderCodec<Distribute?> = BuilderCodec.builder(
            Distribute::class.java, { Distribute() }, SimpleInstantInteraction.CODEC
        ).build()
    }
}
