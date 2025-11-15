package me.ghostpixels.ghostpings.network;

import me.ghostpixels.ghostpings.GhostPings;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PacketPayloads {
    // From Fabric Docs on networking
    public record SummonLightningS2CPayload(BlockPos pos) implements CustomPayload {
        public static final Identifier SUMMON_LIGHTNING_PAYLOAD_ID = Identifier.of(GhostPings.MOD_ID, "summon_lightning_s2c");
        public static final CustomPayload.Id<SummonLightningS2CPayload> ID = new CustomPayload.Id<>(SUMMON_LIGHTNING_PAYLOAD_ID);
        public static final PacketCodec<RegistryByteBuf, SummonLightningS2CPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, SummonLightningS2CPayload::pos, SummonLightningS2CPayload::new);

        @Override
        public Id<? extends CustomPayload> getId(){
            return ID;
        }
    }

    public record SummonLightningC2SPayload(BlockPos pos) implements CustomPayload {
        public static final Identifier SUMMON_LIGHTNING_PAYLOAD_ID = Identifier.of(GhostPings.MOD_ID, "summon_lightning_c2s");
        public static final CustomPayload.Id<SummonLightningC2SPayload> ID = new CustomPayload.Id<>(SUMMON_LIGHTNING_PAYLOAD_ID);
        public static final PacketCodec<RegistryByteBuf, SummonLightningC2SPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, SummonLightningC2SPayload::pos, SummonLightningC2SPayload::new);

        @Override
        public Id<? extends CustomPayload> getId(){
            return ID;
        }
    }
}