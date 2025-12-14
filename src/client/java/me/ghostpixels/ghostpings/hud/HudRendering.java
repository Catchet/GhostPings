package me.ghostpixels.ghostpings.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import static me.ghostpixels.ghostpings.GhostPingsClient.behindPlayer;
import static me.ghostpixels.ghostpings.GhostPingsClient.pingScreenLocation;

public class HudRendering {
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (behindPlayer) return;

        int size = 8;
        int posX = (int) pingScreenLocation.x - size / 2;
        int posY = (int) pingScreenLocation.y - size / 2;

        var playerInfo = MinecraftClient.getInstance().getNetworkHandler().getListedPlayerListEntries().iterator().next(); // TODO: Get by UUID
        var texture = playerInfo.getSkinTextures().body().texturePath();
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, posX, posY, 8, 8, 8, 8, 64, 64, 0xC0FFFFFF);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, posX, posY, 0, 0, 40, 8, 8, 8, 64, 64, 0xC0FFFFFF);
    }
}
