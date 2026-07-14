package io.github.riiimc.riiimc_leveling.items.upgrades

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import io.github.riiimc.riiimc_leveling.components.ToolAttributeData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.Item

class AttackDamageUpgradePlate(properties: Properties) : UpgradePlateItem(properties), IUpgradePlate {
    override val slotCost = 1
    // override val id = ResourceLocation.fromNamespaceAndPath(RiiiMcLeveling.MODID, "attack_damage")


    override val modifiers = listOf(
        ToolAttributeData(
        AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(RiiiMcLeveling.MODID, "attack_damage"),
            1.0,
            AttributeModifier.Operation.ADD_VALUE
        ), EquipmentSlotGroup.MAINHAND,
            ResourceLocation.fromNamespaceAndPath("minecraft", "generic.attack_damage")
        )
    )

    // override val equipmentSlot = EquipmentSlotGroup.MAINHAND
    // override val attributeId = ResourceLocation.fromNamespaceAndPath("minecraft", "generic.attack_damage")
}