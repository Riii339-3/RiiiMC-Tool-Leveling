package io.github.riiimc.riiimc_leveling.blockentity

import io.github.riiimc.riiimc_leveling.registries.LevelingRegistry
import io.github.riiimc.riiimc_leveling.components.ToolAttributeData
import io.github.riiimc.riiimc_leveling.components.ToolLevelData
import io.github.riiimc.riiimc_leveling.handler.DynamicItemHandler
import io.github.riiimc.riiimc_leveling.items.upgrades.IUpgradePlate
import io.github.riiimc.riiimc_leveling.menu.LevelingTableMenu
import io.github.riiimc.riiimc_leveling.packet.CheckItemsRequestToClientPayload
import io.github.riiimc.riiimc_leveling.packet.CheckItemsRequestToServerPayload
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
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
import net.neoforged.neoforge.network.PacketDistributor
import java.util.UUID

class LevelingTableBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(LevelingRegistry.LEVELING_TABLE_ENTITY_TYPE.get(), pos, state), MenuProvider {
    companion object {
        const val MATERIAL_START_SLOT = 3
    }
    val inventory = DynamicItemHandler(4)
        /*
        override fun getSlotLimit(slot: Int): Int {
            return if (slot == 2) MAX_MATERIAL_AMOUNT.get() else super.getSlotLimit(slot)
        }

         */

    var tinkeringPlayer: UUID? = null
    var tinkering: Boolean = false
    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)
        tag.put("inventory", inventory.serializeNBT(provider))
        tag.putBoolean("isTinkering", tinkering)
        tinkeringPlayer?.let {
            tag.putUUID("tinkeringPlayer", it)
        }
    }

    override fun loadAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(tag, provider)
        inventory.deserializeNBT(provider, tag.getCompound("inventory"))
        tinkering = tag.getBoolean("isTinkering")
        tinkeringPlayer =
            if (tag.hasUUID("tinkeringPlayer")) {
                tag.getUUID("tinkeringPlayer")
            } else {
                null
            }
    }

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu {
        return LevelingTableMenu(p0, p1, this)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("block.riiimc_leveling.leveling_table")
    }

    fun tryUpgrade(result: Boolean): Boolean {
        val tool = inventory.getStackInSlot(0)
        val repair = inventory.getStackInSlot(1)
        val upgrade = inventory.getStackInSlot(2)


        if (tool.isEmpty || !tool.has(LevelingRegistry.TOOL_LEVEL)) return false
        if (repair.isEmpty || !LevelingUtils.checkTool(tool, repair)) return false
        if (upgrade.isEmpty) return false


        val toolLevelData = tool.get(LevelingRegistry.TOOL_LEVEL)
            ?: return false


        val upgradeItem = upgrade.item
        val upgradeItemItf = upgradeItem as? IUpgradePlate
            ?: return false


        val extraCost = toolLevelData.level * (toolLevelData.level + 1) / 2
        val cost = upgradeItemItf.slotCost + extraCost


        // 必要素材チェック
        if (!hasMaterial(cost)) {
            return false
        }


        // ミニゲーム失敗
        if (!result) {

            upgrade.shrink(1)
            consumeMaterial(1)
            repair.shrink(1)

            return false
        }


        if (upgradeItemItf.slotCost > toolLevelData.availableSlots) {
            return false
        }


        val upgradeAttributes = upgradeItemItf.modifiers


        val currentAttributes =
            toolLevelData.toolAttributes.toMutableList()


        upgradeAttributes.forEach { attribute ->

            val index = currentAttributes.indexOfFirst {
                it.attribute == attribute.attribute &&
                        it.modifier.id == attribute.modifier.id
            }


            if (index >= 0) {

                val old = currentAttributes[index]

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
            availableSlots =
                toolLevelData.availableSlots - upgradeItemItf.slotCost,

            upgrades =
                toolLevelData.upgrades +
                        BuiltInRegistries.ITEM.getKey(upgradeItem),

            toolAttributes = currentAttributes
        )


        tool.set(
            LevelingRegistry.TOOL_LEVEL,
            newToolLevelData
        )


        upgrade.shrink(1)
        consumeMaterial(cost)
        repair.shrink(1)


        return true
    }



    fun checkItems(
        pos: BlockPos,
        player: ServerPlayer
    ) {
        val tool = inventory.getStackInSlot(0)
        val repair = inventory.getStackInSlot(1)
        val upgrade = inventory.getStackInSlot(2)


        val result =
            !tool.isEmpty &&
                    tool.has(LevelingRegistry.TOOL_LEVEL) &&
                    !repair.isEmpty &&
                    LevelingUtils.checkTool(tool, repair) &&
                    !upgrade.isEmpty &&
                    hasMaterial(
                        getUpgradeCost()
                    )
        var upgradeItem: IUpgradePlate

        if (upgrade.item is IUpgradePlate) {
            upgradeItem = upgrade.item as IUpgradePlate
            tool.get(LevelingRegistry.TOOL_LEVEL)?.let {
                PacketDistributor.sendToPlayer(
                    player,
                    CheckItemsRequestToClientPayload(
                        pos,
                        false
                    )
                )
                if (upgradeItem.slotCost > it.availableSlots) {
                    return
                }
            }
        }



        PacketDistributor.sendToPlayer(
            player,
            CheckItemsRequestToClientPayload(
                pos,
                result
            )
        )
    }



    private fun getUpgradeCost(): Int {

        val tool = inventory.getStackInSlot(0)
        val upgrade = inventory.getStackInSlot(2)

        if (tool.isEmpty || upgrade.isEmpty)
            return Int.MAX_VALUE


        val data =
            tool.get(LevelingRegistry.TOOL_LEVEL)
                ?: return Int.MAX_VALUE


        val plate =
            upgrade.item as? IUpgradePlate
                ?: return Int.MAX_VALUE


        val extraCost =
            data.level * (data.level + 1) / 2


        return plate.slotCost + extraCost
    }



    private fun hasMaterial(amount: Int): Boolean {

        var count = 0


        for (i in MATERIAL_START_SLOT until inventory.slots) {

            val stack =
                inventory.getStackInSlot(i)


            if (stack.isEmpty)
                continue


            count += stack.count


            if (count >= amount)
                return true
        }


        return false
    }



    private fun consumeMaterial(amount: Int): Boolean {

        var remaining = amount


        for (i in inventory.slots - 1 downTo MATERIAL_START_SLOT) {

            val stack =
                inventory.getStackInSlot(i)


            if (stack.isEmpty)
                continue


            val remove =
                minOf(
                    stack.count,
                    remaining
                )


            stack.shrink(remove)

            remaining -= remove


            if (stack.isEmpty) {
                inventory.setStackInSlot(
                    i,
                    ItemStack.EMPTY
                )
            }


            if (remaining <= 0) {
                return true
            }
        }


        return false
    }}