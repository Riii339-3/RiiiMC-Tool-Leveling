package io.github.riiimc.riiimc_leveling.mixin;

import io.github.riiimc.riiimc_leveling.LevelingConfig;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
@Mixin(Container.class)
public interface ContainerMixin {
    @Inject(
            method = "getMaxStackSize()I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(LevelingConfig.INSTANCE.getMAX_MATERIAL_AMOUNT().get());
    }
}

 */