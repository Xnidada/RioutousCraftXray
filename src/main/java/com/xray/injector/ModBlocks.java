package com.xray.injector;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModBlocks {
    public static final BlockXrayInjector XRAY_INJECTOR = new BlockXrayInjector();

    @SubscribeEvent public static void register(RegistryEvent.Register<Block> event) { register(event.getRegistry()); }
    public static void register(IForgeRegistry<Block> registry) {
        if (!registry.containsKey(XRAY_INJECTOR.getRegistryName())) registry.register(XRAY_INJECTOR);
    }

    private ModBlocks() {}
}
