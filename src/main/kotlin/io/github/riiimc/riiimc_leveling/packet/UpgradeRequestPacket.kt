package io.github.riiimc.riiimc_leveling.packet

import net.minecraft.core.BlockPos

data class UpgradeRequestPacket(
    val pos: BlockPos,
    val result: Boolean
)