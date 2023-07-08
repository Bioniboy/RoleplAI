package com.bion.roleplayai.mixin;

import com.bion.roleplayai.util.RoleplAIEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MobEntity.class)
public abstract class RoleplAIEntityMixin extends LivingEntity implements RoleplAIEntity {


    protected RoleplAIEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @Shadow
    public abstract EntityNavigation getNavigation();
    @Shadow
    private LivingEntity target;
    @Shadow
    public abstract void setTarget(LivingEntity target);
    BlockPos posTarget = null;
    UUID targetUuid = null;
    LivingEntity entityTarget = null;
    int followRange = 0;
    double followSpeed = 1;
    boolean attack = false;
    String originalType = null;

    @Override
    public NbtCompound getNbt() {
        NbtCompound nbt = new NbtCompound();
        if (posTarget != null)
            nbt.putIntArray("posTarget", new int[]{posTarget.getX(), posTarget.getY(), posTarget.getZ()});
        if (targetUuid != null) {
            nbt.putUuid("entityTarget", targetUuid);
        }
        if (followRange != 0) {
            nbt.putInt("followRange", followRange);
        }
        if (followSpeed != 1)
            nbt.putDouble("followSpeed", followSpeed);
        if (attack)
            nbt.putBoolean("attack", true);
        if (originalType != null)
            nbt.putString("originalType", originalType);

        return nbt;
    }

    @Override
    public void setNbt(NbtCompound nbt) {

        if (nbt.contains("posTarget")) {
            int[] posArray = nbt.getIntArray("posTarget");
            posTarget = new BlockPos(posArray[0], posArray[1], posArray[2]);
        }
//        if (nbt.contains("playerTarget")) {
//            entityTarget = (LivingEntity)((ServerWorld)getWorld()).getEntity(nbt.getUuid("playerTarget"));
//        } else if (nbt.contains("entityTarget"))
//            entityTarget = (LivingEntity)getWorld().getEntityById(nbt.getInt("entityTarget"));
        if (nbt.contains("entityTarget")) {
            targetUuid = nbt.getUuid("entityTarget");
        }
        if (nbt.contains("followRange"))
            followRange = nbt.getInt("followRange");
        if (nbt.contains("followSpeed"))
            followSpeed = nbt.getDouble("followSpeed");
        if (nbt.contains("attack"))
            attack = true;
        if (nbt.contains("originalType"))
            originalType = nbt.getString("originalType");
    }

    boolean inRange = false;
    @Inject(method="tick", at=@At("HEAD"))
    public void continuousAI(CallbackInfo ci) {
        if (posTarget != null && posTarget != getNavigation().getTargetPos()) {
            getNavigation().startMovingAlong(getNavigation().findPathTo(posTarget, 0), followSpeed);
        }
        else if (entityTarget != null) {
            if (attack && target != entityTarget) {
                setTarget(entityTarget);
            } else {
                if (!getBlockPos().isWithinDistance(entityTarget.getBlockPos(), followRange)) {
                    getNavigation().startMovingAlong(getNavigation().findPathTo(entityTarget.getBlockPos(), 0), followSpeed);
                    inRange = false;
                } else if (!inRange) {
                    inRange = true;
                    getNavigation().stop();
                }

            }
        } else if (targetUuid != null) {
            entityTarget = (LivingEntity)((ServerWorld)getWorld()).getEntity(targetUuid);
        }
    }

    @Override
    public void attack(LivingEntity entity) {
        targetUuid = entity.getUuid();
        entityTarget = entity;
        posTarget = null;
        followRange = 0;
        attack = true;
    }

    @Override
    public void follow(LivingEntity entity, int range, double speed) {
        targetUuid = entity.getUuid();
        entityTarget = entity;
        posTarget = null;
        followRange = range;
        attack = false;
        followSpeed = speed;
    }

    @Override
    public void follow(LivingEntity entity, int range) {
        follow(entity, range, 1);
    }

    @Override
    public void follow(LivingEntity entity) {
        follow(entity, 0, 1);
    }

    @Override
    public void walkTo(BlockPos pos) {
        walkTo(pos, 1);
    }

    @Override
    public void walkTo(BlockPos pos, double speed) {
        targetUuid = null;
        entityTarget = null;
        posTarget = pos;
        followRange = 0;
        attack = false;
        followSpeed = speed;
    }

    @Override
    public void clearTargets() {
        getNavigation().stop();
        targetUuid = null;
        entityTarget = null;
        posTarget = null;
        followRange = 0;
        attack = false;
    }

    @Override
    public void setOriginalType(String type) {
        originalType = type;
    }

    @Override
    public String getOriginalType() {
        return originalType;
    }
}
