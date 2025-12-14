package me.ghostpixels.ghostpings.rendering;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.Vec2f;

public class ScreenLocation {
    @Getter @Setter
    int x;
    @Getter @Setter
    int y;
    @Getter @Setter
    int depth;

    public ScreenLocation(int x, int y, int depth) {
        this.x = x;
        this.y = y;
        this.depth = depth;
    }

    public boolean isInFront() {
        return depth > 0;
    }

    public boolean isWithinBounds(Vec2f topLeft, Vec2f bottomRight) {
        return
                (topLeft.x <= x && x <= bottomRight.x)
                        &&
                        (topLeft.y <= y && y <= bottomRight.y);
    }
}
