package me.ghostpixels.ghostpings;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GhostPingsClient implements ClientModInitializer {

	private static KeyBinding pingKeyBinding;
	private static final KeyBinding.Category PING_KEY_CATEGORY = KeyBinding.Category.create(Identifier.of(GhostPings.MOD_ID, "pings"));

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
			while (pingKeyBinding.wasPressed()) {
				client.player.sendMessage(Text.literal("Ping key was pressed!"), false);
			}
		});
	}
}