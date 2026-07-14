package io.github.riiimc.riiimc_leveling.components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import java.util.UUID

data class AppliedUpgrade(
    val id: ResourceLocation,
    val uuid: UUID
) {
    companion object {
        val CODEC: Codec<AppliedUpgrade> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(AppliedUpgrade::id),
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(AppliedUpgrade::uuid),
                ).apply(instance, ::AppliedUpgrade)
            }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AppliedUpgrade> =
            StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, AppliedUpgrade::id,
                UUIDUtil.STREAM_CODEC, AppliedUpgrade::uuid,
                ::AppliedUpgrade
            )
    }
}