package io.github.riiimc.riiimc_leveling.handler

import io.github.riiimc.riiimc_leveling.SlotType
import io.github.riiimc.riiimc_leveling.blockentity.LevelingTableBlockEntity
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.SlotItemHandler

class LevelingSlotItemHandler(
    private val blockEntity: LevelingTableBlockEntity,
    index: Int,
    x: Int,
    y: Int,
    private val type: SlotType
) : SlotItemHandler(blockEntity.inventory, index, x, y) {

    override fun mayPlace(stack: ItemStack): Boolean {
        return when (type) {
            SlotType.REPAIR -> {
                val tool = blockEntity.inventory.getStackInSlot(0)
                if (tool.isEmpty) {
                    false
                } else {
                    /*
                    when (val item = tool.item) {
                        is TieredItem -> {
                            item.tier.repairIngredient.test(stack)
                        }

                        is ArmorItem -> {
                            item.material.value().repairIngredient.get().test(stack)
                        }

                        else -> false
                    }
                     */
                    LevelingUtils.checkTool(tool, stack)
                }
            }

            else -> true
        }
    }

}