package com.xray.proxy;

import com.xray.item.ModItems;
import com.xray.item.ModRecipes;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // RegistryEvent is the normal path; this idempotent call also covers
        // launchers that fail to inject static registry subscribers.
        ModItems.register(ForgeRegistries.ITEMS);
        ModRecipes.register(ForgeRegistries.RECIPES);
    }
    public void init(FMLInitializationEvent event) {}
    public void postInit(FMLPostInitializationEvent event) {}
    public void onServerStopping(FMLServerStoppingEvent event) {}
}
