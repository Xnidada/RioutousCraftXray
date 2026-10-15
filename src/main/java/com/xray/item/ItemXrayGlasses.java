package com.xray.item;

import com.xray.reference.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import java.util.List;

/** Six server-authoritative tiers of powered X-Ray glasses. Energy is stored in EU. */
public class ItemXrayGlasses extends ItemArmor {
    public final int tier, capacity, radius, slots;
    private static final String ENERGY = "Energy";
    private static final String TARGET = "TargetState";

    public ItemXrayGlasses(int tier, int capacity, int radius, int slots) {
        super(ArmorMaterial.IRON, 0, EntityEquipmentSlot.HEAD);
        this.tier = tier; this.capacity = capacity; this.radius = radius; this.slots = slots;
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.COMBAT);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, "glasses_l" + tier));
        setUnlocalizedName("rioutouscraftxray.glasses_l" + tier);
    }

    public int getEnergy(ItemStack stack) { return stack.hasTagCompound() ? stack.getTagCompound().getInteger(ENERGY) : 0; }
    public void setEnergy(ItemStack stack, int value) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(ENERGY, Math.max(0, Math.min(capacity, value)));
    }
    public String getTarget(ItemStack stack) { return stack.hasTagCompound() ? stack.getTagCompound().getString(TARGET) : ""; }
    public void setTarget(ItemStack stack, String state) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(TARGET, state);
    }
    public boolean hasTarget(ItemStack stack, String state) {
        if (!stack.hasTagCompound()) return false;
        NBTTagList list = stack.getTagCompound().getTagList("Targets", Constants.NBT.TAG_STRING);
        for (int i = 0; i < Math.min(slots, list.tagCount()); i++) if (state.equals(list.getStringTagAt(i))) return true;
        return list.tagCount() == 0 && state.equals(getTarget(stack));
    }
    public boolean hasConfiguredTargets(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getTagList("Targets", Constants.NBT.TAG_STRING).tagCount() > 0;
    }
    public void addTarget(ItemStack stack, String state) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagList list = stack.getTagCompound().getTagList("Targets", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) if (state.equals(list.getStringTagAt(i))) return;
        if (list.tagCount() >= slots) list.removeTag(0);
        list.appendTag(new net.minecraft.nbt.NBTTagString(state));
        stack.getTagCompound().setTag("Targets", list);
        stack.getTagCompound().setString(TARGET, state);
    }
    public boolean consume(ItemStack stack, int eu) { int e = getEnergy(stack); if (e < eu) return false; setEnergy(stack, e - eu); return true; }
    public boolean isUsable(ItemStack stack) { return getEnergy(stack) >= 100 && hasConfiguredTargets(stack); }

    @Override public boolean showDurabilityBar(ItemStack stack) { return true; }
    @Override public double getDurabilityForDisplay(ItemStack stack) { return 1.0D - (double) getEnergy(stack) / capacity; }
    @Override public int getRGBDurabilityForDisplay(ItemStack stack) { return 0x20D8FF; }
    @Override public void addInformation(ItemStack stack, net.minecraft.world.World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.AQUA + "EU: " + getEnergy(stack) + " / " + capacity);
        tooltip.add(TextFormatting.GRAY + "L" + tier + "  " + radius + "x" + radius + "  " + slots + " targets");
    }

    @Override public void onArmorTick(net.minecraft.world.World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && player.ticksExisted % 20 == 0) {
            if (getEnergy(stack) < capacity) setEnergy(stack, getEnergy(stack) + IC2EnergyBridge.pullFromInventory(player, Math.min(10000, capacity - getEnergy(stack))));
            if (isUsable(stack)) consume(stack, 100);
        }
    }
}
