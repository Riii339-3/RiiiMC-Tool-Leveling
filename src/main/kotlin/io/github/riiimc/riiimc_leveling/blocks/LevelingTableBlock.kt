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
        for (i in inventory.slots - 1 downTo MATERIAL_START_SLOT) {

            val stack = inventory.getStackInSlot(i)

            if (stack.isEmpty)
                continue


            val copy = stack.copy()


            if (!player.addItem(copy)) {
                player.drop(copy, false)
            }


            inventory.setStackInSlot(
                i,
                ItemStack.EMPTY
            )


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
                val materialStartSlot = 3
                var nowSlot = inventory.slots - 1

// 材料スロットがまだ存在しない場合
                if (inventory.slots <= materialStartSlot) {
                    inventory.resize(materialStartSlot + 1)
                    nowSlot = materialStartSlot
                }

// 追加対象スロット
                val currentStack = inventory.getStackInSlot(nowSlot)

                if (currentStack.isEmpty) {
                    val newStack = stack.copy()
                    newStack.count = stack.count.coerceAtMost(64)

                    inventory.setStackInSlot(nowSlot, newStack)

                    // 64個入ったら次のスロットを準備
                    if (newStack.count >= 64) {
                        inventory.resize(inventory.slots + 1)
                    }

                    stack.shrink(newStack.count)
                } else {
                    val max = 64
                    val setCount = currentStack.count + stack.count

                    when {
                        setCount < max -> {
                            val newStack = currentStack.copy()
                            newStack.count = setCount

                            inventory.setStackInSlot(nowSlot, newStack)
                            stack.shrink(stack.count)
                        }

                        setCount == max -> {
                            val newStack = currentStack.copy()
                            newStack.count = max

                            inventory.setStackInSlot(nowSlot, newStack)

                            stack.shrink(stack.count)

                            // 次の材料スロット追加
                            inventory.resize(inventory.slots + 1)
                        }

                        setCount > max -> {
                            val insertCount = max - currentStack.count

                            val newStack = currentStack.copy()
                            newStack.count = max

                            inventory.setStackInSlot(nowSlot, newStack)

                            // 入らなかった分だけ手持ちに残す
                            stack.shrink(insertCount)

                            // 次のスロットを追加
                            inventory.resize(inventory.slots + 1)

                            val nextSlot = inventory.slots - 1

                            val remain = stack.copy()
                            remain.count = stack.count

                            inventory.setStackInSlot(nextSlot, remain)

                            stack.shrink(stack.count)
                        }
                    }
                }
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

        if (!stack.`is`(LevelingTags.LevelingToolTag)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        }


        if (inventory.getStackInSlot(3).isEmpty) {
            inventory.setStackInSlot(3, stack.copy())
            be.setChanged()
            return ItemInteractionResult.SUCCESS
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
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