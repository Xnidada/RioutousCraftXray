package com.xray.injector;

import com.xray.item.ItemXrayGlasses;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerXrayInjector extends Container {
    public final TileXrayInjector tile;
    private int lastEnergy;
    private int lastSelected;

    public ContainerXrayInjector(InventoryPlayer playerInventory, TileXrayInjector tile) {
        this.tile = tile;
        addSlotToContainer(new Slot(tile, 0, 14, 43) {
            @Override public boolean isItemValid(ItemStack stack) { return stack.getItem() instanceof ItemXrayGlasses; }
            @Override public int getSlotStackLimit() { return 1; }
        });
        addSlotToContainer(new Slot(tile, 1, 57, 43) {
            @Override public boolean isItemValid(ItemStack stack) { return OreTargetUtil.isOre(stack); }
            @Override public int getSlotStackLimit() { return 1; }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        lastEnergy = tile.getField(0);
        lastSelected = tile.getField(1);
    }

    @Override public boolean canInteractWith(EntityPlayer player) { return tile.isUsableByPlayer(player); }

    @Override public boolean enchantItem(EntityPlayer player, int id) {
        if (player == null || !canInteractWith(player)) return false;
        if (id == 0) return tile.injectOre(player);
        if (id == 1) return tile.clearSelectedOre(player);
        if (id >= 100) {
            int target = id - 100;
            if (target < tile.getTargets().size()) {
                tile.setSelectedTarget(target);
                return true;
            }
        }
        return false;
    }

    @Override public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int energy = tile.getField(0);
        int selected = tile.getField(1);
        if (energy != lastEnergy || selected != lastSelected) {
            for (IContainerListener listener : listeners) {
                listener.sendWindowProperty(this, 0, energy);
                listener.sendWindowProperty(this, 1, selected);
            }
            lastEnergy = energy;
            lastSelected = selected;
        }
    }

    @Override public void updateProgressBar(int id, int value) { tile.setField(id, value); }

    @Override public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return result;
        ItemStack stack = slot.getStack();
        result = stack.copy();
        if (index < 2) {
            if (!mergeItemStack(stack, 2, inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof ItemXrayGlasses) {
            if (!mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (OreTargetUtil.isOre(stack)) {
            if (!mergeItemStack(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return result;
    }

    @Override public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        tile.closeInventory(player);
    }
}
