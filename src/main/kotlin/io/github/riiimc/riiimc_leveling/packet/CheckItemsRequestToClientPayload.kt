package io.github.riiimc.riiimc_leveling.packet

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class CheckItemsRequestToClientPayload(
    val pos: BlockPos,
    val result: Boolean
): CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<CheckItemsRequestToClientPayload> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<CheckItemsRequestToClientPayload> =
            CustomPacketPayload.Type(
                ResourceLocation.fromNamespaceAndPath(
                    RiiiMcLeveling.MODID,
                    "check_items_request_to_client"
                )
            )

        val STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CheckItemsRequestToClientPayload::pos,
            ByteBufCodecs.BOOL,
            CheckItemsRequestToClientPayload::result,
            ::CheckItemsRequestToClientPayload
        )
    }
}