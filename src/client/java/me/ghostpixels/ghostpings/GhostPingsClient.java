package me.ghostpixels.ghostpings;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GhostPingsClient implements ClientModInitializer {

    public static Vec3d pingLocation = new Vec3d(0, 0, 0);

	private static KeyBinding pingKeyBinding;
	private static final KeyBinding.Category PING_KEY_CATEGORY = KeyBinding.Category.create(Identifier.of(GhostPings.MOD_ID, "pings"));

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

        new CustomRenderPipeline().initialize();

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
//                    MinecraftClient client = MinecraftClient.getInstance();
//                    HitResult hit = client.crosshairTarget;
                    double maxReach = 1000; //The farthest target the cameraEntity can detect
                    float tickDelta = 1.0F; //Used for tracking animation progress; no tracking is 1.0F
                    boolean includeFluids = false; //Whether to detect fluids as blocks


                    HitResult hit = client.getCameraEntity().raycast(maxReach, tickDelta, includeFluids);
                    Vec3d screen_normal_vec = client.player.getRotationVecClient();

                    switch(hit.getType()) {
                        case HitResult.Type.MISS:
                            //nothing near enough
                            client.player.sendMessage(Text.literal("Missed..."), true);
                            break;
                        case HitResult.Type.BLOCK:
                            BlockHitResult blockHit = (BlockHitResult) hit;
                            BlockPos blockPos = blockHit.getBlockPos();
                            BlockState blockState = client.world.getBlockState(blockPos);
                            Block block = blockState.getBlock();

                            client.player.sendMessage(Text.literal("Block is: " + block.toString()), true);
                            pingLocation = hit.getPos();
                            WorldRenderEvents.BEFORE_TRANSLUCENT.register(CustomRenderPipeline.getInstance()::extractAndDrawWaypoint);
                            break;
                        case HitResult.Type.ENTITY:
                            EntityHitResult entityHit = (EntityHitResult) hit;
                            Entity entity = entityHit.getEntity();

                            client.player.sendMessage(Text.literal("Entity is: " + entity.toString()), true);
                            break;
                    }

                    //client.player.sendMessage(Text.literal("Ping key was pressed!"), false);
                }
            }
		});

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(GhostPings.MOD_ID, "before_chat"), GhostPingsClient::render);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(GhostPings.MOD_ID, "ping"), GhostPingsClient::render2);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        int color = 0xFFFF0000; // Red
        int targetColor = 0xFF00FF00; // Green

        // You can use the Util.getMeasuringTimeMs() function to get the current time in milliseconds.
        // Divide by 1000 to get seconds.
        float currentTime = Util.getMeasuringTimeMs() / 1000f;

        // "lerp" simply means "linear interpolation", which is a fancy way of saying "blend".
        //float lerpedAmount = MathHelper.abs(MathHelper.sin(currentTime));
        float lerpedAmount = (1f + MathHelper.sin(currentTime)) / 2f;
        int lerpedColor = ColorHelper.lerp(lerpedAmount, color, targetColor);

        int pos_x = 0;
        int pos_y = 0;
        int size = 30;

        pos_x += (int) (lerpedAmount * (context.getScaledWindowWidth() - size));
        pos_y += (int) (lerpedAmount * (context.getScaledWindowHeight() - size));

        // Draw a square with the lerped color.
        // x1, y1, x2, y2, color
        context.fill(pos_x, pos_y, pos_x + size, pos_y + size, lerpedColor);
    }

    private static void render2(DrawContext context, RenderTickCounter tickCounter) {
        int color = 0xFFFF0000; // Red
        int targetColor = 0xFF00FF00; // Green

        // You can use the Util.getMeasuringTimeMs() function to get the current time in milliseconds.
        // Divide by 1000 to get seconds.
        float currentTime = Util.getMeasuringTimeMs() / 1000f;

        // "lerp" simply means "linear interpolation", which is a fancy way of saying "blend".
        float lerpedAmount = (1f + MathHelper.sin(currentTime)) / 2f;
        int lerpedColor = ColorHelper.lerp(lerpedAmount, color, targetColor);

        Vec3d n = MinecraftClient.getInstance().getCameraEntity().getRotationVecClient();
        Vec3d x =  pingLocation.subtract(MinecraftClient.getInstance().player.getEntityPos());
        Vec3d x_n = n.multiply(x.dotProduct(n));
        Vec3d x_proj = x.subtract(x_n);

        // TODO: Calculate angle from y-axis

        int pos_x = 0;
        int pos_y = 0;
        int size = 30;

        // Draw a square with the lerped color.
        // x1, y1, x2, y2, color
        context.fill(pos_x, pos_y, pos_x + size, pos_y + size, lerpedColor);
    }
}
