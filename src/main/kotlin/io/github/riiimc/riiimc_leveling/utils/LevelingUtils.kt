package io.github.riiimc.riiimc_leveling.utils

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling.Companion.MODID
import io.github.riiimc.riiimc_leveling.compat.sgear.SGearCompat
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TieredItem
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import net.silentchaos512.gear.api.util.GearComponent
import net.silentchaos512.gear.api.util.GearComponentInstance
import net.silentchaos512.gear.setup.SgDataComponents

object LevelingUtils {
    fun getUpgradeIngredient(tool: ItemStack): Ingredient? {

        // Silent Gear
        if (SGearCompat.checkMod()) {
            val construction = tool.get(SgDataComponents.GEAR_CONSTRUCTION)
            if (construction != null) {
                val items = mutableListOf<ItemLike>()

                construction.parts.forEach { part ->
                    part.materials.forEach { material ->
                        items += material.item.item
                    }
                }

                if (items.isNotEmpty()) {
                    return Ingredient.of(*items.toTypedArray())
                }
            }
        }

        // TieredItem
        val item = tool.item
        if (item is TieredItem) {
            return item.tier.repairIngredient
        }

        // Armor
        if (item is ArmorItem) {
            return item.material.value().repairIngredient.get()
        }

        return null
    }
    fun checkTool(tool: ItemStack, repair: ItemStack): Boolean {
        val ingredient = getUpgradeIngredient(tool)
            ?: return false

        return ingredient.test(repair)
    }

    fun rl(id: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MODID, id)
    }

    fun mcRl(id: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("minecraft", id)
    }
}