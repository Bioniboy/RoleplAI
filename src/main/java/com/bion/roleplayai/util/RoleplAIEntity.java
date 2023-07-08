package com.bion.roleplayai.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public interface RoleplAIEntity {
    void attack(LivingEntity entity);
    void follow(LivingEntity entity, int range, double speed);
    void follow(LivingEntity entity, int range);
    void follow(LivingEntity entity);
    void walkTo(BlockPos pos, double speed);
    void walkTo(BlockPos pos);
    void clearTargets();
    void setOriginalType(String type);
    String getOriginalType();
    NbtCompound getNbt();
    void setNbt(NbtCompound nbt);
}
