package com.xray.injector;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** Shared server-side validation for the injector's ORE slot. */
public final class OreTargetUtil {
    private OreTargetUtil() {}

    public static boolean isOre(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        ResourceLocation id = Item.REGISTRY.getNameForObject(stack.getItem());
        return id != null && id.toString().toLowerCase().contains("ore")
                && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR;
    }

    public static IBlockState getState(ItemStack stack) {
        if (!isOre(stack)) return null;
        Block block = Block.getBlockFromItem(stack.getItem());
        return block.getStateFromMeta(stack.getMetadata());
    }
}
