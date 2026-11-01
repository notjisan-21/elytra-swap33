package com.elytraswap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ElytraSwapper {
	/** Chestplate slot index inside the player's screen handler. */
	private static final int CHEST_SLOT = 6;

	private ElytraSwapper() {}

	public static void swap(MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		ClientPlayerInteractionManager manager = client.interactionManager;
		if (player == null || manager == null || player.isSpectator()) return;

		// Don't mess with things if the mouse cursor is already holding an item.
		if (!player.playerScreenHandler.getCursorStack().isEmpty()) return;

		boolean wearingElytra = player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);

		// Wearing elytra -> look for a chestplate. Otherwise -> look for a usable elytra.
		int inventoryIndex = findInInventory(player, wearingElytra);
		if (inventoryIndex == -1) {
			player.sendMessage(Text.literal(wearingElytra
					? "No chestplate found in your inventory"
					: "No usable elytra found in your inventory").formatted(Formatting.RED), true);
			return;
		}

		// Inventory index (0-8 hotbar, 9-35 main) -> screen handler slot id.
		int screenSlot = inventoryIndex < 9 ? inventoryIndex + 36 : inventoryIndex;

		if (player.isCreative()) {
			ItemStack worn = player.playerScreenHandler.getSlot(CHEST_SLOT).getStack().copy();
			ItemStack incoming = player.getInventory().getStack(inventoryIndex).copy();
			manager.clickCreativeStack(incoming, CHEST_SLOT);
			manager.clickCreativeStack(worn, screenSlot);
		} else {
			int syncId = player.playerScreenHandler.syncId;
			// 1) pick up the item, 2) drop it on the chest slot (picks up what was worn),
			// 3) put the old chest item where the new one came from.
			manager.clickSlot(syncId, screenSlot, 0, SlotActionType.PICKUP, player);
			manager.clickSlot(syncId, CHEST_SLOT, 0, SlotActionType.PICKUP, player);
			if (!player.playerScreenHandler.getCursorStack().isEmpty()) {
				manager.clickSlot(syncId, screenSlot, 0, SlotActionType.PICKUP, player);
			}
		}

		player.sendMessage(Text.literal(wearingElytra ? "Equipped chestplate" : "Equipped elytra")
				.formatted(Formatting.GREEN), true);
	}

	private static int findInInventory(ClientPlayerEntity player, boolean lookingForChestplate) {
		for (int i = 0; i < 36; i++) {
			ItemStack stack = player.getInventory().getStack(i);
			if (stack.isEmpty()) continue;
			if (lookingForChestplate ? isChestplate(stack) : isUsableElytra(stack)) {
				return i;
			}
		}
		return -1;
	}

	private static boolean isChestplate(ItemStack stack) {
		if (stack.isOf(Items.ELYTRA)) return false;
		EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
		return equippable != null && equippable.slot() == EquipmentSlot.CHEST;
	}

	private static boolean isUsableElytra(ItemStack stack) {
		if (!stack.isOf(Items.ELYTRA)) return false;
		// A "broken" elytra (1 durability left) can't be used for flying.
		return !(stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage() - 1);
	}
}
