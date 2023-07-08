package com.bion.roleplayai.command;

import com.bion.roleplayai.mixin.accessor.MobEntityAccessor;
import com.bion.roleplayai.util.RoleplAIEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.disguiselib.api.EntityDisguise;


public class RoleplAICommand {
    private static final SimpleCommandExceptionType INVALID_TARGET_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Invalid target entity"));
    private static final SimpleCommandExceptionType INVALID_SOURCE_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Invalid source entity"));
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed"));
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess s, CommandManager.RegistrationEnvironment p) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(CommandManager.literal("roleplai")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("entity" +
                                "", EntityArgumentType.entity())
                        .then(CommandManager.literal("goto")
                                .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                        .executes(context -> pathfindTo(context.getSource(), EntityArgumentType.getEntity(context, "entity"), BlockPosArgumentType.getBlockPos(context, "pos"), 1, false))
                                        .then(CommandManager.argument("speed", DoubleArgumentType.doubleArg(0, 3))
                                                .executes(context -> pathfindTo(context.getSource(), EntityArgumentType.getEntity(context, "entity"), BlockPosArgumentType.getBlockPos(context, "pos"), DoubleArgumentType.getDouble(context, "speed"), false))
                                                .then(CommandManager.argument("force", BoolArgumentType.bool())
                                                        .executes(context -> pathfindTo(context.getSource(), EntityArgumentType.getEntity(context, "entity"), BlockPosArgumentType.getBlockPos(context, "pos"), DoubleArgumentType.getDouble(context, "speed"), BoolArgumentType.getBool(context, "force")))
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("attack")
                                .then(CommandManager.argument("target", EntityArgumentType.entity())
                                        .executes(context -> attack(context.getSource(), EntityArgumentType.getEntity(context, "entity"), EntityArgumentType.getEntity(context, "target"), false))
                                        .then(CommandManager.argument("force", BoolArgumentType.bool())
                                                .executes(context -> attack(context.getSource(), EntityArgumentType.getEntity(context, "entity"), EntityArgumentType.getEntity(context, "target"), BoolArgumentType.getBool(context, "force")))
                                        )
                                )
                        )
                        .then(CommandManager.literal("follow")
                                .then(CommandManager.argument("target", EntityArgumentType.entity())
                                        .executes(context -> follow(context.getSource(), EntityArgumentType.getEntity(context, "entity"), EntityArgumentType.getEntity(context, "target"), 0, 1))
                                        .then(CommandManager.argument("range", IntegerArgumentType.integer(0))
                                                .executes(context -> follow(context.getSource(), EntityArgumentType.getEntity(context, "entity"), EntityArgumentType.getEntity(context, "target"), IntegerArgumentType.getInteger(context, "range"), 1))
                                                .then(CommandManager.argument("speed", DoubleArgumentType.doubleArg(0, 3))
                                                        .executes(context -> follow(context.getSource(), EntityArgumentType.getEntity(context, "entity"), EntityArgumentType.getEntity(context, "target"), IntegerArgumentType.getInteger(context, "range"), DoubleArgumentType.getDouble(context, "speed")))
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("actas")
                                .then(CommandManager.argument("type", RegistryEntryArgumentType.registryEntry(s, RegistryKeys.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                        .executes(context -> actAs(context.getSource(), EntityArgumentType.getEntity(context, "entity"), RegistryEntryArgumentType.getSummonableEntityType(context, "type")))
                                )
                        )
                        .then(CommandManager.literal("reset")
                                .executes(context -> resetCommand(context.getSource(), EntityArgumentType.getEntity(context, "entity"), true, true))
                                .then(CommandManager.literal("behavior")
                                        .executes(context -> resetCommand(context.getSource(), EntityArgumentType.getEntity(context, "entity"), true, false))
                                )
                                .then(CommandManager.literal("goals")
                                        .executes(context -> resetCommand(context.getSource(), EntityArgumentType.getEntity(context, "entity"), false, true))
                                )
                        )
                ));
        dispatcher.register(CommandManager.literal("rai").requires(source -> source.hasPermissionLevel(2)).redirect(literalCommandNode));
    }

    private static int pathfindTo(ServerCommandSource source, Entity entity, BlockPos pos, double speed, boolean force) throws CommandSyntaxException {
        if (!(entity instanceof MobEntity))
            throw INVALID_SOURCE_EXCEPTION.create();
        source.sendFeedback(Text.literal("Set ")
                .append(entity.getDisplayName())
                .append(Text.literal(" to go to " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())), true);
        if (force) {
            ((RoleplAIEntity)entity).walkTo(pos, speed);
        } else {
            ((MobEntityAccessor) entity).getGoalSelector().enableControl(Goal.Control.MOVE);
            ((MobEntity)entity).getNavigation().startMovingAlong(((PathAwareEntity)entity).getNavigation().findPathTo(pos, 0), speed);
        }


        return 1;
    }

    private static int attack(ServerCommandSource source, Entity entity, Entity target, boolean force) throws CommandSyntaxException {
        if (!(entity instanceof MobEntity))
            throw INVALID_SOURCE_EXCEPTION.create();
        if (!(target instanceof LivingEntity))
            throw INVALID_TARGET_EXCEPTION.create();

        source.sendFeedback(Text.literal("Set ")
                .append(entity.getDisplayName())
                .append(Text.literal(" to attack "))
                .append(target.getDisplayName()), true);
        if (force) {
            ((RoleplAIEntity)entity).attack((LivingEntity) target);
        } else {
            ((MobEntityAccessor)entity).getGoalSelector().enableControl(Goal.Control.TARGET);
            ((MobEntity)entity).setTarget((LivingEntity) target);
        }
        return 1;
    }

    private static int follow(ServerCommandSource source, Entity entity, Entity target, int range, double speed) throws CommandSyntaxException {
        if (!(entity instanceof MobEntity))
            throw INVALID_SOURCE_EXCEPTION.create();
        if (!(target instanceof LivingEntity))
            throw INVALID_TARGET_EXCEPTION.create();

        source.sendFeedback(Text.literal("Set ")
                .append(entity.getDisplayName())
                .append(Text.literal(" to follow "))
                .append(target.getDisplayName())
                .append(Text.literal(" at range " + range)), true);
        ((RoleplAIEntity) entity).follow((LivingEntity) target, range, speed);

        return 1;
    }

    private static int actAs(ServerCommandSource source, Entity entity, RegistryEntry.Reference<EntityType<?>> entityType) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity && !(entity instanceof PlayerEntity)))
            throw INVALID_SOURCE_EXCEPTION.create();
        if (!(entityType.value().create(source.getWorld()) instanceof LivingEntity))
            throw INVALID_TARGET_EXCEPTION.create();

        if (((RoleplAIEntity) entity).getOriginalType() != null) {
            entity = reset(source, entity);
        }
        ((RoleplAIEntity) entity).setOriginalType(EntityType.getId(entity.getType()).toString());
        NbtCompound nbt = entity.writeNbt(new NbtCompound());

        nbt.putString("id", entityType.registryKey().getValue().toString());
        Entity finalEntity = entity;
        Entity spawnedEntity = EntityType.loadEntityWithPassengers(nbt, source.getWorld(), (entityx) -> {
            entityx.refreshPositionAndAngles(finalEntity.getX(), finalEntity.getY(), finalEntity.getZ(), entityx.getYaw(), entityx.getPitch());
            return entityx;
        });
        entity.discard();
        source.getWorld().spawnEntity(spawnedEntity);
        if (spawnedEntity == null) {
            throw FAILED_EXCEPTION.create();
        }
        ((EntityDisguise) spawnedEntity).disguiseAs(entity.getType());
        source.sendFeedback(Text.literal("Set ")
                .append(EntityType.get(((RoleplAIEntity) entity).getOriginalType()).get().getName())
                .append(Text.literal(" to act as "))
                .append(entityType.value().getName()), true);


        return 1;
    }
    private static int resetCommand(ServerCommandSource source, Entity entity, boolean behavior, boolean goals) throws CommandSyntaxException {
        if (!(entity instanceof PathAwareEntity)) {
            throw INVALID_SOURCE_EXCEPTION.create();
        }
        Entity spawnedEntity = entity;
        if (behavior) {

            if (((RoleplAIEntity) entity).getOriginalType() != null)
                spawnedEntity = reset(source, entity);
            if (!goals) {
                source.sendFeedback(Text.literal("Reset ")
                        .append(spawnedEntity.getDisplayName())
                        .append(Text.literal("'s behavior")), true);
            }
        }
        if (goals) {
            ((RoleplAIEntity)spawnedEntity).clearTargets();
            if (!behavior) {
                source.sendFeedback(Text.literal("Reset ")
                        .append(spawnedEntity.getDisplayName())
                        .append(Text.literal("'s goals")), true);
            }
        }
        if (behavior && goals) {
            source.sendFeedback(Text.literal("Reset ")
                    .append(spawnedEntity.getDisplayName())
                    .append(Text.literal("'s behavior and goals")), true);
        }
        return 1;
    }
    private static Entity reset(ServerCommandSource source, Entity entity) throws CommandSyntaxException {
        NbtCompound nbt = entity.writeNbt(new NbtCompound());

        nbt.putString("id", ((RoleplAIEntity) entity).getOriginalType());
        nbt.remove("NoGravity");
        nbt.remove("DisguiseLib");
        nbt.remove("RoleplAI");
        Entity spawnedEntity = EntityType.loadEntityWithPassengers(nbt, source.getWorld(), (entityx) -> {
            entityx.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entityx.getYaw(), entityx.getPitch());
            return entityx;
        });
        entity.discard();
        source.getWorld().spawnEntity(spawnedEntity);
        if (spawnedEntity == null) {
            throw FAILED_EXCEPTION.create();
        }
        return spawnedEntity;
    }
}
