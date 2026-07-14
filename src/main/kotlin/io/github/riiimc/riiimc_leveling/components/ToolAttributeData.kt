package io.github.riiimc.riiimc_leveling.components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier

@JvmRecord
data class ToolAttributeData(
    val modifier: AttributeModifier,
    val equipment: EquipmentSlotGroup,
    val attribute: ResourceLocation,
    //val id: ResourceLocation
) {
    companion object {
        val CODEC: Codec<ToolAttributeData> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    AttributeModifier.CODEC.fieldOf("modifier").forGetter(ToolAttributeData::modifier),
                    EquipmentSlotGroup.CODEC.fieldOf("equipment").forGetter(ToolAttributeData::equipment),
                    ResourceLocation.CODEC.fieldOf("attribute").forGetter(ToolAttributeData::attribute),
                    //ResourceLocation.CODEC.fieldOf("id").forGetter(ToolAttributeData::id)
                ).apply(instance, ::ToolAttributeData)
            }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToolAttributeData> =
            StreamCodec.composite(
                AttributeModifier.STREAM_CODEC, ToolAttributeData::modifier,
                EquipmentSlotGroup.STREAM_CODEC, ToolAttributeData::equipment,
                ResourceLocation.STREAM_CODEC, ToolAttributeData::attribute,
                //ResourceLocation.STREAM_CODEC, ToolAttributeData::id,
                ::ToolAttributeData
            )
    }
}