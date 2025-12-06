package me.ghostpixels.ghostpings.network;

import me.ghostpixels.ghostpings.GhostPings;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class PacketPayloads {
    // From Fabric Docs on networking
    public record PingCreatedC2SPayload(Vec3d pos, int argbPrimary, int argbSecondary) implements CustomPayload {
        public static final Identifier PING_CREATED_PAYLOAD_ID = Identifier.of(GhostPings.MOD_ID, "ping_created_c2s");
        public static final CustomPayload.Id<PingCreatedC2SPayload> ID = new CustomPayload.Id<>(PING_CREATED_PAYLOAD_ID);
        public static final PacketCodec<RegistryByteBuf, PingCreatedC2SPayload> CODEC = PacketCodec.tuple(
                Vec3d.PACKET_CODEC, PingCreatedC2SPayload::pos,
                PacketCodecs.INTEGER, PingCreatedC2SPayload::argbPrimary,
                PacketCodecs.INTEGER, PingCreatedC2SPayload::argbSecondary,
                PingCreatedC2SPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId(){
            return ID;
        }
    }

    public record PingBroadcastS2CPayload(UUID playerUuid, Vec3d pos, int argbPrimary, int argbSecondary) implements CustomPayload {
        public static final Identifier PING_BROADCAST_PAYLOAD_ID = Identifier.of(GhostPings.MOD_ID, "ping_broadcast_s2c");
        public static final CustomPayload.Id<PingBroadcastS2CPayload> ID = new CustomPayload.Id<>(PING_BROADCAST_PAYLOAD_ID);
        public static final PacketCodec<RegistryByteBuf, PingBroadcastS2CPayload> CODEC = PacketCodec.tuple(
                MorePacketCodecs.UUID, PingBroadcastS2CPayload::playerUuid,
                Vec3d.PACKET_CODEC, PingBroadcastS2CPayload::pos,
                PacketCodecs.INTEGER, PingBroadcastS2CPayload::argbPrimary,
                PacketCodecs.INTEGER, PingBroadcastS2CPayload::argbSecondary,
                PingBroadcastS2CPayload::new
        );


        @Override
        public Id<? extends CustomPayload> getId(){
            return ID;
        }
    }
}