package com.xray.client;

import com.xray.gui.GuiOverlay;
import com.xray.keybinding.InputEvent;
import com.xray.keybinding.KeyBindings;
import com.xray.reference.block.BlockData;
import com.xray.reference.block.SimpleBlockData;
import com.xray.store.BlockStore;
import com.xray.store.GameBlockStore;
import com.xray.store.JsonStore;
import com.xray.xray.Controller;
import com.xray.xray.Events;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;

@SideOnly(Side.CLIENT)
public final class ClientState {
    public static Minecraft mc;
    public static JsonStore blockStore;
    public static GameBlockStore gameBlockStore;
    public static Logger logger;

    private ClientState() {}

    public static void initialize(FMLPreInitializationEvent event) {
        mc = Minecraft.getMinecraft();
        logger = event.getModLog();
        gameBlockStore = new GameBlockStore();
        blockStore = new JsonStore();
        logger.debug(I18n.format("xray.debug.init"));
        KeyBindings.setup();
        MinecraftForge.EVENT_BUS.register(new InputEvent());
        MinecraftForge.EVENT_BUS.register(new Events());
        MinecraftForge.EVENT_BUS.register(new GuiOverlay());
        List<SimpleBlockData> data = blockStore.read();
        if (!data.isEmpty()) {
            HashMap<String, BlockData> map = BlockStore.getFromSimpleBlockList(data);
            Controller.getBlockStore().setStore(map);
        }
    }
}
