package me.ghostpixels.ghostpings;

import me.ghostpixels.ghostpings.core.Ping;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;

public class GhostPingsClient implements ClientModInitializer {

	private static KeyBinding pingKeyBinding;
	private static final KeyBinding.Category PING_KEY_CATEGORY = KeyBinding.Category.create(Identifier.of(GhostPings.MOD_ID, "pings"));

    public static final ArrayList<Ping> ACTIVE_PINGS = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

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
                    client.player.sendMessage(Text.literal("Ping key was pressed!"), false);
                    Ping newPing = new Ping(client.player.getEntityPos(), client.getRenderTime(), client.player.getUuid());
                    ACTIVE_PINGS.add(newPing);
                }
            }
		});
	}
}