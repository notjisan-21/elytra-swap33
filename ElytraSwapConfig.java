package com.elytraswap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Tiny config: just remembers whether the mod is switched on or off. */
public final class ElytraSwapConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("elytraswap.json");

	public static boolean enabled = true;

	private ElytraSwapConfig() {}

	public static void load() {
		if (!Files.exists(PATH)) {
			save();
			return;
		}
		try {
			JsonObject obj = GSON.fromJson(Files.readString(PATH), JsonObject.class);
			if (obj != null && obj.has("enabled")) {
				enabled = obj.get("enabled").getAsBoolean();
			}
		} catch (Exception e) {
			System.err.println("[ElytraSwap] Could not read config, using defaults: " + e.getMessage());
		}
	}

	public static void save() {
		JsonObject obj = new JsonObject();
		obj.addProperty("enabled", enabled);
		try {
			Files.writeString(PATH, GSON.toJson(obj));
		} catch (IOException e) {
			System.err.println("[ElytraSwap] Could not save config: " + e.getMessage());
		}
	}
}
