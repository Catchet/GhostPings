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

import static me.ghostpixels.ghostpings.GhostPings.LOGGER;

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
        HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, Identifier.of(GhostPings.MOD_ID, "ping_hud"), GhostPingsClient::render3);
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

        int posX = 0;
        int posY = 0;
        int size = 30;

        posX += (int) (lerpedAmount * (context.getScaledWindowWidth() - size));
        posY += (int) (lerpedAmount * (context.getScaledWindowHeight() - size));

        // Draw a square with the lerped color.
        // x1, y1, x2, y2, color
        context.fill(posX, posY, posX + size, posY + size, lerpedColor);
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

        int posX = 0;
        int posY = 0;
        int size = 30;

        // Draw a square with the lerped color.
        // x1, y1, x2, y2, color
        context.fill(posX, posY, posX + size, posY + size, lerpedColor);
    }

    private static void render3(DrawContext context, RenderTickCounter tickCounter) {
        int color = 0xFFFF0000; // Red
        int targetColor = 0xFF00FF00; // Green

        // You can use the Util.getMeasuringTimeMs() function to get the current time in milliseconds.
        // Divide by 1000 to get seconds.
        float currentTime = Util.getMeasuringTimeMs() / 1000f;

        // "lerp" simply means "linear interpolation", which is a fancy way of saying "blend".
        //float lerpedAmount = MathHelper.abs(MathHelper.sin(currentTime));
//        float lerpedAmount = (1f + MathHelper.sin(currentTime)) / 2f;
//        int lerpedColor = ColorHelper.lerp(lerpedAmount, color, targetColor);

        int windowWidth = context.getScaledWindowWidth();
        int windowHeight = context.getScaledWindowHeight();
        float aspectRatio = (float) windowWidth / (float) windowHeight;
        float defaultAspectRatio = 16f / 9f;
        int fov = MinecraftClient.getInstance().options.getFov().getValue();
        float visualAngleX = ((float) fov / defaultAspectRatio) * aspectRatio;
        float visualAngleY = (float) fov / defaultAspectRatio;

        Vec3d n = MinecraftClient.getInstance().getCameraEntity().getRotationVecClient();
        Vec3d p =  pingLocation.subtract(MinecraftClient.getInstance().getCameraEntity().getCameraPosVec(tickCounter.getTickProgress(false)));
        Vec3d diff = p.subtract(p.projectOnto(n));

        float nDotPX = (float) (n.getX() * p.getX() + n.getZ() * p.getZ());
        float pLenX = MathHelper.sqrt((float) (p.getX() * p.getX() + p.getZ() * p.getZ()));
        float nLenX = MathHelper.sqrt((float) (n.getX() * n.getX() + n.getZ() * n.getZ()));
        float vX = (float) ((Math.acos(nDotPX / (pLenX * nLenX)) / (2 * Math.PI)) * 360);
//        if (Math.asin(nDotPX / (pLenX * nLenX)) < 0)
//            vX = -vX;

        int size = 15;
        int midX = (windowWidth - size) / 2;
        int midY = (windowHeight - size) / 2;

        int posX = (int) (midX + (vX / (visualAngleX / 2f)) * (windowWidth / 4f)); //(int) (lerpedAmount * (context.getScaledWindowWidth() - size));
        int posY = midY; //(int) (lerpedAmount * (context.getScaledWindowHeight() - size));

        // Draw a square with the lerped color.
        // x1, y1, x2, y2, color
        context.fill(posX, posY, posX + size, posY + size, 0x800000FF);
    }
}
