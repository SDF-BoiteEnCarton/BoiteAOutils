package io.boiteencarton.boiteaoutils

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import io.boiteencarton.boiteaoutils.commands.BoiteCommand
import io.boiteencarton.boiteaoutils.commands.GetItemJsonCommand
import io.boiteencarton.boiteaoutils.components.registerComponentTypes
import io.boiteencarton.boiteaoutils.events.ecs.OnContainerFilterTagPlace
import io.boiteencarton.boiteaoutils.events.ecs.OnKeepItemContainerBreak
import io.boiteencarton.boiteaoutils.events.ecs.OnKeepItemContainerPlace
import io.boiteencarton.boiteaoutils.interactions.Distribute
import io.boiteencarton.boiteaoutils.interactions.RandomReplace
import io.boiteencarton.boiteaoutils.packets.HidingCardsPacketHandler
import io.boiteencarton.boiteaoutils.systems.HolographicContainerQuantityCounterInitializer
import io.boiteencarton.boiteaoutils.systems.HolographicContainerQuantityCounterSystem

class BoiteAOutils(init: JavaPluginInit) : JavaPlugin(init) {

    companion object {
        lateinit var plugin: JavaPlugin
    }

    init {
        plugin = this
    }

    override fun setup() {
        commandRegistry.registerCommand(BoiteCommand("boite", "Commandes de la boite à outils !"))
        commandRegistry.registerCommand(GetItemJsonCommand("getitemjson", "Get the json from item in hand"))

        getCodecRegistry(Interaction.CODEC)
            .register("boiteaoutils_distribute", Distribute::class.java, Distribute.CODEC)
        getCodecRegistry(Interaction.CODEC)
            .register("boiteaoutils_RandomReplace", RandomReplace::class.java, RandomReplace.CODEC)
        HidingCardsPacketHandler().registerPacketCounters()
        registerComponentTypes(chunkStoreRegistry, entityStoreRegistry)
    }

    override fun start() {
        registerSystems()
    }

    fun registerSystems() {
        chunkStoreRegistry.registerSystem(HolographicContainerQuantityCounterInitializer())
        chunkStoreRegistry.registerSystem(HolographicContainerQuantityCounterSystem())

        // Register events
        entityStoreRegistry.registerSystem(OnKeepItemContainerBreak())
        entityStoreRegistry.registerSystem(OnKeepItemContainerPlace())
        entityStoreRegistry.registerSystem(OnContainerFilterTagPlace())
    }
}