package me.ghostpixels.ghostpings;

import me.ghostpixels.ghostpings.network.PacketPayloads.ChannelRegistrationC2SPayload;
import me.ghostpixels.ghostpings.network.PacketPayloads.PingBroadcastS2CPayload;
import me.ghostpixels.ghostpings.network.PacketPayloads.PingCreatedC2SPayload;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class GhostPings implements ModInitializer {
    public static final String MOD_ID = "ghostpings";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final HashMap<UUID, String> USER_CHANNELS = new HashMap<>();
    public static final HashMap<UUID, LinkedList<Long>> USER_PING_HISTORY = new HashMap<>();
    public static final Integer MAX_ACTIVE_PINGS = 3;
    public static final Integer PING_COOLDOWN_SECONDS = 5;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");

        PayloadTypeRegistry.playC2S().register(ChannelRegistrationC2SPayload.ID, ChannelRegistrationC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PingCreatedC2SPayload.ID, PingCreatedC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PingBroadcastS2CPayload.ID, PingBroadcastS2CPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ChannelRegistrationC2SPayload.ID, (payload, context) -> {
            var sender = context.player().getUuid();
            var channel = payload.channel();
            USER_CHANNELS.put(sender, channel);
        });

        ServerPlayNetworking.registerGlobalReceiver(PingCreatedC2SPayload.ID, (payloadIncoming, context) -> {
            Vec3d pos = payloadIncoming.pos();
            if (pos.getY() % 2 != 0) return; // For debugging

            var sender = context.player().getUuid();
            { // Handling cooldowns
                var now = System.currentTimeMillis();
                if (!USER_PING_HISTORY.containsKey(sender)) {
                    USER_PING_HISTORY.put(sender, new LinkedList<>());
                }
                var activePings = USER_PING_HISTORY.get(sender);
                var oldestPing = activePings.getFirst();
                var timeDelta = now - oldestPing;
                if (timeDelta > PING_COOLDOWN_SECONDS) {
                    activePings.removeFirst();
                }
                if (activePings.size() < MAX_ACTIVE_PINGS) {
                    activePings.add(now);
                } else {
                    return;
                }
            }
            var senderChannel = USER_CHANNELS.getOrDefault(sender, "");

            PingBroadcastS2CPayload payloadOutgoing = new PingBroadcastS2CPayload(
                    sender,
                    pos,
                    payloadIncoming.argbPrimary(),
                    payloadIncoming.argbSecondary()
            );
            var trackingPlayers = PlayerLookup.tracking(
                context.player().getEntityWorld(),
                new BlockPos((int) pos.getX(), (int) pos.getY(), (int) pos.getZ())
            );
            for (ServerPlayerEntity targetPlayer : trackingPlayers) {
                if (sender.equals(targetPlayer.getUuid())) continue;
                var targetChannel = USER_CHANNELS.getOrDefault(targetPlayer.getUuid(), "");
                if (senderChannel.equals(targetChannel))
                    ServerPlayNetworking.send(targetPlayer, payloadOutgoing);
            }
        });
    }
}