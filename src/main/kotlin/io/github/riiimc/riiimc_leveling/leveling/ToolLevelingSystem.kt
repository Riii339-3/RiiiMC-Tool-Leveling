package io.github.riiimc.riiimc_leveling.leveling

import io.github.riiimc.riiimc_leveling.LevelingConfig
import io.github.riiimc.riiimc_leveling.LevelingConfig.DURABILITY_EDITION
import io.github.riiimc.riiimc_leveling.LevelingTags
import io.github.riiimc.riiimc_leveling.components.ToolLevelData
import io.github.riiimc.riiimc_leveling.registries.LevelingRegistry
import net.minecraft.world.item.ItemStack

object ToolLevelingSystem {

    fun addExp(
        stack: ItemStack,
        amount: Int,
        source: ExperienceType
    ) {
        if (DURABILITY_EDITION.get()) return
        if (amount <= 0) return
        if (!stack.`is`(LevelingTags.LevelingToolTag)) return

        val data = stack.get(LevelingRegistry.TOOL_LEVEL)
            ?: ToolLevelData(
                0,
                0,
                LevelingConfig.BASE_NEXT_EXP.get(),
                listOf(),
                0,
                listOf()
            )

        var level = data.level
        var exp = data.exp + amount
        var next = data.nextLevelExp
        var slots = data.availableSlots

        while (exp >= next && level < LevelingConfig.MAX_LEVEL.get()) {
            exp -= next
            level++
            slots++
            next = (next * LevelingConfig.NEXT_EXP_RATE.get()).toInt()
        }

        stack.set(
            LevelingRegistry.TOOL_LEVEL,
            data.copy(
                level = level,
                exp = exp,
                nextLevelExp = next,
                availableSlots = slots
            )
        )
    }
}