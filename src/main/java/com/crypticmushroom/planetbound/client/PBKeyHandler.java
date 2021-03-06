package com.crypticmushroom.planetbound.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class PBKeyHandler {
    private static KeyBinding inventory;

    public static void registerKeyBindings() {
        inventory = new KeyBinding("key.inventory", Keyboard.KEY_E, "key.categories.planetbound");

        ClientRegistry.registerKeyBinding(inventory);
    }

    public static boolean isInventoryPressed() {
        return inventory.isPressed();
    }
}