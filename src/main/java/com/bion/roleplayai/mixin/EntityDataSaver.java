package com.bion.roleplayai.mixin;

import com.bion.roleplayai.util.RoleplAIEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public abstract class EntityDataSaver {
    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if ((Entity)(Object)this instanceof RoleplAIEntity rai) {
            NbtCompound raiData = rai.getNbt();
            if (!raiData.isEmpty()) {
                nbt.put("RoleplAI", raiData);
            }
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void injectReadMethod(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("RoleplAI") && (Entity)(Object)this instanceof RoleplAIEntity rai) {
            rai.setNbt(nbt.getCompound("RoleplAI"));
        }

    }
}
