package io.github.riiimc.riiimc_leveling.packet

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class UpgradeRequestPacketPayload(
    val pos: BlockPos
) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<UpgradeRequestPacketPayload> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<UpgradeRequestPacketPayload> =
            CustomPacketPayload.Type(
                ResourceLocation.fromNamespaceAndPath(
                    RiiiMcLeveling.MODID,
                    "upgrade_request"
                )
            )

        val STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            UpgradeRequestPacketPayload::pos,
            ::UpgradeRequestPacketPayload
        )
    }
}