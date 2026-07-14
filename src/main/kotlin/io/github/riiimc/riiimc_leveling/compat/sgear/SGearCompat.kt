package io.github.riiimc.riiimc_leveling.compat.sgear

import io.github.riiimc.riiimc_leveling.compat.LevelingCompat
import net.neoforged.fml.ModList



object SGearCompat {
    fun checkMod(): Boolean {
        return LevelingCompat.checkOptionalMod("silentgear")
    }
}