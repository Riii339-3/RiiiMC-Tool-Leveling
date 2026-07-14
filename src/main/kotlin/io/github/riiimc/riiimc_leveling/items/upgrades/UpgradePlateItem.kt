package io.github.riiimc.riiimc_leveling.items.upgrades

import io.github.riiimc.riiimc_leveling.components.ToolAttributeData
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

open class UpgradePlateItem(properties: Properties): Item(properties), IUpgradePlate {
    override val slotCost = 1

    override val modifiers = emptyList<ToolAttributeData>()

    override val id = LevelingUtils.rl("upgrade")

    override fun appendHoverText(
        stack: ItemStack,
        ctx: TooltipContext,
        components: MutableList<Component?>,
        flag: TooltipFlag
    ) {
        super.appendHoverText(stack, ctx, components, flag)
        components.add(Component.translatable("item.riiimc_leveling.upgrade_plate.tooltip.slot"))
        modifiers.forEach { modifier ->
            components.add(
                Component.translatable(
                    "item.riiimc_leveling.upgrade_plate.tooltip.attribute",
                    modifier.equipment.name,
                    modifier.attribute.path
                )
            )
        }
    }
}