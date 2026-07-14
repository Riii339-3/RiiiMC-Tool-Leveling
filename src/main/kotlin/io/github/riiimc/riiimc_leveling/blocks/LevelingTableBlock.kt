package io.github.riiimc.riiimc_leveling.blocks

import io.github.riiimc.riiimc_leveling.blockentity.LevelingTableBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class LevelingTableBlock(properties: BlockBehaviour.Properties): Block(properties), EntityBlock {
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
        if (!level.isClientSide) {
            val be = level.getBlockEntity(pos) as? LevelingTableBlockEntity
                ?: return InteractionResult.PASS

            player.openMenu(be, pos)
        }

        return InteractionResult.SUCCESS
    }
}