package com.xray.client;

import com.xray.xray.Events;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientState {
    public static Minecraft mc;

    private ClientState() {}

    public static void initialize() {
        mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(new Events());
    }
}
