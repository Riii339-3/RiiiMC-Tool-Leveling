package io.github.riiimc.riiimc_leveling.client

import io.github.riiimc.riiimc_leveling.menu.LevelingTableMenu
import io.github.riiimc.riiimc_leveling.packet.UpgradeRequestPacketPayload
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.network.PacketDistributor

class LevelingTableScreen(
    menu: LevelingTableMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<LevelingTableMenu>(
    menu,
    inventory,
    title
) {
    private lateinit var upgradeButton: Button
    init {
        addRenderableWidget(
            Button.builder(
                Component.literal("強化"),
            ) {
                PacketDistributor.sendToServer(
                    UpgradeRequestPacketPayload(menu.blockEntity.blockPos)
                )
            }
                .bounds(leftPos + 100, topPos + 50, 40, 20)
                .build()
        )
    }
    override fun renderBg(
        guiGraphics: GuiGraphics,
        partialTick: Float,
        mouseX: Int,
        mouseY: Int
    ) {
    }
}