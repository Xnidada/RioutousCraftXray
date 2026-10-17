package com.xray.injector;

import com.xray.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Locale;

/** Shared server-side validation for the injector's ORE slot. */
public final class OreTargetUtil {
    private OreTargetUtil() {}

    public static boolean isOre(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == Blocks.AIR) return false;
        ResourceLocation id = Item.REGISTRY.getNameForObject(stack.getItem());
        if (id == null) return false;

        String registryId = id.toString().toLowerCase(Locale.ROOT);
        if (registryId.contains("ore") || hasOreDictionaryEntry(stack)) return true;
        return isConfiguredBlock(registryId, stack.getMetadata());
    }

    private static boolean hasOreDictionaryEntry(ItemStack stack) {
        for (int oreId : OreDictionary.getOreIDs(stack)) {
            String name = OreDictionary.getOreName(oreId);
            if (name != null && name.regionMatches(true, 0, "ore", 0, 3)) return true;
        }
        return false;
    }

    private static boolean isConfiguredBlock(String registryId, int metadata) {
        for (String entry : Configuration.additionalOreBlocks) {
            if (entry == null) continue;
            String value = entry.trim().toLowerCase(Locale.ROOT);
            if (value.isEmpty()) continue;
            int separator = value.lastIndexOf(':');
            String configuredId = value;
            String configuredMetadata = "*";
            if (separator > value.indexOf(':')) {
                configuredId = value.substring(0, separator);
                configuredMetadata = value.substring(separator + 1);
            }
            if (!registryId.equals(configuredId)) continue;
            if ("*".equals(configuredMetadata)) return true;
            try {
                if (Integer.parseInt(configuredMetadata) == metadata) return true;
            } catch (NumberFormatException ignored) {
                // Ignore malformed entries instead of rejecting valid ore items.
            }
        }
        return false;
    }

    public static IBlockState getState(ItemStack stack) {
        if (!isOre(stack)) return null;
        Block block = Block.getBlockFromItem(stack.getItem());
        return block.getStateFromMeta(stack.getMetadata());
    }
}
