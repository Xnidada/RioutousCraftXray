package com.xray.item;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.xray.reference.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModItems {
    public static final ItemXrayGlasses[] GLASSES = {
        new ItemXrayGlasses(1, 30000, 4, 1), new ItemXrayGlasses(2, 60000, 6, 2),
        new ItemXrayGlasses(3, 100000, 8, 3), new ItemXrayGlasses(4, 150000, 12, 4),
        new ItemXrayGlasses(5, 200000, 14, 6), new ItemXrayGlasses(6, 300000, 16, 8)
    };
    @SubscribeEvent public static void register(RegistryEvent.Register<Item> e) { for (Item item : GLASSES) e.getRegistry().register(item); }
    private ModItems() {}
}
