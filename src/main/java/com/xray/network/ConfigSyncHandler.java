package com.xray.network;

import com.xray.Configuration;
import com.xray.item.ModItems;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/** Applies the server snapshot on either side without accepting client values. */
public class ConfigSyncHandler implements IMessageHandler<ConfigSyncMessage, IMessage> {
    @Override public IMessage onMessage(ConfigSyncMessage message, MessageContext context) {
        if (context.side == Side.CLIENT) {
            Configuration.applySynced(message.injectorMaxEnergy, message.injectorActionCost,
                    message.radius, message.energyPerSecond, message.capacity, message.targetSlots);
            ModItems.applyConfig();
        }
        return null;
    }
}
