package io.github.riiimc.riiimc_leveling.components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.AttributeModifier


@JvmRecord
data class ToolLevelData(
    @JvmField
    val level: Int,
    @JvmField
    val exp: Int,
    @JvmField
    val nextLevelExp: Int,
    @JvmField
    val upgrades: List<ResourceLocation>,
    @JvmField
    val availableSlots: Int,
    @JvmField
    val toolAttributes: List<ToolAttributeData>
) {
    companion object {
        val CODEC: Codec<ToolLevelData> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("level").forGetter(ToolLevelData::level),
                    Codec.INT.fieldOf("exp").forGetter(ToolLevelData::exp),
                    Codec.INT.fieldOf("next_level_exp").forGetter(ToolLevelData::nextLevelExp),
                    ResourceLocation.CODEC.listOf().fieldOf("upgrades").forGetter(ToolLevelData::upgrades),
                    Codec.INT.fieldOf("availableSlots").forGetter(ToolLevelData::availableSlots),
                    ToolAttributeData.CODEC.listOf().fieldOf("toolAttributes").forGetter(ToolLevelData::toolAttributes)
                    ).apply(instance, ::ToolLevelData)
            }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToolLevelData> =
            StreamCodec.composite(
                ByteBufCodecs.INT, ToolLevelData::level,
                ByteBufCodecs.INT, ToolLevelData::exp,
                ByteBufCodecs.INT, ToolLevelData::nextLevelExp,
                ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), ToolLevelData::upgrades,
                ByteBufCodecs.INT, ToolLevelData::availableSlots,
                ToolAttributeData.STREAM_CODEC.apply(ByteBufCodecs.list()), ToolLevelData::toolAttributes,
                ::ToolLevelData
            )
    }
}