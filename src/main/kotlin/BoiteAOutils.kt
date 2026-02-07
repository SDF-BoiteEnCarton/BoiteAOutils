package io.boiteencarton.boiteaoutils

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import io.boiteencarton.boiteaoutils.commands.BoiteCommand
import io.boiteencarton.boiteaoutils.components.registerComponentTypes
import io.boiteencarton.boiteaoutils.systems.HolographicContainerQuantityCounterInitializer
import io.boiteencarton.boiteaoutils.interactions.Distribute
import io.boiteencarton.boiteaoutils.interactions.RandomRotation
import io.boiteencarton.boiteaoutils.packets.HidingCardsPacketHandler
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