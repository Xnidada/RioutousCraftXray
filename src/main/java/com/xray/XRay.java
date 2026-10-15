package com.xray;

import com.xray.item.ModItems;
import com.xray.item.ModRecipes;
import com.xray.proxy.CommonProxy;
import com.xray.reference.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.MOD_VERSION,
        updateJSON = Reference.UPDATE_JSON,
        dependencies = "required-after:ic2@[2.8.222,)"
)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class XRay {
    @SidedProxy(clientSide = "com.xray.proxy.ClientProxy", serverSide = "com.xray.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(Reference.MOD_ID)
    public static XRay instance;

    public XRay() {
        // Forge 1.12.2 posts registry events on MinecraftForge.EVENT_BUS.
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        MinecraftForge.EVENT_BUS.register(ModRecipes.class);
    }

    @EventHandler public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }
    @EventHandler public void init(FMLInitializationEvent event) { proxy.init(event); }
    @EventHandler public void postInit(FMLPostInitializationEvent event) { proxy.postInit(event); }
    @EventHandler public void onServerStopping(FMLServerStoppingEvent event) { proxy.onServerStopping(event); }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Reference.MOD_ID.equals(event.getModID())) ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
    }
}
