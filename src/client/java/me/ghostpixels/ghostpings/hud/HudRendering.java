package me.ghostpixels.ghostpings.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static me.ghostpixels.ghostpings.GhostPingsClient.*;

public class HudRendering {
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
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

    public static void render2(DrawContext context, RenderTickCounter tickCounter) {
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

    public static void render3(DrawContext context, RenderTickCounter tickCounter) {
        if (behindPlayer) return;

        int color = 0xFFFF0000; // Red
        int targetColor = 0xFF00FF00; // Green

        // You can use the Util.getMeasuringTimeMs() function to get the current time in milliseconds.
        // Divide by 1000 to get seconds.
        float currentTime = Util.getMeasuringTimeMs() / 1000f;

        // "lerp" simply means "linear interpolation", which is a fancy way of saying "blend".
        //float lerpedAmount = MathHelper.abs(MathHelper.sin(currentTime));
//        float lerpedAmount = (1f + MathHelper.sin(currentTime)) / 2f;
//        int lerpedColor = ColorHelper.lerp(lerpedAmount, color, targetColor);

//        int windowWidth = context.getScaledWindowWidth();
//        int windowHeight = context.getScaledWindowHeight();
//        float aspectRatio = (float) windowWidth / (float) windowHeight;
//        float defaultAspectRatio = 16f / 9f;
//        int fov = MinecraftClient.getInstance().options.getFov().getValue();
//        float visualAngleX = ((float) fov / defaultAspectRatio) * aspectRatio;
//        float visualAngleY = (float) fov / defaultAspectRatio;
//
//        Vec3d n = MinecraftClient.getInstance().getCameraEntity().getRotationVecClient();
//        Vec3d p =  pingLocation.subtract(MinecraftClient.getInstance().getCameraEntity().getCameraPosVec(tickCounter.getTickProgress(false)));
//        Vec3d diff = p.subtract(p.projectOnto(n));
//
//        float nDotPX = (float) (n.getX() * p.getX() + n.getZ() * p.getZ());
//        float pLenX = MathHelper.sqrt((float) (p.getX() * p.getX() + p.getZ() * p.getZ()));
//        float nLenX = MathHelper.sqrt((float) (n.getX() * n.getX() + n.getZ() * n.getZ()));
//        float vX = (float) ((Math.acos(nDotPX / (pLenX * nLenX)) / (2 * Math.PI)) * 360);
//        if (Math.asin(nDotPX / (pLenX * nLenX)) < 0)
//            vX = -vX;

        int size = 8;
//        int midX = (windowWidth - size) / 2;
//        int midY = (windowHeight - size) / 2;
//
//        int posX = (int) (midX + (vX / (visualAngleX / 2f)) * (windowWidth / 4f)); //(int) (lerpedAmount * (context.getScaledWindowWidth() - size));
//        int posY = midY; //(int) (lerpedAmount * (context.getScaledWindowHeight() - size));

        int posX = (int) pingScreenLocation.x - size / 2;
        int posY = (int) pingScreenLocation.y - size / 2;

        // Draw a square with the lerped color.
        // x1, y1, x2, y2, color
        //context.fill(posX, posY, posX + size, posY + size, 0x800000FF);
        // net.minecraft.client.network.PlayerListEntry
        var matrices = context.getMatrices();
        matrices.pushMatrix();
        {
            float scale = 1f; // 1.25f
            matrices.scale(scale);
//            matrices.scaleLocal(1.25f);
            var playerInfo = MinecraftClient.getInstance().getNetworkHandler().getListedPlayerListEntries().iterator().next();
            var texture = playerInfo.getSkinTextures().body().texturePath();
            context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, (int) (posX / scale), (int) (posY / scale), 8, 8, 8, 8, 64, 64, 0xC0FFFFFF);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, (int) (posX / scale), (int) (posY / scale), 0, 0, 40, 8, 8, 8, 64, 64, 0xC0FFFFFF);
        }
        matrices.popMatrix();
    }
}
