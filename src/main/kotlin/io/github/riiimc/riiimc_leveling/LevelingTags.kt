package io.github.riiimc.riiimc_leveling

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.neoforged.neoforge.common.Tags

object LevelingTags {
    @JvmField
    val UpgradeItemTag = TagKey.create<Item>(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(RiiiMcLeveling.MODID, "upgrades"))
    @JvmField
    val LevelingToolTag = TagKey.create<Item>(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(RiiiMcLeveling.MODID, "leveling_tools"))

    @JvmField
    val RepairMaterialTag = TagKey.create<Item>(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(RiiiMcLeveling.MODID, "repair_materials"))
}