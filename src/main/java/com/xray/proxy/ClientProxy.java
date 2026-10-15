package com.xray.proxy;

import com.xray.client.ClientState;
import com.xray.client.gui.GuiXrayInjector;
import com.xray.injector.TileXrayInjector;
import com.xray.reference.Reference;
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
    @Override public void init(FMLInitializationEvent event) { super.init(event); }
    @Override public void postInit(FMLPostInitializationEvent event) {}
    @Override public void onServerStopping(FMLServerStoppingEvent event) { Controller.shutdownExecutor(); }
    @Override public Object getClientGuiElement(int id, net.minecraft.entity.player.EntityPlayer player,
                                                net.minecraft.world.World world, int x, int y, int z) {
        if (id != Reference.GUI_INJECTOR) return null;
        net.minecraft.tileentity.TileEntity tile = world.getTileEntity(new net.minecraft.util.math.BlockPos(x, y, z));
        return tile instanceof TileXrayInjector ? new GuiXrayInjector(player.inventory, (TileXrayInjector) tile) : null;
    }
}
