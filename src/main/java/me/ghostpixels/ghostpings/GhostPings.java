package me.ghostpixels.ghostpings;

import me.ghostpixels.ghostpings.network.PacketPayloads.SummonLightningS2CPayload;
import me.ghostpixels.ghostpings.network.PacketPayloads.SummonLightningC2SPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhostPings implements ModInitializer {
	public static final String MOD_ID = "ghostpings";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		PayloadTypeRegistry.playS2C().register(SummonLightningS2CPayload.ID, SummonLightningS2CPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SummonLightningC2SPayload.ID, SummonLightningC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SummonLightningC2SPayload.ID, (payload_incoming, context) -> {
            BlockPos pos = payload_incoming.pos();
            if (pos.getY() % 2 != 0) return; // For debugging

            SummonLightningS2CPayload payload_outgoing = new SummonLightningS2CPayload(pos);
            for (ServerPlayerEntity player :
                    PlayerLookup.tracking(context.player().getEntityWorld(), pos))
                ServerPlayNetworking.send(player, payload_outgoing);
        });
	}
}