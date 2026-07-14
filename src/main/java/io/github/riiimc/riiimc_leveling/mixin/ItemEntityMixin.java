package io.github.riiimc.riiimc_leveling.mixin;

import io.github.riiimc.riiimc_leveling.Config;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @ModifyConstant(
            method = "merge(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
            constant = @Constant(intValue = 64)
    )
    private static int changeStack(int value) {
        return Config.INSTANCE.getMAX_MATERIAL_AMOUNT().get();
    }

}
