package io.github.riiimc.riiimc_leveling.mixin;

import io.github.riiimc.riiimc_leveling.Config;
import io.github.riiimc.riiimc_leveling.registries.LevelingRegistry;
import io.github.riiimc.riiimc_leveling.LevelingTags;
import io.github.riiimc.riiimc_leveling.components.ToolAttributeData;
import io.github.riiimc.riiimc_leveling.components.ToolLevelData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V", shift = At.Shift.AFTER)
    )
    public void onHurtAndBreak(int damage, ServerLevel level, @Nullable LivingEntity entity, Consumer<Item> consumer, CallbackInfo ci) {
        if (!(entity instanceof Player)) return;
        ItemStack stack = (ItemStack)(Object)this;
        if (!(stack.is(LevelingTags.LevelingToolTag))) return;
        ToolLevelData data = stack.get(LevelingRegistry.TOOL_LEVEL.get());
        if (data == null) {
            data = new ToolLevelData(0, 0, Config.INSTANCE.getBASE_NEXT_EXP().get(), List.of(), 0, List.of());
            stack.set(LevelingRegistry.TOOL_LEVEL.get(), data);
        }
        int toolLevel = data.level;
        int toolExp = data.exp;
        toolExp += damage;
        int toolNextLevelExp = data.nextLevelExp;
        int toolCost = data.availableSlots;
        List<ToolAttributeData> toolAttributes = data.toolAttributes;
        if (toolExp >= toolNextLevelExp) {
            if (toolLevel >= Config.INSTANCE.getMAX_LEVEL().get()) return;
            toolLevel++;
            toolExp = 0;
            toolCost++;
            double toolNextLevelExpCap = toolNextLevelExp * Config.INSTANCE.getNEXT_EXP_RATE().get();
            toolNextLevelExp = (int) toolNextLevelExpCap;
        }
        stack.set(
                LevelingRegistry.TOOL_LEVEL.get(),
                new ToolLevelData(
                        toolLevel,
                        toolExp,
                        toolNextLevelExp,
                        data.upgrades,
                        toolCost,
                        toolAttributes
                )
        );
    }

    @Inject(method = "getMaxStackSize", at = @At(
            "HEAD"
    ), cancellable = true)
    public void onGetMaxStackSize(CallbackInfoReturnable<Integer> ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if (stack.is(LevelingTags.RepairMaterialTag)) {
            ci.setReturnValue(Config.INSTANCE.getMAX_MATERIAL_AMOUNT().get());
        }
    }

    @ModifyConstant(
            method = "lambda$static$3", // ← 実際のメソッド名
            constant = @Constant(intValue = 99)
    )
    private static int changeMaxStackCount(int value) {
        return Config.INSTANCE.getMAX_MATERIAL_AMOUNT().get();
    }
}
