package me.ghostpixels.ghostpings.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import static me.ghostpixels.ghostpings.GhostPingsClient.behindPlayer;

public class RenderingHelpers {
    public static Vec2f projectToScreen(Vec3d worldPos, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        MinecraftClient client = MinecraftClient.getInstance();
        var cameraPos = client.gameRenderer.getCamera().getCameraPos();
        var worldPosRel = new Vector4f(worldPos.subtract(cameraPos).toVector3f(), 1f);
        worldPosRel.mul(modelViewMatrix);
        worldPosRel.mul(projectionMatrix);

        var depth = worldPosRel.w;
        if (depth != 0) {
            worldPosRel.div(depth);
        }
        behindPlayer = depth < 0;

        return new Vec2f(
                client.getWindow().getScaledWidth() * (0.5f + worldPosRel.x * 0.5f),
                client.getWindow().getScaledHeight() * (0.5f - worldPosRel.y * 0.5f)
        );
    }
}
