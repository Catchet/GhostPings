package me.ghostpixels.ghostpings.rendering;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.ghostpixels.ghostpings.GhostPings;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

import static me.ghostpixels.ghostpings.GhostPings.LOGGER;
import static me.ghostpixels.ghostpings.GhostPingsClient.pingLocation;

// From the Fabric Documentation on Rendering the World
public class CustomRenderPipeline {
    public static CustomRenderPipeline instance;
    // :::custom-pipelines:define-pipeline
    private static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation(Identifier.of(GhostPings.MOD_ID, "pipeline/ping_boxes"))
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .build()
    );
    // :::custom-pipelines:define-pipeline
    // :::custom-pipelines:extraction-phase
    private static final BufferAllocator allocator = new BufferAllocator(RenderLayer.CUTOUT_BUFFER_SIZE);
    private BufferBuilder buffer;

    // :::custom-pipelines:extraction-phase
    // :::custom-pipelines:drawing-phase
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private MappableRingBuffer vertexBuffer;

    // :::custom-pipelines:drawing-phase
    public static CustomRenderPipeline getInstance() {
        return instance;
    }

    public void initialize() {
        instance = this;
        WorldRenderEvents.AFTER_ENTITIES.register(this::extractAndDrawWaypoint);

        LOGGER.info("Initialized World Rendering Pipeline!");
    }

    public void extractAndDrawWaypoint(WorldRenderContext context) {
        renderWaypoint(context);
        drawFilledThroughWalls(MinecraftClient.getInstance(), FILLED_THROUGH_WALLS);
    }

    // :::custom-pipelines:extraction-phase
    private void renderWaypoint(WorldRenderContext context) {
        MatrixStack matrices = context.matrices();
        Vec3d camera = context.worldState().cameraRenderState.pos;

        assert matrices != null;
        matrices.push();
        {
            matrices.translate(-camera.x, -camera.y, -camera.z);

            if (buffer == null) {
                buffer = new BufferBuilder(allocator, FILLED_THROUGH_WALLS.getVertexFormatMode(), FILLED_THROUGH_WALLS.getVertexFormat());
//              BufferBuilder buffer2 = new BufferBuilder(allocator, RenderPipeline.)
//              buffer = new BufferBuilder(allocator, RenderPipelines.DEBUG_FILLED_BOX.getVertexFormatMode(), RenderPipelines.DEBUG_FILLED_BOX.getVertexFormat());
            }

//          VertexRendering.drawFilledBox(matrices, buffer, 0f, 100f, 0f, 1f, 101f, 1f, 0f, 1f, 0f, 0.5f);
            VertexRendering.drawFilledBox(matrices, buffer, -160f, 65f, 200f, -159.5f, 65.5f, 200.5f, 0f, 1f, 0f, 0.02f);
            double size = 1. / 2;
            double x = pingLocation.getX() - size / 2;
            double y = pingLocation.getY() - size / 2;
            double z = pingLocation.getZ() - size / 2;
            float startAlpha = 0.035f;
            matrices.push();
            {
                float currentTime = Util.getMeasuringTimeMs() / 1000f;
//              matrices.translate(0, 1.5, 0);
                matrices.multiply(RotationAxis.POSITIVE_X.rotation(currentTime * .3f), (float) pingLocation.getX(), (float) pingLocation.getY(), (float) pingLocation.getZ());
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(currentTime * .4f), (float) pingLocation.getX(), (float) pingLocation.getY(), (float) pingLocation.getZ());
                matrices.multiply(RotationAxis.POSITIVE_Z.rotation(currentTime * .5f), (float) pingLocation.getX(), (float) pingLocation.getY(), (float) pingLocation.getZ());
                VertexRendering.drawFilledBox(matrices, buffer, x, y, z, x + size, y + size, z + size, 1f, .5f, 0f, startAlpha);

//              VertexRendering.drawBox(matrices.peek(), buffer, x, y + 1, z, x + 1, y + 2, z + 1, 0f, 0f, 1f, 0.2f);
//              VoxelShape shape = VoxelShapes.cuboid(x, y + 1, z, x + 1, y + 2, z + 1);
//              VertexRendering.drawOutline(matrices, buffer, shape, 0, 0, 0, 0x300000FF);
//
//              VertexRendering.drawVector(matrices, buffer, pingLocation.toVector3f(), new Vec3d(3, 3, 3), 0xFFFFFFFF);
//
//              VertexRendering.drawSide(matrices.peek().getPositionMatrix(), buffer, Direction.NORTH, (float) x, (float) y, (float) z, (float) x + 1, (float) y + 1, (float) z, 1f, 1f, 1f, 1f);
//
//              context.modelViewMatrix

                float alpha = 2 * startAlpha * (1 - (currentTime % 2f));
                if (alpha > 0f) {
                    size += size * (currentTime % 1f);
                    x = pingLocation.getX() - size / 2;
                    y = pingLocation.getY() - size / 2;
                    z = pingLocation.getZ() - size / 2;
                    VertexRendering.drawFilledBox(matrices, buffer, x, y, z, x + size, y + size, z + size, 1f, 0f, 0f, alpha);
                }
            }
            matrices.pop();
        }
        matrices.pop();
    }
    // :::custom-pipelines:extraction-phase

    // :::custom-pipelines:drawing-phase
    private void drawFilledThroughWalls(MinecraftClient client, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline) {
        // Build the buffer
        BuiltBuffer builtBuffer = buffer.end();
        BuiltBuffer.DrawParameters drawParameters = builtBuffer.getDrawParameters();
        VertexFormat format = drawParameters.format();

        GpuBuffer vertices = upload(drawParameters, format, builtBuffer);

        draw(client, pipeline, builtBuffer, drawParameters, vertices, format);

        // Rotate the vertex buffer so we are less likely to use buffers that the GPU is using
        vertexBuffer.rotate();
        buffer = null;
    }

    private GpuBuffer upload(BuiltBuffer.DrawParameters drawParameters, VertexFormat format, BuiltBuffer builtBuffer) {
        // Calculate the size needed for the vertex buffer
        int vertexBufferSize = drawParameters.vertexCount() * format.getVertexSize();

        // Initialize or resize the vertex buffer as needed
        if (vertexBuffer == null || vertexBuffer.size() < vertexBufferSize) {
            vertexBuffer = new MappableRingBuffer(() -> GhostPings.MOD_ID + " example render pipeline", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE, vertexBufferSize);
        }

        // Copy vertex data into the vertex buffer
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

        try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(vertexBuffer.getBlocking().slice(0, builtBuffer.getBuffer().remaining()), false, true)) {
            MemoryUtil.memCopy(builtBuffer.getBuffer(), mappedView.data());
        }

        return vertexBuffer.getBlocking();
    }

    private static void draw(MinecraftClient client, RenderPipeline pipeline, BuiltBuffer builtBuffer, BuiltBuffer.DrawParameters drawParameters, GpuBuffer vertices, VertexFormat format) {
        GpuBuffer indices;
        VertexFormat.IndexType indexType;

        if (pipeline.getVertexFormatMode() == VertexFormat.DrawMode.QUADS) {
            // Sort the quads if there is translucency
            builtBuffer.sortQuads(allocator, RenderSystem.getProjectionType().getVertexSorter());
            // Upload the index buffer
            indices = pipeline.getVertexFormat().uploadImmediateIndexBuffer(builtBuffer.getSortedBuffer());
            indexType = builtBuffer.getDrawParameters().indexType();
        } else {
            // Use the general shape index buffer for non-quad draw modes
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            indices = shapeIndexBuffer.getIndexBuffer(drawParameters.indexCount());
            indexType = shapeIndexBuffer.getIndexType();
        }

        // Actually execute the draw
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .write(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR, new Vector3f(), RenderSystem.getTextureMatrix(), 1f);
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> GhostPings.MOD_ID + " ping box pipeline rendering", client.getFramebuffer().getColorAttachmentView(), OptionalInt.empty(), client.getFramebuffer().getDepthAttachmentView(), OptionalDouble.empty())) {
            renderPass.setPipeline(pipeline);

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            // Bind texture if applicable:
            // Sampler0 is used for texture inputs in vertices
            // renderPass.bindSampler("Sampler0", textureView);

            renderPass.setVertexBuffer(0, vertices);
            renderPass.setIndexBuffer(indices, indexType);

            // The base vertex is the starting index when we copied the data into the vertex buffer divided by vertex size
            //noinspection ConstantValue
            renderPass.drawIndexed(0 / format.getVertexSize(), 0, drawParameters.indexCount(), 1);
        }

        builtBuffer.close();
    }
    // :::custom-pipelines:drawing-phase

    // :::custom-pipelines:clean-up
    public void close() {
        allocator.close();

        if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }
    }
    // :::custom-pipelines:clean-up
}