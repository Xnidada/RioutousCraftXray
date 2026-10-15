package com.xray.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Pulls EU from IC2 electric items in the player's inventory. */
final class IC2EnergyBridge {
    private IC2EnergyBridge() {}
    static int pullFromInventory(EntityPlayer player, int amount) {
        if (amount <= 0 || ElectricItem.manager == null) return 0;
        try {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack source = player.inventory.getStackInSlot(i);
                if (source.isEmpty() || isGlasses(source) || !(source.getItem() instanceof IElectricItem)) continue;
                double moved = ElectricItem.manager.discharge(source, amount, 4, true, false, false);
                if (moved > 0) return (int) Math.min(Integer.MAX_VALUE, moved);
            }
        } catch (RuntimeException ignored) { }
        return 0;
    }

    private static boolean isGlasses(ItemStack stack) {
        for (ItemXrayGlasses glasses : ModItems.GLASSES) if (stack.getItem() == glasses) return true;
        return false;
    }
}
