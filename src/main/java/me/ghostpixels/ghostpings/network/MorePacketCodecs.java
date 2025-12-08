package me.ghostpixels.ghostpings.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.UUID;

public class MorePacketCodecs {

    public static PacketCodec<PacketByteBuf, UUID> UUID = new PacketCodec<>() {
        public UUID decode(PacketByteBuf byteBuf) {
            return byteBuf.readUuid();
        }

        public void encode(PacketByteBuf byteBuf, UUID uuid) {
            byteBuf.writeUuid(uuid);
        }
    };
}
