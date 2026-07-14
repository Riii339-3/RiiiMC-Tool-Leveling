package io.github.riiimc.riiimc_leveling

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.common.ModConfigSpec.IntValue
import java.util.function.Predicate
import java.util.stream.Collectors

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = RiiiMcLeveling.MODID, bus = EventBusSubscriber.Bus.MOD)
object Config {
    private val BUILDER = ModConfigSpec.Builder()

    val BASE_NEXT_EXP = BUILDER.comment("探せ、この世の全てを置いてきた").defineInRange("baseNextExp", 50, 1, Int.MAX_VALUE)

    val NEXT_EXP_RATE = BUILDER.comment("次Lvまでの経験値を計算する際の係数").defineInRange("nextExpRate", 1.2, 1.0, 10.0)

    val MAX_LEVEL = BUILDER.comment("レベルの最大値").defineInRange("maxLevel", 999, 1, Int.MAX_VALUE)

    val MAX_MATERIAL_AMOUNT = BUILDER.comment("素材の最大値").defineInRange("maxMaterialAmount", 256, 1, Int.MAX_VALUE)
    val SPEC: ModConfigSpec = BUILDER.build()
    var logDirtBlock: Boolean = false
    var magicNumber: Int = 0
    var magicNumberIntroduction: String? = null
    var items: MutableSet<Item?>? = null

    private fun validateItemName(obj: Any?): Boolean {
        return obj is String && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(obj))
    }

    @SubscribeEvent
    @JvmStatic
    fun onLoad(event: ModConfigEvent?) {

        // convert the list of strings into a set of items
    }
}
