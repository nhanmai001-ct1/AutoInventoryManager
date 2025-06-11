package com.example.autoinventory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class AutoInventoryMod implements ClientModInitializer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static boolean enabled = false;
    private static final Set<Integer> fullPVs = new HashSet<>();
    private static ItemStack itemTemplate = ItemStack.EMPTY;

    private static KeyBinding toggleKey;
    private static KeyBinding selectItemKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoinventory.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "category.autoinventory"
        ));

        selectItemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoinventory.select_item",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "category.autoinventory"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                sendMessage("AutoInventoryManager " + (enabled ? "BẬT" : "TẮT"));
            }

            while (selectItemKey.wasPressed()) {
                if (client.player != null) {
                    itemTemplate = client.player.getMainHandStack().copy();
                    sendMessage("Đã chọn item mẫu: " + itemTemplate.getName().getString());
                }
            }

            if (enabled && client.player != null && client.world != null) {
                if (isInventoryFull(client.player)) {
                    processStorage(client.player);
                }
            }
        });
    }

    private boolean isInventoryFull(PlayerEntity player) {
        for (int i = 0; i < 36; i++) {
            if (i == 27) continue;
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxCount()) {
                return false;
            }
        }
        return true;
    }

    private void processStorage(PlayerEntity player) {
        for (int key = GLFW.GLFW_KEY_2; key <= GLFW.GLFW_KEY_9; key++) {
            int pvIndex = key - GLFW.GLFW_KEY_2;
            if (fullPVs.contains(pvIndex)) continue;

            InputUtil.simulateKeyPress(client.getWindow().getHandle(), key);
            sleep(300);

            boolean pvHasSpace = true; // Giả lập: luôn còn chỗ
            if (pvHasSpace) {
                for (int i = 0; i < 36; i++) {
                    if (i == 27) continue;
                    ItemStack stack = player.getInventory().getStack(i);
                    if (!stack.isEmpty()) {
                        player.getInventory().removeStack(i);
                    }
                }
            } else {
                InputUtil.simulateKeyPress(client.getWindow().getHandle(), GLFW.GLFW_KEY_E);
                fullPVs.add(pvIndex);
            }

            sleep(300);
            if (!isInventoryFull(player)) break;
        }

        if (isInventoryFull(player)) {
            sendMessage("Không còn PV nào trống!");
        } else {
            fillWithTemplate(player);
        }
    }

    private void fillWithTemplate(PlayerEntity player) {
        if (itemTemplate.isEmpty()) {
            sendMessage("Chưa chọn item mẫu.");
            return;
        }

        for (int i = 0; i < 36; i++) {
            if (i == 27) continue;
            player.getInventory().setStack(i, itemTemplate.copyWithCount(1));
        }

        sendMessage("Đã lấp đầy bằng item mẫu.");
    }

    private void sendMessage(String msg) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal("[AutoInventoryManager] " + msg), true);
        }
    }

    private void sleep(int millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
    }
}