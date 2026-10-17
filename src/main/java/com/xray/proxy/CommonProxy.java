package com.xray.proxy;

import com.xray.item.ModItems;
import com.xray.injector.InjectorGuiHandler;
import com.xray.injector.ModBlocks;
import com.xray.injector.TileXrayInjector;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // RegistryEvent is the normal path; this idempotent call also covers
        // launchers that fail to inject static registry subscribers.
        ModBlocks.register(ForgeRegistries.BLOCKS);
        ModItems.register(ForgeRegistries.ITEMS);
        GameRegistry.registerTileEntity(TileXrayInjector.class, com.xray.reference.Reference.MOD_ID + ":xray_injector");
    }
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(com.xray.XRay.instance, new InjectorGuiHandler());
    }
    public void onServerStopping(FMLServerStoppingEvent event) {}
    public Object getClientGuiElement(int id, net.minecraft.entity.player.EntityPlayer player,
                                      net.minecraft.world.World world, int x, int y, int z) { return null; }
}
