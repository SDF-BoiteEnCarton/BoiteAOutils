package me.clcondorcet.boiteaoutils.systems

import com.hypixel.hytale.component.*
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.tick.EntityTickingSystem
import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.protocol.BlockPosition
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy
import com.hypixel.hytale.server.core.entity.UUIDComponent
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import me.clcondorcet.boiteaoutils.components.HolographicContainerQuantityCounter
import me.clcondorcet.boiteaoutils.components.componentTypes
import me.clcondorcet.boiteaoutils.utils.ItemContainerStateType
import me.clcondorcet.boiteaoutils.utils.add
import me.clcondorcet.boiteaoutils.utils.getBlockRef
import me.clcondorcet.boiteaoutils.utils.getComponent
import me.clcondorcet.boiteaoutils.utils.getRefUUID
import me.clcondorcet.boiteaoutils.utils.toVector3d


class HolographicContainerQuantityCounterSystem : EntityTickingSystem<ChunkStore>() {
    override fun tick(
        dt: Float,
        index: Int,
        archetypeChunk: ArchetypeChunk<ChunkStore>,
        store: Store<ChunkStore>,
        commandBuffer: CommandBuffer<ChunkStore>
    ) {
        val blocks: BlockSection = archetypeChunk.getComponent(index, BlockSection.getComponentType()) ?: return

        if (blocks.tickingBlocksCountCopy == 0) return
        val section: ChunkSection = archetypeChunk.getComponent(index, ChunkSection.getComponentType()) ?: return
        val blockComponentChunk: BlockComponentChunk = commandBuffer.getComponent(
                section.chunkColumnReference,
                BlockComponentChunk.getComponentType()
            ) ?: return

        blocks.forEachTicking(
            blockComponentChunk,
            commandBuffer,
            section.y
        ) { _: BlockComponentChunk, cmdBuffer: CommandBuffer<ChunkStore>, localX: Int, localY: Int, localZ: Int, _: Int ->
            val blockRef: Ref<ChunkStore> =
                blockComponentChunk.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ))
                    ?: return@forEachTicking BlockTickStrategy.IGNORED

            val hologramComponent: HolographicContainerQuantityCounter = cmdBuffer.getComponent(blockRef, componentTypes.holographicContainerQuantityCounter) ?: return@forEachTicking BlockTickStrategy.IGNORED

            val worldChunk = commandBuffer.getComponent(
                section.chunkColumnReference,
                WorldChunk.getComponentType()
            )
            val world = worldChunk!!.world

            val globalX = localX + (worldChunk.x * 32)
            val globalZ = localZ + (worldChunk.z * 32)
            var containerPosition = BlockPosition(globalX, localY, globalZ)

            val itemContainerStateType = ItemContainerStateType ?: return@forEachTicking BlockTickStrategy.IGNORED

            containerPosition = containerPosition.add(hologramComponent.containerOffsetPosition)

            // Check for existence of the hologram entity. If not found we create it.
            world.execute {
                var sum = 0
                // Compute size of the container
                world.getBlockRef(containerPosition)
                    ?.getComponent(itemContainerStateType)
                    ?.itemContainer
                    ?.forEach { _, stack -> sum += stack.quantity }

                if (hologramComponent.hologramEntityUUID != null) {
                    val ref = world.entityStore.getRefFromUUID(hologramComponent.hologramEntityUUID!!)
                    if (ref != null) {
                        world.entityStore.store?.getComponent(ref, Nameplate.getComponentType())?.text = sum.toString()
                    }
                } else {
                    val holder: Holder<EntityStore> = EntityStore.REGISTRY.newHolder()
                    val projectileComponent = ProjectileComponent("Projectile")
                    holder.putComponent(ProjectileComponent.getComponentType(), projectileComponent)
                    holder.putComponent(
                        TransformComponent.getComponentType(),
                        TransformComponent(containerPosition.toVector3d().add(Vector3d(0.5, 1.25, 0.5)), Vector3f(0F, 0F, 0F))
                    )
                    holder.ensureComponent(UUIDComponent.getComponentType())

                    if (projectileComponent.projectile == null) {
                        projectileComponent.initialize()
                        if (projectileComponent.projectile == null) {
                            return@execute
                        }
                    }
                    holder.addComponent(
                        NetworkId.getComponentType(),
                        NetworkId(world.entityStore.store.getExternalData().takeNextNetworkId())
                    )
                    holder.addComponent(Nameplate.getComponentType(), Nameplate(sum.toString()))
                    val ref = world.entityStore.store.addEntity(holder, AddReason.SPAWN)
                    if (ref?.isValid == true) {
                        hologramComponent.hologramEntityUUID = ref.getRefUUID()
                    }
                }
            }

            return@forEachTicking BlockTickStrategy.CONTINUE
        }
    }

    override fun getQuery(): Query<ChunkStore> {
        return Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType())
    }
}