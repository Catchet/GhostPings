package me.ghostpixels.ghostpings;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhostPings implements ModInitializer {
	public static final String MOD_ID = "ghostpings";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		PayloadTypeRegistry.playS2C().register(SummonLightningS2CPayload.ID, SummonLightningS2CPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SummonLightningC2SPayload.ID, SummonLightningC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SummonLightningC2SPayload.ID, (payload_incoming, context) -> {
            if (payload_incoming.pos().getY() % 2 != 0) return; // For debugging

            SummonLightningS2CPayload payload_outgoing = new SummonLightningS2CPayload(payload_incoming.pos());
            for (ServerPlayerEntity player : PlayerLookup.world(context.player().getEntityWorld()))
                ServerPlayNetworking.send(player, payload_outgoing);
        });
	}
}