package com.xray.client;

import com.xray.xray.Events;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public final class ClientState {
    public static Minecraft mc;
    public static Logger logger;

    private ClientState() {}

    public static void initialize(FMLPreInitializationEvent event) {
        mc = Minecraft.getMinecraft();
        logger = event.getModLog();
        logger.debug(I18n.format("xray.debug.init"));
        MinecraftForge.EVENT_BUS.register(new Events());
    }
}
