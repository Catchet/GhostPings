package me.ghostpixels.ghostpings;

import me.ghostpixels.ghostpings.network.PacketPayloads.PingBroadcastS2CPayload;
import me.ghostpixels.ghostpings.network.PacketPayloads.PingCreatedC2SPayload;

import org.lwjgl.glfw.GLFW;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Colors;
import net.minecraft.util.math.Vec3d;

public class GhostPingsClient implements ClientModInitializer {

    private static KeyBinding pingKeyBinding;
    private static final KeyBinding.Category PING_KEY_CATEGORY = KeyBinding.Category.create(Identifier.of(GhostPings.MOD_ID, "pings"));

    @Override
    public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		// From Fabric Docs on networking
		ClientPlayNetworking.registerGlobalReceiver(PingBroadcastS2CPayload.ID, (payload, context) -> {
            ClientWorld world = context.client().world;

            if (world == null) {
                return;
            }

            context.client().player.sendMessage(Text.literal(
                    "Received packet! (" + Util.getMeasuringTimeMs() + ")" +
                    "\nSent by: " + payload.playerUuid() +
                    "\nPos: " + payload.pos().toString() +
                    "\nPrimary colour: " + new ColorCode(payload.argbPrimary()) +
                    "\nSecondary colour: " + new ColorCode(payload.argbSecondary())
            ).withColor(Colors.GRAY), false);
        });

        // From Fabric wiki on custom keybinds
        pingKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + GhostPings.MOD_ID + ".ping", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_C, // The keycode of the key
                PING_KEY_CATEGORY // The category of the key - you'll need to add a translation for this!
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (pingKeyBinding.wasPressed()) {
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Ping key was pressed! (" + Util.getMeasuringTimeMs() + ")"), false);
                    Vec3d pos = client.player.getEntityPos();
                    if (pos.getY() % 2 == 0) { // For debugging
                        PingCreatedC2SPayload payload = new PingCreatedC2SPayload(pos, 0xFFFF8000, 0xFFFF0000);
                        ClientPlayNetworking.send(payload);
                    }
                }
            }
        });
	}
}