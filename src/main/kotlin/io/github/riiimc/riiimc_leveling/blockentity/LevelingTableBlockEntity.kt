package io.github.riiimc.riiimc_leveling.blockentity

import io.github.riiimc.riiimc_leveling.Config.MAX_MATERIAL_AMOUNT
import io.github.riiimc.riiimc_leveling.registries.LevelingRegistry
import io.github.riiimc.riiimc_leveling.components.ToolAttributeData
import io.github.riiimc.riiimc_leveling.components.ToolLevelData
import io.github.riiimc.riiimc_leveling.items.upgrades.IUpgradePlate
import io.github.riiimc.riiimc_leveling.menu.LevelingTableMenu
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.ItemStackHandler

class LevelingTableBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(LevelingRegistry.LEVELING_TABLE_ENTITY_TYPE.get(), pos, state), MenuProvider {
    val inventory = object : ItemStackHandler(4) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }

        override fun getSlotLimit(slot: Int): Int {
            return if (slot == 2) MAX_MATERIAL_AMOUNT.get() else super.getSlotLimit(slot)
        }
    }
    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)
        tag.put("inventory", inventory.serializeNBT(provider))
    }

    override fun loadAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(tag, provider)
        inventory.deserializeNBT(provider, tag.getCompound("inventory"))
    }

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu {
        return LevelingTableMenu(p0, p1, this)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("block.riiimc_leveling.leveling_table")
    }

    fun tryUpgrade(player: Player): Boolean {
        val tool: ItemStack = inventory.getStackInSlot(0)
        val repair: ItemStack = inventory.getStackInSlot(1)
        val material: ItemStack = inventory.getStackInSlot(2)
        val upgrade: ItemStack = inventory.getStackInSlot(3)

        if (tool.isEmpty || !tool.has(LevelingRegistry.TOOL_LEVEL)) return false
        if (repair.isEmpty || !LevelingUtils.checkTool(tool, repair)) return false
        if (material.isEmpty) return false
        if (upgrade.isEmpty) return false

        val toolLevelData: ToolLevelData = tool.get(LevelingRegistry.TOOL_LEVEL) ?: return false

        val upgradeItem: Item = upgrade.item
        val upgradeItemItf = upgradeItem as? IUpgradePlate ?: return false

        if (upgradeItem.slotCost > toolLevelData.availableSlots) return false

        val upgradeAttributes: List<ToolAttributeData> = upgradeItem.modifiers

        // 既存のアップグレード属性
        val currentAttributes: MutableList<ToolAttributeData> = toolLevelData.toolAttributes.toMutableList()

        upgradeAttributes.forEach { attribute ->

            val index: Int = currentAttributes.indexOfFirst {
                it.attribute == attribute.attribute &&
                        it.modifier.id == attribute.modifier.id
            }

            if (index >= 0) {
                val old: ToolAttributeData = currentAttributes[index]

                currentAttributes[index] = old.copy(
                    modifier = AttributeModifier(
                        old.modifier.id,
                        old.modifier.amount + attribute.modifier.amount,
                        old.modifier.operation
                    )
                )
            } else {
                currentAttributes.add(attribute)
            }
        }
        val newToolLevelData = toolLevelData.copy(
            availableSlots = toolLevelData.availableSlots - upgradeItem.slotCost,
            upgrades = toolLevelData.upgrades +
                    BuiltInRegistries.ITEM.getKey(upgradeItem),
            toolAttributes = currentAttributes
        )

        tool.set(
            LevelingRegistry.TOOL_LEVEL,
            newToolLevelData
        )

        /*
        if (SGearCompat.checkMod()) {
            if (tool.has(SgDataComponents.GEAR_CONSTRUCTION)) {
                GearData.recalculateGearData(tool, player)
            }
        }

         */

        val extraCost = toolLevelData.level * (toolLevelData.level + 1) / 2
        val cost = upgradeItem.slotCost + extraCost
        if (material.count < cost) {
            return false
        }
        upgrade.shrink(1)
        material.shrink(cost)
        repair.shrink(1)

        return true
    }
}