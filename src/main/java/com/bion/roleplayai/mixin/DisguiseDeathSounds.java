package com.bion.roleplayai.mixin;

import com.bion.roleplayai.mixin.accessor.LivingEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.disguiselib.api.EntityDisguise;

@Mixin(LivingEntity.class)
public abstract class DisguiseDeathSounds extends Entity {


    protected DisguiseDeathSounds(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method="damage", at=@At("STORE"), ordinal = 0)
    public SoundEvent modifyDeathSoundsDamage(SoundEvent soundEvent) {
        if (((EntityDisguise) this).isDisguised()) {
            return ((LivingEntityAccessor) ((EntityDisguise) this).getDisguiseEntity()).invokeGetDeathSound();
        }
        return soundEvent;
    }

    @ModifyVariable(method="handleStatus", at=@At("STORE"), ordinal = 0)
    public SoundEvent modifyDeathSoundsStatus(SoundEvent soundEvent) {
        if (((EntityDisguise) this).isDisguised()) {
            return ((LivingEntityAccessor) ((EntityDisguise) this).getDisguiseEntity()).invokeGetDeathSound();
        }
        return soundEvent;
    }

}
