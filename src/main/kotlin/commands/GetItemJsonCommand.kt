package io.boiteencarton.boiteaoutils.commands

import com.hypixel.hytale.codec.EmptyExtraInfo
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

class GetItemJsonCommand(name: String, description: String) : AbstractPlayerCommand(name, description) {

    val LOGGER = HytaleLogger.forEnclosingClass()

    override fun execute(context: CommandContext, store: Store<EntityStore>, ref: Ref<EntityStore>, playerRef: PlayerRef, world: World) {
        val playerComponent = store.getComponent(ref, Player.getComponentType())

        checkNotNull(playerComponent)

        val inventory = playerComponent.inventory
        val activeHotbarItem = inventory.activeHotbarItem

        val json = ItemStack.CODEC.encode(activeHotbarItem, EmptyExtraInfo.EMPTY).toJson()
        context.sendMessage(Message.raw(json))
        LOGGER.atInfo().log("Item in hand of ${playerRef.username} is: $json")
    }
}