package io.github.riiimc.riiimc_leveling.events

import io.github.riiimc.riiimc_leveling.registries.LevelingRegistry
import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.ItemAttributeModifierEvent

@EventBusSubscriber(
    modid = RiiiMcLeveling.MODID,
    bus = EventBusSubscriber.Bus.MOD
)
object CommonEvents {
    @SubscribeEvent
    @JvmStatic
    fun addToolAttributes(event: ItemAttributeModifierEvent) {
        val stack = event.itemStack

        val data = stack.get(LevelingRegistry.TOOL_LEVEL)
            ?: return

        data.toolAttributes.forEach { modifier ->
            event.addModifier(BuiltInRegistries.ATTRIBUTE.getHolder(modifier.attribute).get(), modifier.modifier, modifier.equipment)
        }
    }
}