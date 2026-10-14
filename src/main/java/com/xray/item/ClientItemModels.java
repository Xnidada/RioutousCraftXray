package com.xray.item;

import com.xray.reference.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public final class ClientItemModels {
    @SubscribeEvent public static void register(ModelRegistryEvent event) {
        for (ItemXrayGlasses glasses : ModItems.GLASSES) ModelLoader.setCustomModelResourceLocation(glasses, 0, new ModelResourceLocation(glasses.getRegistryName(), "inventory"));
    }
    private ClientItemModels() {}
}
