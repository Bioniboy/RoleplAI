//package com.bion.roleplayai.mixin;
//
//import com.bion.roleplayai.RoleplayAI;
//import net.minecraft.entity.Entity;
//import net.minecraft.network.PacketCallbacks;
//import net.minecraft.network.Packet;
//import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
//import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
//import net.minecraft.server.network.ServerPlayNetworkHandler;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//@Mixin(ServerPlayNetworkHandler.class)
//public class NetworkHandlerMixin {
//    @Inject(
//            method = "sendPacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V"
//            ),
//            cancellable = true
//    )
//    private void disguiseEntity(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
//        String className = packet.getClass().getSimpleName();
//        Entity entity = null;
////        if (packet instanceof EntityStatusS2CPacket statusPacket) {
////
////        }
//        String[] whitelist = {
//                "GameJoinS2CPacket",
//                "ChunkDataS2CPacket",
//                "PlayerSpawnPositionS2CPacket",
//                "PlayerPositionLookS2CPacket",
//
//                "BundleS2CPacket"
//
//
//
//        };
////        if (!Arrays.asList(whitelist).contains(className)) {
////            ci.cancel();
////            return;
////        }
////        if (!(className.equals("GameJoinS2CPacket") || className.equals("PlayerSpawnPositionS2CPacket") ||className.equals("PlayerPositionLookS2CPacket") || className.equals("ChunkDataS2CPacket"))) {
////            ci.cancel();
////            return;
////        }
////        if (className.equals("DeathMessageS2CPacket") || className.equals("HealthUpdateS2CPacket") || className.equals("EntityTrackerUpdateS2CPacket")) {
////            ci.cancel();
////            return;
////        }
//
//        if (packet instanceof BundleS2CPacket bundle) {
//            RoleplayAI.LOGGER.info("Bundle:");
//            for (Packet<?> pckt : bundle.getPackets()) {
//                RoleplayAI.LOGGER.info(pckt.getClass().getSimpleName());
//            }
//
//        }
//        if (className.contains("Entity")) {
//            RoleplayAI.LOGGER.info(className);
//        }
//
//    }
//}
