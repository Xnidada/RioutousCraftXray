package com.xray.item;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import net.minecraftforge.registries.IForgeRegistryEntry;

/** Unordered glasses + ore recipe. The ore's block state is written to the new stack. */
public class XrayGlassesRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private final ItemXrayGlasses glasses;
    public XrayGlassesRecipe(ItemXrayGlasses glasses) { this.glasses = glasses; setRegistryName(new ResourceLocation("rioutouscraftxray", "glasses_l" + glasses.tier + "_ore")); }
    private boolean isOre(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() == glasses) return false;
        ResourceLocation id = Item.REGISTRY.getNameForObject(stack.getItem());
        return id != null && id.toString().toLowerCase().contains("ore") && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR;
    }
    private ItemStack findOre(InventoryCrafting inv) {
        ItemStack found = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == glasses) { if (!found.isEmpty()) return ItemStack.EMPTY; }
            else if (isOre(stack) && found.isEmpty()) found = stack;
            else return ItemStack.EMPTY;
        }
        return found;
    }
    @Override public boolean matches(InventoryCrafting inv, World world) { return !findOre(inv).isEmpty() && findGlasses(inv); }
    private boolean findGlasses(InventoryCrafting inv) { int count = 0; for (int i=0;i<inv.getSizeInventory();i++) if (inv.getStackInSlot(i).getItem()==glasses) count++; return count==1; }
    @Override public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack ore = findOre(inv); ItemStack out = new ItemStack(glasses);
        for (int i = 0; i < inv.getSizeInventory(); i++) if (inv.getStackInSlot(i).getItem() == glasses) { out = inv.getStackInSlot(i).copy(); out.setCount(1); break; }
        if (!ore.isEmpty()) {
            Block block = Block.getBlockFromItem(ore.getItem());
            if (block != null) {
                // Crafting configures the existing glasses and must preserve their exact EU charge,
                // including zero or a partially charged stack.
                glasses.addTarget(out, block.getStateFromMeta(ore.getMetadata()).toString());
            }
        }
        return out;
    }
    @Override public boolean canFit(int width, int height) { return width * height >= 2; }
    @Override public ItemStack getRecipeOutput() { return new ItemStack(glasses); }
    @Override public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) { return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY); }
    @Override public boolean isDynamic() { return true; }
}
