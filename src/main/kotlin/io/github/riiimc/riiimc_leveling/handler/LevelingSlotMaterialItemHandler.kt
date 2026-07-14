package io.github.riiimc.riiimc_leveling.handler

import io.github.riiimc.riiimc_leveling.LevelingTags
import io.github.riiimc.riiimc_leveling.SlotType
import io.github.riiimc.riiimc_leveling.blockentity.LevelingTableBlockEntity
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.SlotItemHandler

class LevelingSlotMaterialItemHandler(
    private val blockEntity: LevelingTableBlockEntity,
    index: Int,
    x: Int,
    y: Int,
    private val type: SlotType
) : SlotItemHandler(blockEntity.inventory, index, x, y) {

    override fun mayPlace(stack: ItemStack): Boolean {
        return stack.`is`(LevelingTags.RepairMaterialTag)
    }

    override fun getMaxStackSize(): Int {
        return 256
    }
}