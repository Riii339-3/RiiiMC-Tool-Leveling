package io.github.riiimc.riiimc_leveling.utils

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling.Companion.MODID
import io.github.riiimc.riiimc_leveling.compat.sgear.SGearCompat
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TieredItem
import net.silentchaos512.gear.api.util.GearComponent
import net.silentchaos512.gear.api.util.GearComponentInstance
import net.silentchaos512.gear.setup.SgDataComponents

object LevelingUtils {
    fun checkTool(tool: ItemStack, repair: ItemStack): Boolean {
        if (SGearCompat.checkMod()) {
            val construction = tool.get(SgDataComponents.GEAR_CONSTRUCTION)
            if (construction != null) {
                val parts = construction.parts

                parts.forEach { part ->
                    part.materials.forEach { material ->
                        return material.item.item == repair.item
                    }
                }
                return false
            }
        }
        return when (val item = tool.item) {
            is TieredItem -> {
                item.tier.repairIngredient.test(repair)
            }

            is ArmorItem -> {
                item.material.value().repairIngredient.get().test(repair)
            }

            else -> false
        }
    }

    fun rl(id: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MODID, id)
    }

    fun mcRl(id: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("minecraft", id)
    }
}