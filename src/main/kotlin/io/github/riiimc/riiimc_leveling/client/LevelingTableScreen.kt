package io.github.riiimc.riiimc_leveling.client

import io.github.riiimc.riiimc_leveling.menu.LevelingTableMenu
import io.github.riiimc.riiimc_leveling.packet.UpgradeRequestPacketPayload
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
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
    private val gui = LevelingUtils.rl("textures/gui/leveling_table.png")

    override fun init() {
        super.init()

        super.imageWidth = 176
        super.imageHeight = 200

        upgradeButton = Button.builder(
            Component.translatable("gui.riiimc_leveling.leveling_table.upgrade")
        ) {
            PacketDistributor.sendToServer(
                UpgradeRequestPacketPayload(menu.blockEntity.blockPos, true)
            )
        }.bounds(leftPos + 100, topPos + 50, 40, 20).build()

        addRenderableWidget(upgradeButton)
    }

    override fun renderBg(
        guiGraphics: GuiGraphics,
        partialTick: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        guiGraphics.blit(gui, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 176, 200)
    }
}