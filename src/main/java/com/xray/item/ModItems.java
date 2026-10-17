package com.xray.item;

import com.xray.injector.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
public final class ModItems {
    public static final ItemBlock XRAY_INJECTOR = (ItemBlock) new ItemBlock(ModBlocks.XRAY_INJECTOR)
            .setRegistryName(ModBlocks.XRAY_INJECTOR.getRegistryName())
            .setCreativeTab(ModCreativeTab.TAB);
    public static final ItemXrayGlasses[] GLASSES = {
        new ItemXrayGlasses(1, 30000, 4, 1), new ItemXrayGlasses(2, 60000, 6, 2),
        new ItemXrayGlasses(3, 100000, 8, 3), new ItemXrayGlasses(4, 150000, 12, 4),
        new ItemXrayGlasses(5, 200000, 14, 6), new ItemXrayGlasses(6, 300000, 16, 8)
    };
    @SubscribeEvent public static void register(RegistryEvent.Register<Item> e) { register(e.getRegistry()); }
    /** Idempotent fallback for launchers that do not inject registry subscribers correctly. */
    public static void register(IForgeRegistry<Item> registry) {
        if (!registry.containsKey(XRAY_INJECTOR.getRegistryName())) registry.register(XRAY_INJECTOR);
        for (Item item : GLASSES) {
            if (!registry.containsKey(item.getRegistryName())) registry.register(item);
        }
    }
    private ModItems() {}
}
