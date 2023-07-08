package com.bion.roleplayai.mixin;

import com.bion.roleplayai.mixin.accessor.LivingEntityAccessor;
import com.bion.roleplayai.mixin.accessor.MobEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.disguiselib.api.EntityDisguise;

@Mixin(MobEntity.class)
public abstract class DisguiseAmbientSounds extends Entity {


    protected DisguiseAmbientSounds(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method="playAmbientSound", at=@At("STORE"), ordinal = 0)
    public SoundEvent modifyHurtSounds(SoundEvent soundEvent) {
        if (((EntityDisguise) this).isDisguised()) {
            return ((MobEntityAccessor) ((EntityDisguise) this).getDisguiseEntity()).invokeGetAmbientSound();
        }
        return soundEvent;
    }

}
