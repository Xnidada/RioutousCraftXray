package com.xray.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Optional IC2 integration kept reflective so the source builds without shipping IC2. */
final class IC2EnergyBridge {
    private IC2EnergyBridge() {}
    static int pullFromInventory(EntityPlayer player, int amount) {
        try {
            Class<?> electric = Class.forName("ic2.api.item.ElectricItem");
            Object manager = electric.getField("manager").get(null);
            java.lang.reflect.Method discharge = manager.getClass().getMethod("discharge", ItemStack.class, double.class, int.class, boolean.class, boolean.class, boolean.class);
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack source = player.inventory.getStackInSlot(i);
                if (source.isEmpty() || source.getItem().getRegistryName() == null) continue;
                String id = source.getItem().getRegistryName().getResourcePath().toLowerCase();
                if (!(id.contains("battery") || id.contains("crystal") || id.contains("lapotron"))) continue;
                double moved = ((Number) discharge.invoke(manager, source, (double) amount, 4, true, false, false)).doubleValue();
                if (moved > 0) return (int) Math.min(Integer.MAX_VALUE, moved);
            }
        } catch (Throwable ignored) { }
        return 0;
    }
}
