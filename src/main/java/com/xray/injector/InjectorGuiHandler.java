package com.xray.injector;

import com.xray.XRay;
import com.xray.network.NetworkHandler;
import com.xray.reference.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class InjectorGuiHandler implements IGuiHandler {
    @Override public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id != Reference.GUI_INJECTOR) return null;
        if (!(world.getTileEntity(new net.minecraft.util.math.BlockPos(x, y, z)) instanceof TileXrayInjector)) return null;
        TileXrayInjector tile = (TileXrayInjector) world.getTileEntity(new net.minecraft.util.math.BlockPos(x, y, z));
        tile.openInventory(player);
        if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
            NetworkHandler.sendTo((net.minecraft.entity.player.EntityPlayerMP) player);
        }
        return new ContainerXrayInjector(player.inventory, tile);
    }

    @Override public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return XRay.proxy.getClientGuiElement(id, player, world, x, y, z);
    }
}
