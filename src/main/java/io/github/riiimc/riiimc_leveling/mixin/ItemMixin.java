package io.github.riiimc.riiimc_leveling.mixin;

import io.github.riiimc.riiimc_leveling.Config;
import io.github.riiimc.riiimc_leveling.LevelingTags;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getDefaultMaxStackSize", at = @At("HEAD"), cancellable = true)
    public void onGetDefaultMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        Item item = (Item)(Object)this;

        if (item.builtInRegistryHolder().is(LevelingTags.RepairMaterialTag)) {
            cir.setReturnValue(Config.INSTANCE.getMAX_MATERIAL_AMOUNT().get());
        }
    }
}
