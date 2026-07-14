package io.github.riiimc.riiimc_leveling.compat

import net.neoforged.fml.ModList

object LevelingCompat {

    fun checkOptionalMod(modId: String): Boolean {
        return ModList.get().isLoaded(modId)
    }
}