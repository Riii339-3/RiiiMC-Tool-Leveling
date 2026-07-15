package io.github.riiimc.riiimc_leveling.mixin;

import io.github.riiimc.riiimc_leveling.LevelingConfig;
import io.github.riiimc.riiimc_leveling.LevelingTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getDefaultMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void onGetDefaultMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        Item item = (Item) (Object) this;

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null) return;

        BuiltInRegistries.ITEM.getHolder(id).ifPresent(holder -> {
            if (holder.is(LevelingTags.RepairMaterialTag)) {
                cir.setReturnValue(LevelingConfig.INSTANCE.getMAX_MATERIAL_AMOUNT().get());
            }
        });
    }
}

 */