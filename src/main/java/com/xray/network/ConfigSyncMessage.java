package com.xray.network;

import com.xray.Configuration;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/** Server snapshot used to update client-side display and scan values. */
public class ConfigSyncMessage implements IMessage {
    public int injectorMaxEnergy;
    public int injectorActionCost;
    public int[] radius;
    public int[] energyPerSecond;
    public int[] capacity;
    public int[] targetSlots;

    public ConfigSyncMessage() {}

    public static ConfigSyncMessage fromServer() {
        Configuration.sanitize();
        ConfigSyncMessage message = new ConfigSyncMessage();
        message.injectorMaxEnergy = Configuration.injectorMaxEnergy;
        message.injectorActionCost = Configuration.injectorActionCost;
        message.radius = Configuration.glassesRadius.clone();
        message.energyPerSecond = Configuration.glassesEnergyPerSecond.clone();
        message.capacity = Configuration.glassesCapacity.clone();
        message.targetSlots = Configuration.glassesTargetSlots.clone();
        return message;
    }

    @Override public void fromBytes(ByteBuf buffer) {
        injectorMaxEnergy = buffer.readInt();
        injectorActionCost = buffer.readInt();
        radius = readArray(buffer);
        energyPerSecond = readArray(buffer);
        capacity = readArray(buffer);
        targetSlots = readArray(buffer);
    }

    @Override public void toBytes(ByteBuf buffer) {
        buffer.writeInt(injectorMaxEnergy);
        buffer.writeInt(injectorActionCost);
        writeArray(buffer, radius);
        writeArray(buffer, energyPerSecond);
        writeArray(buffer, capacity);
        writeArray(buffer, targetSlots);
    }

    private static void writeArray(ByteBuf buffer, int[] values) {
        for (int i = 0; i < Configuration.TIER_COUNT; i++) buffer.writeInt(values[i]);
    }

    private static int[] readArray(ByteBuf buffer) {
        int[] values = new int[Configuration.TIER_COUNT];
        for (int i = 0; i < values.length; i++) values[i] = buffer.readInt();
        return values;
    }
}
