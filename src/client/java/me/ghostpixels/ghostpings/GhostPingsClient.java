package me.ghostpixels.ghostpings;

import me.ghostpixels.ghostpings.core.Ping;
import me.ghostpixels.ghostpings.hud.HudRendering;
import me.ghostpixels.ghostpings.network.PacketPayloads.ChannelRegistrationC2SPayload;
import me.ghostpixels.ghostpings.network.PacketPayloads.PingBroadcastS2CPayload;
import me.ghostpixels.ghostpings.network.PacketPayloads.PingCreatedC2SPayload;
import me.ghostpixels.ghostpings.rendering.CustomRenderPipeline;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class GhostPingsClient implements ClientModInitializer {
    public static Vec3d pingLocation = new Vec3d(0, 0, 0);
    public static Vec2f pingScreenLocation = new Vec2f(0, 0);
    public static boolean behindPlayer = false;

    private static KeyBinding pingKeyBinding;
    private static final KeyBinding.Category PING_KEY_CATEGORY = KeyBinding.Category.create(Identifier.of(GhostPings.MOD_ID, "pings"));

    public static final ArrayList<Ping> ACTIVE_PINGS = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        new CustomRenderPipeline().initialize();

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

        pingKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + GhostPings.MOD_ID + ".ping", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_C, // The keycode of the key
                PING_KEY_CATEGORY // The category of the key - you'll need to add a translation for this!
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (pingKeyBinding.wasPressed()) {
                if (client.player != null) {
                    double maxReach = 1000; // The farthest target the cameraEntity can detect
                    float tickDelta = 1.0F; // Used for tracking animation progress; no tracking is 1.0F
                    boolean includeFluids = false; // Whether to detect fluids as blocks

                    HitResult hit = client.getCameraEntity().raycast(maxReach, tickDelta, includeFluids);
                    switch (hit.getType()) {
                        case HitResult.Type.MISS:
                            client.player.sendMessage(Text.literal("Nothing in range..."), true);
                            break;
                        case HitResult.Type.BLOCK:
                            Vec3d pos = hit.getPos();
                            pingLocation = pos;
                            client.player.sendMessage(Text.literal("Ping key was pressed! (" + Util.getMeasuringTimeMs() + ")"), false);
                            if (((int) pos.getY()) % 2 == 0) { // For debugging
                                PingCreatedC2SPayload payload = new PingCreatedC2SPayload(pos, 0xFFFF8000, 0xFFFF0000);
                                ClientPlayNetworking.send(payload);
                                Ping newPing = new Ping(pos, client.getRenderTime(), client.player.getUuid());
                                ACTIVE_PINGS.add(newPing);
                            }
                            break;
                        case HitResult.Type.ENTITY:
                            // Can't seem to get here
                            break;
                    }
                }
            }
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, Identifier.of(GhostPings.MOD_ID, "ping_hud"), HudRendering::render);

        // TODO: Add server specific channels
        var payload = new ChannelRegistrationC2SPayload("channel1");
        ClientPlayNetworking.send(payload);
    }
}
