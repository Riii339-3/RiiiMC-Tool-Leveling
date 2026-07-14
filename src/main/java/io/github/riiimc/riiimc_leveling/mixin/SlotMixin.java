package io.github.riiimc.riiimc_leveling.mixin;

import io.github.riiimc.riiimc_leveling.Config;
import io.github.riiimc.riiimc_leveling.LevelingTags;
import io.github.riiimc.riiimc_leveling.handler.LevelingSlotMaterialItemHandler;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {
    @Inject(method = "getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    public void onGetMaxStackSize(ItemStack p_40238_, CallbackInfoReturnable<Integer> cir) {
        if (p_40238_.is(LevelingTags.RepairMaterialTag)) {
            cir.setReturnValue(Config.INSTANCE.getMAX_MATERIAL_AMOUNT().get());
        }

    }
}
