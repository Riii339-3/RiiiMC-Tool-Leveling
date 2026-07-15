package io.github.riiimc.riiimc_leveling.items

import io.github.riiimc.riiimc_leveling.client.TableMinigameEvent
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext

class HammerItem(properties: Properties) : Item(properties) {
    /*
    override fun useOn(ctx: UseOnContext): InteractionResult {
        val level = ctx.level

        if (!level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        if (TableMinigameEvent.isVisible) {
            return InteractionResult.SUCCESS
        }

        TableMinigameEvent.start("none", 3)

        return InteractionResult.SUCCESS
    }

     */
}