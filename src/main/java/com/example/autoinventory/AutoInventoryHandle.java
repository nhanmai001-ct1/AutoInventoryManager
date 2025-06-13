package com.example.autoinventorymanager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import java.util.HashSet;
import java.util.Set;

public class InventoryHandler {
    private static final Set<Integer> fullPV = new HashSet<>();

    public static void tick(MinecraftClient client) {
        PlayerInventory inv = client.player.getInventory();
        if (isFull(inv)) {
            for (int key = 2; key <= 9; key++) {
                if (fullPV.contains(key)) continue;
                client.options.hotbarKeySlots[key - 1].wasPressed(); // mở PV
                client.interactionManager.clickButton(0, 0, 0); // mô phỏng mở
                // ... logic di chuyển + đóng + fullPV.add nếu PV đầy
                break;
            }
        }
    }

    private static boolean isFull(PlayerInventory inv) {
        int count = 0;
        for (int i = 0; i < inv.size(); i++) {
            if (i == 27) continue;
            ItemStack st = inv.getStack(i);
            if (st.getCount() == st.getMaxCount()) count++;
        }
        return count >= 35;
    }
}
