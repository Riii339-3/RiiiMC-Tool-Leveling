package io.github.riiimc.riiimc_leveling.items.upgrades

import io.github.riiimc.riiimc_leveling.components.ToolAttributeData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier

interface IUpgradePlate {
    val id: ResourceLocation

    val modifiers: List<ToolAttributeData>
    val slotCost: Int }