package me.ghostpixels.ghostpings.mixin.client;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.WorldRenderer;

import static me.ghostpixels.ghostpings.GhostPingsClient.pingLocation;
import static me.ghostpixels.ghostpings.GhostPingsClient.pingScreenLocation;
import static me.ghostpixels.ghostpings.rendering.RenderingHelpers.projectToScreen;

@Mixin(WorldRenderer.class)
public abstract class ClientRenderMixin {

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/WorldRenderer;client:Lnet/minecraft/client/MinecraftClient;", ordinal = 7, shift = At.Shift.AFTER))
    public void onRender(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
        pingScreenLocation = projectToScreen(pingLocation, positionMatrix, matrix4f);
	}
}