package io.github.riiimc.riiimc_leveling.menu

import com.mojang.datafixers.util.Pair
import io.github.riiimc.riiimc_leveling.registries.LevelingRegistry
import io.github.riiimc.riiimc_leveling.LevelingTags
import io.github.riiimc.riiimc_leveling.SlotType
import io.github.riiimc.riiimc_leveling.blockentity.LevelingTableBlockEntity
import io.github.riiimc.riiimc_leveling.handler.LevelingSlotItemHandler
import io.github.riiimc.riiimc_leveling.handler.LevelingSlotMaterialItemHandler
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.SlotItemHandler

class LevelingTableMenu(
    id: Int,
    playerInventory: Inventory,
    val blockEntity: LevelingTableBlockEntity
): AbstractContainerMenu(
    LevelingRegistry.LEVELING_TABLE_MENU.get(),
    id
) {
    init {
        // 0: ツール
        addSlot(object : SlotItemHandler(blockEntity.inventory, 0, 78, 54) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return stack.has(LevelingRegistry.TOOL_LEVEL.get())
            }
        })

// 1: 修理素材（自動判定）
        addSlot(LevelingSlotItemHandler(blockEntity, 1, 62, 35, SlotType.REPAIR))

// 2: 敵ドロップ素材
        addSlot(LevelingSlotMaterialItemHandler(blockEntity, 3, 80, 35, SlotType.MATERIAL))

// 3: アップグレード板
        addSlot(object : SlotItemHandler(blockEntity.inventory, 2, 98, 35) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return stack.`is`(LevelingTags.UpgradeItemTag)
            }
        })

        // Player Inventory
        for (row in 0 until 3) {
            for (col in 0 until 9) {
                addSlot(
                    Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18 + 32
                    )
                )
            }
        }

// Hotbar
        for (col in 0 until 9) {
            addSlot(
                Slot(
                    playerInventory,
                    col,
                    8 + col * 18,
                    174
                )
            )
        }
    }
    override fun quickMoveStack(p0: Player, p1: Int): ItemStack {
        return ItemStack.EMPTY
    }
    override fun stillValid(player: Player): Boolean {
        return true
    }

    /*
    override fun clicked(
        slotId: Int,
        button: Int,
        clickType: ClickType,
        player: Player
    ) {
        // 素材スロット
        if (slotId == 2 && clickType == ClickType.PICKUP) {
            val slot = getSlot(slotId)
            val carried = carried

            // 手持ちが素材で、スロットにも同じ素材
            if (!carried.isEmpty &&
                slot.hasItem() &&
                ItemStack.isSameItemSameComponents(carried, slot.item)
            ) {
                val stack = slot.item
                val limit = 256

                val move = minOf(
                    carried.count,
                    limit - stack.count
                )

                if (move > 0) {
                    stack.grow(move)
                    carried.shrink(move)
                    slot.setChanged()
                    return
                }
            }
        }

        super.clicked(slotId, button, clickType, player)
    }

     */
}