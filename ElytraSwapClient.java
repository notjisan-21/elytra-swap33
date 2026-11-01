package com.elytraswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ElytraSwapClient implements ClientModInitializer {
	public static final String MOD_ID = "elytraswap";

	private static KeyBinding swapKey;
	private static KeyBinding toggleKey;

	@Override
	public void onInitializeClient() {
		ElytraSwapConfig.load();

		KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

		// Default: R to swap. Change it any time in Options > Controls > Key Binds > Elytra Swap.
		swapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.elytraswap.swap", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, category));

		// Unbound by default. Set your own key if you want a quick on/off hotkey.
		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.elytraswap.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, category));

		ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
	}

	private void onTick(MinecraftClient client) {
		if (client.player == null) return;

		while (toggleKey.wasPressed()) {
			ElytraSwapConfig.enabled = !ElytraSwapConfig.enabled;
			ElytraSwapConfig.save();
			client.player.sendMessage(
					Text.literal("Elytra Swap: ")
							.append(Text.literal(ElytraSwapConfig.enabled ? "ON" : "OFF")
									.formatted(ElytraSwapConfig.enabled ? Formatting.GREEN : Formatting.RED)),
					true);
		}

		while (swapKey.wasPressed()) {
			if (ElytraSwapConfig.enabled && client.currentScreen == null) {
				ElytraSwapper.swap(client);
			}
		}
	}
}
