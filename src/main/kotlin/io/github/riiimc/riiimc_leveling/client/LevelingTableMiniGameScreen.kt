package io.github.riiimc.riiimc_leveling.client

import com.mojang.blaze3d.systems.RenderSystem
import io.github.riiimc.riiimc_leveling.LevelingConfig
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import java.util.function.Supplier


@OnlyIn(Dist.CLIENT)
class LevelingTableMiniGameScreen : LayeredDraw.Layer {
    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val showMainOverlay: Boolean = TableMinigameEvent.isVisible
        if (!showMainOverlay) return  // Early return if not visible


        val screenWidth = guiGraphics.guiWidth()
        val screenHeight = guiGraphics.guiHeight()

        val imageWidth = 238
        val imageHeight = 37
        val textureWidth = 256
        val textureHeight = 128

        val x = (screenWidth - imageWidth) / 2
        val y: Int = ((screenHeight - imageHeight)
                - LevelingConfig.MINIGAME_OVERLAY_HEIGHT.get())

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader(Supplier { GameRenderer.getPositionTexShader() })
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)

        // Draw the main overlay
        guiGraphics.blit(
            TEXTURE, x, y,
            0f, 0f, imageWidth, imageHeight,
            textureWidth, textureHeight
        )

        val barX = x + 9
        val barY = y + 21
        val barWidth = 220
        val barHeight = 10

        // Zones
        val perfectZoneStart: Int = TableMinigameEvent.perfectZoneStart
        val perfectZoneEnd: Int = TableMinigameEvent.perfectZoneEnd
        val goodZoneStart: Int = TableMinigameEvent.goodZoneStart
        val goodZoneEnd: Int = TableMinigameEvent.goodZoneEnd
        val arrowPosition: Float = TableMinigameEvent.arrowPosition

        val goodStartPx = (barWidth * goodZoneStart / 100f).toInt()
        val goodEndPx = (barWidth * goodZoneEnd / 100f).toInt()

        if (goodEndPx > goodStartPx) {
            guiGraphics.blit(
                TEXTURE,
                barX + goodStartPx, barY,
                9f, 94f,
                goodEndPx - goodStartPx, barHeight,
                textureWidth, textureHeight
            )
        }

        val perfectStartPx = (barWidth * perfectZoneStart / 100f).toInt()
        val perfectEndPx = (barWidth * perfectZoneEnd / 100f).toInt()

        if (perfectEndPx > perfectStartPx) {
            guiGraphics.blit(
                TEXTURE,
                barX + perfectStartPx, barY,
                9f, 72f,
                perfectEndPx - perfectStartPx, barHeight,
                textureWidth, textureHeight
            )
        }

        // Progress bar
        val progressLengthPx =
            (222 * (1 - (TableMinigameEvent.hitsRemaining.toFloat() / TableMinigameEvent.maxHits))).toInt()

        guiGraphics.blit(
            TEXTURE,
            x + 8, y + 12,
            8f, 62f,
            progressLengthPx, 5,
            textureWidth, textureHeight
        )

        val arrowX = barX + (barWidth * arrowPosition / 100f).toInt() - 5
        guiGraphics.blit(
            TEXTURE,
            arrowX, barY - 3,
            9f, 41f,
            ARROW_WIDTH, ARROW_HEIGHT,
            textureWidth, textureHeight
        )

        /*
        int hitsRemain = AnvilMinigameEvents.getHitsRemaining();
        int perfect = AnvilMinigameEvents.getPerfectHits();
        int good = AnvilMinigameEvents.getGoodHits();
        int miss = AnvilMinigameEvents.getMissedHits();

        Component stats = Component.translatable(
                "gui.overgeared.forging_stats",
                hitsRemain, perfect, good, miss
        );

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                stats,
                x + 10,
                y + 10,
                0x404040,
                false
        );
        */
    }

    companion object {
        val INSTANCE: LevelingTableMiniGameScreen = LevelingTableMiniGameScreen()
        val ID: ResourceLocation = LevelingUtils.rl("table_minigame")

        private val TEXTURE: ResourceLocation = LevelingUtils.rl("textures/gui/table_minigame.png")

        // UI dimensions
        const val barTotalWidth: Int = 184
        private const val ARROW_WIDTH = 8
        private const val ARROW_HEIGHT = 16
    }
}