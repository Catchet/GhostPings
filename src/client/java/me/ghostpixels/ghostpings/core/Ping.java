package me.ghostpixels.ghostpings.core;

import lombok.Getter;
import me.ghostpixels.ghostpings.rendering.ScreenLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class Ping {
    @Getter
    Vec3d worldPos;
    @Getter
    long timeCreatedMs;
    @Getter
    UUID creator;

    @Getter
    ScreenLocation screenLocation;  // Potentially move to another class for rendering?
    @Getter
    int hudRenderSize = 8;          // This too.

    public Ping(Vec3d worldPos, long timeCreatedMs, UUID creator) {
        this.worldPos = worldPos;
        this.timeCreatedMs = timeCreatedMs;
        this.creator = creator;
    }

    public boolean isVisible() { // Only for the HUD element, also refactor this
        final var window = MinecraftClient.getInstance().getWindow();
        final int windowWidth = window.getWidth();
        final int windowHeight = window.getHeight();
        return
            screenLocation != null
            &&
            screenLocation.isInFront()
            &&
            screenLocation.isWithinBounds(
                    new Vec2f(0 - hudRenderSize / 2f, 0 - hudRenderSize / 2f),
                    new Vec2f(windowWidth + hudRenderSize / 2f, windowHeight + hudRenderSize / 2f)
            );
    }
}
