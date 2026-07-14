package io.github.riiimc.riiimc_leveling.compat.curios

import io.github.riiimc.riiimc_leveling.compat.LevelingCompat

object CuriosCompat {
    fun checkMod(): Boolean {
        return LevelingCompat.checkOptionalMod("curios")
    }
}