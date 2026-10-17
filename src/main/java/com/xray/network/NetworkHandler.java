package com.xray.network;

import com.xray.reference.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/** Server-to-client configuration synchronization. */
public final class NetworkHandler {
    private static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
    private static boolean initialized;

    private NetworkHandler() {}

    public static void init() {
        if (initialized) return;
        CHANNEL.registerMessage(ConfigSyncHandler.class, ConfigSyncMessage.class, 0, Side.CLIENT);
        MinecraftForge.EVENT_BUS.register(NetworkHandler.class);
        initialized = true;
    }

    public static void sendTo(EntityPlayerMP player) {
        CHANNEL.sendTo(ConfigSyncMessage.fromServer(), player);
    }

    public static void broadcast() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) sendTo(player);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) sendTo((EntityPlayerMP) event.player);
    }
}
