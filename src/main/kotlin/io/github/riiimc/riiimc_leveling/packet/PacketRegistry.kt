package io.github.riiimc.riiimc_leveling.packet

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import io.github.riiimc.riiimc_leveling.blockentity.LevelingTableBlockEntity
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@EventBusSubscriber(
    modid = RiiiMcLeveling.MODID,
    bus = EventBusSubscriber.Bus.MOD
)
object PacketRegistry {

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

                val blockEntity = player.level()
                    .getBlockEntity(payload.pos)

                if (blockEntity is LevelingTableBlockEntity) {
                    blockEntity.tryUpgrade(player)
                }
            }
        }
    }
}