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
public abstract class DisguiseHurtSounds extends Entity {


    protected DisguiseHurtSounds(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method="playHurtSound", at=@At("STORE"), ordinal = 0)
    public SoundEvent modifyHurtSounds(SoundEvent soundEvent, DamageSource source) {
        if (((EntityDisguise) this).isDisguised()) {
            return ((LivingEntityAccessor) ((EntityDisguise) this).getDisguiseEntity()).invokeGetHurtSound(source);
        }
        return soundEvent;
    }

}
