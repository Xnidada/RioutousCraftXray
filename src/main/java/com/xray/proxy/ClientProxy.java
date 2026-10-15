package com.xray.proxy;

import com.xray.client.ClientState;
import com.xray.xray.Controller;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientState.initialize(event);
    }
    @Override public void init(FMLInitializationEvent event) {}
    @Override public void postInit(FMLPostInitializationEvent event) {}
    @Override public void onServerStopping(FMLServerStoppingEvent event) { Controller.shutdownExecutor(); }
}
