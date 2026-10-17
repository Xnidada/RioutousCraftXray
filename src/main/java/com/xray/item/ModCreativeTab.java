package com.xray.item;

import com.xray.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/** Shared creative tab for every RioutousCraftXray item and block item. */
public final class ModCreativeTab {
    public static final CreativeTabs TAB = new CreativeTabs(Reference.MOD_ID) {
        @Override public ItemStack getTabIconItem() {
            return new ItemStack(ModItems.GLASSES[0]);
        }
    };

    private ModCreativeTab() {}
}
