package io.github.riiimc.riiimc_leveling.packet

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import io.github.riiimc.riiimc_leveling.blockentity.LevelingTableBlockEntity
import io.github.riiimc.riiimc_leveling.blocks.LevelingTableBlock
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@EventBusSubscriber(
    modid = RiiiMcLeveling.MODID,
    bus = EventBusSubscriber.Bus.MOD
)
object LevelingPacketRegistry {

    @JvmStatic
    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")

        registrar.playToServer(
            UpgradeRequestPacketPayload.TYPE,
            UpgradeRequestPacketPayload.STREAM_CODEC
        ) { payload, context ->

            context.enqueueWork {
                val player = context.player() as ServerPlayer
                val result = payload.result

                val blockEntity = player.level()
                    .getBlockEntity(payload.pos)

                if (blockEntity is LevelingTableBlockEntity) {
                    blockEntity.tryUpgrade(result)
                }
            }
        }
        registrar.playToServer(
            CheckItemsRequestToServerPayload.TYPE,
            CheckItemsRequestToServerPayload.STREAM_CODEC
        ) { payload, context ->

            context.enqueueWork {
                val player = context.player() as ServerPlayer

                val blockEntity = player.level()
                    .getBlockEntity(payload.pos)
                if (blockEntity is LevelingTableBlockEntity) {
                    blockEntity.checkItems(payload.pos, player)
                }
            }
        }
        registrar.playToClient(
            CheckItemsRequestToClientPayload.TYPE,
            CheckItemsRequestToClientPayload.STREAM_CODEC
        ) {payload, context ->
            context.enqueueWork {
                val player = context.player() as Player
                val pos = payload.pos
                val block = player.level().getBlockState(pos).block
                if (block is LevelingTableBlock) {
                    block.collectData(pos, payload.result)
                }
            }
        }
    }
}