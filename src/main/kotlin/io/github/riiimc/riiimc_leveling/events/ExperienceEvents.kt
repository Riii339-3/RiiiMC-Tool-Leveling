package io.github.riiimc.riiimc_leveling.events

import io.github.riiimc.riiimc_leveling.LevelingTags
import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import io.github.riiimc.riiimc_leveling.leveling.ExperienceType
import io.github.riiimc.riiimc_leveling.leveling.ToolLevelingSystem
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.level.BlockEvent

@EventBusSubscriber(
    modid = RiiiMcLeveling.MODID,
    bus = EventBusSubscriber.Bus.MOD
)
object ExperienceEvents {
    @SubscribeEvent
    @JvmStatic
    fun breakBlock(event: BlockEvent.BreakEvent) {

        val player = event.player ?: return
        val stack = player.mainHandItem

        if (stack.`is`(LevelingTags.MiningExpTag)) {
            ToolLevelingSystem.addExp(stack, 1, ExperienceType.BLOCK_BREAK)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onLivingDamage(event: LivingDamageEvent.Post) {
        val attacker = event.source.entity as? Player ?: return

        val stack = attacker.mainHandItem
        if (!stack.`is`(LevelingTags.MeleeExpTag)) return

        ToolLevelingSystem.addExp(
            stack,
            event.newDamage.toInt().coerceAtLeast(1),
            ExperienceType.MELEE
        )
    }

    @JvmStatic
    @SubscribeEvent
    fun onArmorDamage(event: LivingDamageEvent.Post) {
        val player = event.entity as? Player ?: return

        val exp = event.newDamage.toInt().coerceAtLeast(1)

        player.armorSlots.forEach {
            if (!it.`is`(LevelingTags.ArmorExpTag)) return@forEach
            ToolLevelingSystem.addExp(
                it,
                exp,
                ExperienceType.ARMOR_DAMAGE
            )
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onProjectileDamage(event: LivingDamageEvent.Post) {
        val direct = event.source.directEntity as? Projectile ?: return
        val shooter = direct.owner as? Player ?: return

        val stack = shooter.mainHandItem
        if (!stack.`is`(LevelingTags.ProjectileExpTag)) return

        ToolLevelingSystem.addExp(
            stack,
            event.newDamage.toInt().coerceAtLeast(1),
            ExperienceType.PROJECTILE
        )
    }

    @JvmStatic
    @SubscribeEvent
    fun onRightClick(event: PlayerInteractEvent.RightClickItem) {
        val player = event.entity
        val stack = event.itemStack
        if (!stack.`is`(LevelingTags.RightClickExpTag)) return


        ToolLevelingSystem.addExp(
            stack,
            1,
            ExperienceType.RIGHT_CLICK
        )
    }
}