package io.github.riiimc.riiimc_leveling.packet

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class CheckItemsRequestToServerPayload(
    val pos: BlockPos
): CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<CheckItemsRequestToServerPayload> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<CheckItemsRequestToServerPayload> =
            CustomPacketPayload.Type(
                ResourceLocation.fromNamespaceAndPath(
                    RiiiMcLeveling.MODID,
                    "check_items_request_to_server"
                )
            )

        val STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CheckItemsRequestToServerPayload::pos,
            ::CheckItemsRequestToServerPayload
        )
    }
}