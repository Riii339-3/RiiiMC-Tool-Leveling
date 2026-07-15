package io.github.riiimc.riiimc_leveling.client

import io.github.riiimc.riiimc_leveling.registries.LevelingRegistry
import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import net.minecraft.network.chat.Component
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent


@EventBusSubscriber(
    modid = RiiiMcLeveling.MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = [Dist.CLIENT]
)
object ClientEvents {

    @JvmStatic
    @SubscribeEvent
    fun register(event: RegisterMenuScreensEvent) {
        event.register(
            LevelingRegistry.LEVELING_TABLE_MENU.get(),
            ::LevelingTableScreen
        )
    }
    @SubscribeEvent
    @JvmStatic
    fun registerGuiLayers(event: RegisterGuiLayersEvent) {
        event.registerAboveAll(
            LevelingTableMiniGameScreen.ID,
            LevelingTableMiniGameScreen.INSTANCE
        )
    }


    @SubscribeEvent
    @JvmStatic
    fun onTooltip(event: ItemTooltipEvent) {
        val data = event.itemStack.get(LevelingRegistry.TOOL_LEVEL)
            ?: return

        event.toolTip.add(
            Component.translatable("tooltip.riiimc_leveling.level")
                .append(": ${data.level}")
        )
    }
}