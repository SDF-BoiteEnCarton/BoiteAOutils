package me.clcondorcet.boiteaoutils

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import me.clcondorcet.boiteaoutils.commands.BoiteCommand
import me.clcondorcet.boiteaoutils.components.registerComponentTypes
import me.clcondorcet.boiteaoutils.systems.HolographicContainerQuantityCounterInitializer
import me.clcondorcet.boiteaoutils.interactions.Distribute
import me.clcondorcet.boiteaoutils.interactions.RandomRotation
import me.clcondorcet.boiteaoutils.packets.HidingCardsPacketHandler
import me.clcondorcet.boiteaoutils.systems.HolographicContainerQuantityCounterSystem

class BoiteAOutils(init: JavaPluginInit) : JavaPlugin(init) {

    companion object {
        lateinit var plugin: JavaPlugin
    }

    init {
        plugin = this
    }

    override fun setup() {
        commandRegistry.registerCommand(BoiteCommand("boite", "Commandes de la boite à outils !"))
        getCodecRegistry(Interaction.CODEC)
            .register("boiteaoutils_distribute", Distribute::class.java, Distribute.CODEC)
        getCodecRegistry(Interaction.CODEC)
            .register("boiteaoutils_randomRotation", RandomRotation::class.java, RandomRotation.CODEC)
        HidingCardsPacketHandler().registerPacketCounters()
        registerComponentTypes(chunkStoreRegistry, entityStoreRegistry)
    }

    override fun start() {
        registerSystems()
    }

    fun registerSystems() {
        chunkStoreRegistry.registerSystem(HolographicContainerQuantityCounterInitializer())
        chunkStoreRegistry.registerSystem(HolographicContainerQuantityCounterSystem())
    }
}