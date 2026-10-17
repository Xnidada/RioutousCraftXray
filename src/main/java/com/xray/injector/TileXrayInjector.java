package com.xray.injector;

import com.xray.item.ItemXrayGlasses;
import com.xray.Configuration;
import ic2.api.energy.EnergyNet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IEnergySink;

import java.util.List;

/** Server-authoritative ORE injector and IC2 EU sink. */
public class TileXrayInjector extends TileEntity implements IInventory, IEnergySink {
    public static int getActionCost() { Configuration.sanitize(); return Configuration.injectorActionCost; }
    public static int getMaxEnergy() { Configuration.sanitize(); return Configuration.injectorMaxEnergy; }

    private final InventoryBasic inventory = new InventoryBasic("container.rioutouscraftxray.injector", false, 2);
    private double energy;
    private int selectedTarget;
    private boolean addedToEnergyNet;

    public TileXrayInjector() {}

    @Override public void onLoad() {
        super.onLoad();
        if (!world.isRemote) registerEnergyNet();
    }

    @Override public void onChunkUnload() {
        unloadFromEnergyNet();
        super.onChunkUnload();
    }

    @Override public void invalidate() {
        unloadFromEnergyNet();
        super.invalidate();
    }

    private void unloadFromEnergyNet() {
        if (addedToEnergyNet) {
            boolean cancelled = MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) this));
            if (cancelled && EnergyNet.instance != null) {
                try {
                    EnergyNet.instance.removeTile(this);
                } catch (RuntimeException ignored) { }
            }
            addedToEnergyNet = false;
        }
    }

    private void registerEnergyNet() {
        if (addedToEnergyNet) return;
        boolean cancelled = MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) this));
        addedToEnergyNet = !cancelled;
        if (!addedToEnergyNet && EnergyNet.instance != null) {
            try {
                EnergyNet.instance.addTile(this);
                addedToEnergyNet = true;
            } catch (RuntimeException ignored) { }
        }
    }

    public double getEnergy() {
        clampEnergy();
        return energy;
    }
    public int getSelectedTarget() { return selectedTarget; }
    public void setSelectedTarget(int value) {
        selectedTarget = Math.max(0, value);
        markDirty();
    }

    public List<String> getTargets() {
        ItemStack stack = inventory.getStackInSlot(0);
        return stack.getItem() instanceof ItemXrayGlasses
                ? ((ItemXrayGlasses) stack.getItem()).getTargets(stack)
                : java.util.Collections.<String>emptyList();
    }

    public int getTargetLimit() {
        ItemStack stack = inventory.getStackInSlot(0);
        return stack.getItem() instanceof ItemXrayGlasses ? ((ItemXrayGlasses) stack.getItem()).slots : 0;
    }

    public boolean injectOre(EntityPlayer player) {
        if (world == null || world.isRemote) return false;
        ItemStack glassesStack = inventory.getStackInSlot(0);
        ItemStack oreStack = inventory.getStackInSlot(1);
        if (!(glassesStack.getItem() instanceof ItemXrayGlasses) || !OreTargetUtil.isOre(oreStack)) return false;
        ItemXrayGlasses glasses = (ItemXrayGlasses) glassesStack.getItem();
        if (glasses.getTargetCount(glassesStack) >= glasses.slots || OreTargetUtil.getState(oreStack) == null
                || glasses.hasTarget(glassesStack, OreTargetUtil.getState(oreStack).toString())) return false;
        if (!consumeEnergy()) return false;
        glasses.addTarget(glassesStack, OreTargetUtil.getState(oreStack).toString());
        oreStack.shrink(1);
        if (oreStack.isEmpty()) inventory.setInventorySlotContents(1, ItemStack.EMPTY);
        selectedTarget = Math.max(0, glasses.getTargetCount(glassesStack) - 1);
        markDirty();
        return true;
    }

    public boolean clearSelectedOre(EntityPlayer player) {
        if (world == null || world.isRemote) return false;
        ItemStack glassesStack = inventory.getStackInSlot(0);
        if (!(glassesStack.getItem() instanceof ItemXrayGlasses)) return false;
        ItemXrayGlasses glasses = (ItemXrayGlasses) glassesStack.getItem();
        if (energy < getActionCost() || !glasses.removeTarget(glassesStack, selectedTarget)) return false;
        energy -= getActionCost();
        selectedTarget = Math.min(selectedTarget, Math.max(0, glasses.getTargetCount(glassesStack) - 1));
        markDirty();
        return true;
    }

    private boolean consumeEnergy() {
        if (energy < getActionCost()) return false;
        energy -= getActionCost();
        markDirty();
        return true;
    }

    @Override public double getDemandedEnergy() {
        clampEnergy();
        return Math.max(0, getMaxEnergy() - energy);
    }

    /**
     * IC2's normal tiers stop at 14, but addon generators can advertise higher
     * tiers. Tier 30 is IC2's highest finite power tier and prevents those
     * packets from being treated as an overload; storage is still capped by the
     * server-configured injector capacity.
     */
    @Override public int getSinkTier() { return 30; }

    @Override public double injectEnergy(EnumFacing direction, double amount, double voltage) {
        clampEnergy();
        if (!(amount > 0.0) || Double.isNaN(amount)) return 0.0;
        double accepted = Math.min(getDemandedEnergy(), Math.max(0, amount));
        energy += accepted;
        clampEnergy();
        markDirty();
        return amount - accepted;
    }
    @Override public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing direction) { return true; }

    private void clampEnergy() {
        if (Double.isNaN(energy) || energy < 0.0) energy = 0.0;
        double capacity = getMaxEnergy();
        if (Double.isInfinite(energy) || energy > capacity) energy = capacity;
    }

    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy = tag.getDouble("Energy");
        clampEnergy();
        selectedTarget = Math.max(0, tag.getInteger("SelectedTarget"));
        for (int i = 0; i < 2; i++) {
            String key = "Slot" + i;
            inventory.setInventorySlotContents(i, tag.hasKey(key, Constants.NBT.TAG_COMPOUND)
                    ? new ItemStack(tag.getCompoundTag(key)) : ItemStack.EMPTY);
        }
    }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setDouble("Energy", energy);
        tag.setInteger("SelectedTarget", selectedTarget);
        for (int i = 0; i < 2; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) tag.setTag("Slot" + i, stack.writeToNBT(new NBTTagCompound()));
        }
        return tag;
    }

    @Override public int getSizeInventory() { return inventory.getSizeInventory(); }
    @Override public boolean isEmpty() { return inventory.isEmpty(); }
    @Override public ItemStack getStackInSlot(int index) { return inventory.getStackInSlot(index); }
    @Override public ItemStack decrStackSize(int index, int count) { return inventory.decrStackSize(index, count); }
    @Override public ItemStack removeStackFromSlot(int index) { return inventory.removeStackFromSlot(index); }
    @Override public void setInventorySlotContents(int index, ItemStack stack) { inventory.setInventorySlotContents(index, stack); }
    @Override public int getInventoryStackLimit() { return 1; }
    @Override public void markDirty() { super.markDirty(); }
    @Override public boolean isUsableByPlayer(EntityPlayer player) {
        return world != null && world.getTileEntity(pos) == this
                && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
    @Override public void openInventory(EntityPlayer player) { inventory.openInventory(player); }
    @Override public void closeInventory(EntityPlayer player) { inventory.closeInventory(player); }
    @Override public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 0) return stack.getItem() instanceof ItemXrayGlasses;
        return index == 1 && OreTargetUtil.isOre(stack);
    }
    @Override public int getField(int id) { return id == 0 ? (int) getEnergy() : selectedTarget; }
    @Override public void setField(int id, int value) { if (id == 0) { energy = value; clampEnergy(); } else selectedTarget = Math.max(0, value); }
    @Override public int getFieldCount() { return 2; }
    @Override public void clear() { inventory.clear(); }
    @Override public String getName() { return inventory.getName(); }
    @Override public boolean hasCustomName() { return inventory.hasCustomName(); }
    @Override public ITextComponent getDisplayName() { return inventory.getDisplayName(); }
}
