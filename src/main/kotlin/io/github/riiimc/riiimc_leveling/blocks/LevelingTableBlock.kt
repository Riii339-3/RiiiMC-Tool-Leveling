package io.github.riiimc.riiimc_leveling.blocks

import io.github.riiimc.riiimc_leveling.LevelingTags
import io.github.riiimc.riiimc_leveling.blockentity.LevelingTableBlockEntity
import io.github.riiimc.riiimc_leveling.client.TableMinigameEvent
import io.github.riiimc.riiimc_leveling.packet.CheckItemsRequestToServerPayload
import io.github.riiimc.riiimc_leveling.packet.UpgradeRequestPacketPayload
import io.github.riiimc.riiimc_leveling.registries.LevelingRegistry
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.network.PacketDistributor

class LevelingTableBlock(properties: BlockBehaviour.Properties): Block(properties), EntityBlock {
    companion object {
        const val MATERIAL_START_SLOT = 3
    }

    override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity? {
        return LevelingTableBlockEntity(p0, p1)
    }


    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {

        if (level.isClientSide) return InteractionResult.PASS
        if (!player.isShiftKeyDown) return InteractionResult.PASS


        val be = level.getBlockEntity(pos)
                as? LevelingTableBlockEntity
            ?: return InteractionResult.PASS


        val inventory = be.inventory


        // 材料スロットを後ろから検索
        // 材料スロットを後ろから検索
        for (i in inventory.slots - 1 downTo 0) {

            val stack = inventory.getStackInSlot(i)

            if (stack.isEmpty)
                continue

            val copy = stack.copy()

            if (!player.addItem(copy)) {
                player.drop(copy, false)
            }

            inventory.setStackInSlot(i, ItemStack.EMPTY)

            // 可変スロットだけ縮小
            if (i >= MATERIAL_START_SLOT) {
                var newSize = MATERIAL_START_SLOT

                for (slot in inventory.slots - 1 downTo MATERIAL_START_SLOT) {
                    if (!inventory.getStackInSlot(slot).isEmpty) {
                        newSize = slot + 1
                        break
                    }
                }

                inventory.resize(newSize)
            }

            be.setChanged()
            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): ItemInteractionResult {
        if (level.isClientSide) {
            if (!stack.`is`(LevelingTags.HammersTag)) return ItemInteractionResult.SUCCESS
            PacketDistributor.sendToServer(CheckItemsRequestToServerPayload(pos))
            return ItemInteractionResult.SUCCESS
        }

        val be = level.getBlockEntity(pos) as? LevelingTableBlockEntity ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        val inventory = be.inventory

        println("useItemOn: SERVER")

        when {
            stack.item == LevelingRegistry.OPEN_GUI_ITEM.get() -> {
                val be = level.getBlockEntity(pos) as? LevelingTableBlockEntity
                    ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

                player.openMenu(be, pos)
            }
            stack.`is`(LevelingTags.LevelingToolTag) -> {
                if (!inventory.getStackInSlot(0).isEmpty) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                inventory.setStackInSlot(0, player.mainHandItem.copy())
                player.mainHandItem.count = 0
            }
            stack.`is`(LevelingTags.RepairMaterialTag) -> {

                // 同じアイテムで64未満のスタックを探す
                for (slot in MATERIAL_START_SLOT until inventory.slots) {
                    val current = inventory.getStackInSlot(slot)

                    if (current.isEmpty) continue
                    if (!ItemStack.isSameItemSameComponents(current, stack)) continue
                    if (current.count >= current.maxStackSize) continue

                    val insert = minOf(stack.count, current.maxStackSize - current.count)
                    current.grow(insert)
                    inventory.setStackInSlot(slot, current)

                    stack.shrink(insert)

                    if (stack.isEmpty) {
                        be.setChanged()
                        return ItemInteractionResult.SUCCESS
                    }
                }

                // 空スロットを探す
                for (slot in MATERIAL_START_SLOT until inventory.slots) {
                    if (inventory.getStackInSlot(slot).isEmpty) {
                        val copy = stack.copy()
                        copy.count = minOf(copy.maxStackSize, stack.count)

                        inventory.setStackInSlot(slot, copy)
                        stack.shrink(copy.count)

                        be.setChanged()
                        return ItemInteractionResult.SUCCESS
                    }
                }

                // 空きがないなら1つ増やす
                inventory.resize(inventory.slots + 1)

                val copy = stack.copy()
                copy.count = minOf(copy.maxStackSize, stack.count)

                inventory.setStackInSlot(inventory.slots - 1, copy)
                stack.shrink(copy.count)
            }
            stack.`is`(LevelingTags.UpgradeItemTag) -> {
                if (!inventory.getStackInSlot(2).isEmpty) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                inventory.setStackInSlot(2, player.mainHandItem.copy())
                player.mainHandItem.count = 0
            }
            !inventory.getStackInSlot(0).isEmpty -> {
                if (!inventory.getStackInSlot(1).isEmpty) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                if (!LevelingUtils.checkTool(inventory.getStackInSlot(0), player.mainHandItem)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                inventory.setStackInSlot(1, player.mainHandItem.copy())
                player.mainHandItem.count = 0
            }
            else -> {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
            }
        }
        be.setChanged()

        /*
        if (!stack.`is`(LevelingTags.LevelingToolTag)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        }


        if (inventory.getStackInSlot(3).isEmpty) {
            inventory.setStackInSlot(3, stack.copy())
            be.setChanged()
            return ItemInteractionResult.SUCCESS
        }

         */

        return ItemInteractionResult.SUCCESS
    }

    fun collectData(pos: BlockPos, dataResult: Boolean) {
        if (!dataResult) return
        if (TableMinigameEvent.isVisible) {
            val result = TableMinigameEvent.handleHit()

            if (TableMinigameEvent.hitsRemaining <= 0) {
                println("FINISH: $result")
                if (result == null) return
                PacketDistributor.sendToServer(
                    UpgradeRequestPacketPayload(pos, result)
                )
            }

            return
        }
        TableMinigameEvent.start("none", 3)

    }
}