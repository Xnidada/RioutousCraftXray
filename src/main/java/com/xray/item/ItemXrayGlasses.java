package com.xray.item;

import com.xray.reference.Reference;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;
import java.util.ArrayList;

/** Six server-authoritative tiers of powered X-Ray glasses. Energy is stored in EU. */
public class ItemXrayGlasses extends ItemArmor implements IElectricItem {
    public final int tier, capacity, radius, slots;
    /** IC2's ElectricItemManager uses this exact NBT key. */
    private static final String ENERGY = "charge";
    private static final String LEGACY_ENERGY = "Energy";
    private static final String TARGET = "TargetState";

    public ItemXrayGlasses(int tier, int capacity, int radius, int slots) {
        super(ArmorMaterial.IRON, 0, EntityEquipmentSlot.HEAD);
        this.tier = tier; this.capacity = capacity; this.radius = radius; this.slots = slots;
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.COMBAT);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, "glasses_l" + tier));
        setUnlocalizedName("rioutouscraftxray.glasses_l" + tier);
    }

    public int getEnergy(ItemStack stack) {
        if (!stack.hasTagCompound()) return 0;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey(ENERGY)) return (int) Math.max(0, Math.min(capacity, Math.round(tag.getDouble(ENERGY))));
        // Migrate stacks made by the old implementation once they are read.
        return (int) Math.max(0, Math.min(capacity, tag.getInteger(LEGACY_ENERGY)));
    }
    public void setEnergy(ItemStack stack, int value) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound tag = stack.getTagCompound();
        tag.setDouble(ENERGY, Math.max(0, Math.min(capacity, value)));
        tag.removeTag(LEGACY_ENERGY);
    }
    public String getTarget(ItemStack stack) { return stack.hasTagCompound() ? stack.getTagCompound().getString(TARGET) : ""; }
    public boolean hasTarget(ItemStack stack, String state) {
        for (String target : getTargets(stack)) if (state.equals(target)) return true;
        return false;
    }
    public boolean hasConfiguredTargets(ItemStack stack) {
        return !getTargets(stack).isEmpty();
    }
    public List<String> getTargets(ItemStack stack) {
        List<String> targets = new ArrayList<>();
        if (!stack.hasTagCompound()) return targets;
        NBTTagList list = stack.getTagCompound().getTagList("Targets", Constants.NBT.TAG_STRING);
        for (int i = 0; i < Math.min(slots, list.tagCount()); i++) targets.add(list.getStringTagAt(i));
        if (targets.isEmpty() && !getTarget(stack).isEmpty()) targets.add(getTarget(stack));
        return targets;
    }
    public int getTargetCount(ItemStack stack) { return getTargets(stack).size(); }
    public boolean removeTarget(ItemStack stack, int index) {
        List<String> targets = getTargets(stack);
        if (index < 0 || index >= targets.size()) return false;
        targets.remove(index);
        if (!stack.hasTagCompound()) return true;
        NBTTagCompound tag = stack.getTagCompound();
        if (targets.isEmpty()) {
            tag.removeTag("Targets");
            tag.removeTag(TARGET);
        } else {
            NBTTagList list = new NBTTagList();
            for (String target : targets) list.appendTag(new net.minecraft.nbt.NBTTagString(target));
            tag.setTag("Targets", list);
            tag.setString(TARGET, targets.get(targets.size() - 1));
        }
        return true;
    }
    public void addTarget(ItemStack stack, String state) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagList list = stack.getTagCompound().getTagList("Targets", Constants.NBT.TAG_STRING);
        if (list.tagCount() == 0 && !getTarget(stack).isEmpty()) {
            list.appendTag(new net.minecraft.nbt.NBTTagString(getTarget(stack)));
        }
        for (int i = 0; i < list.tagCount(); i++) if (state.equals(list.getStringTagAt(i))) return;
        if (list.tagCount() >= slots) list.removeTag(0);
        list.appendTag(new net.minecraft.nbt.NBTTagString(state));
        stack.getTagCompound().setTag("Targets", list);
        stack.getTagCompound().setString(TARGET, state);
    }
    public boolean consume(ItemStack stack, int eu) { int e = getEnergy(stack); if (e < eu) return false; setEnergy(stack, e - eu); return true; }
    public boolean isUsable(ItemStack stack) { return getEnergy(stack) >= 100 && hasConfiguredTargets(stack); }

    @Override public boolean canProvideEnergy(ItemStack stack) { return false; }
    @Override public double getMaxCharge(ItemStack stack) { return capacity; }
    @Override public int getTier(ItemStack stack) { return tier <= 3 ? 3 : 4; }
    @Override public double getTransferLimit(ItemStack stack) {
        switch (tier) {
            case 1: return 512;
            case 2: return 1024;
            case 3: return 2048;
            case 4: return 4096;
            case 5: return 8192;
            default: return 16384;
        }
    }

    @Override public boolean showDurabilityBar(ItemStack stack) { return true; }
    @Override public double getDurabilityForDisplay(ItemStack stack) { return 1.0D - (double) getEnergy(stack) / capacity; }
    @Override public int getRGBDurabilityForDisplay(ItemStack stack) { return 0x20D8FF; }
    @SideOnly(Side.CLIENT)
    @Override public void addInformation(ItemStack stack, net.minecraft.world.World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.AQUA + I18n.format("item.rioutouscraftxray.energy", getEnergy(stack), capacity));
        tooltip.add(TextFormatting.GRAY + I18n.format("item.rioutouscraftxray.stats", tier, radius, radius, slots));
    }

    @Override public void onArmorTick(net.minecraft.world.World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && player.ticksExisted % 20 == 0) {
            int energy = getEnergy(stack);
            if (energy < capacity) {
                setEnergy(stack, energy + IC2EnergyBridge.pullFromInventory(player, Math.min(10000, capacity - energy)));
            }
            if (isUsable(stack)) consume(stack, 100);
        }
    }
}
