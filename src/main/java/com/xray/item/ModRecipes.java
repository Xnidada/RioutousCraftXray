package com.xray.item;

import com.xray.reference.Reference;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModRecipes {
    @SubscribeEvent public static void register(RegistryEvent.Register<IRecipe> e) {
        for (ItemXrayGlasses glasses : ModItems.GLASSES) e.getRegistry().register(new XrayGlassesRecipe(glasses));
    }
    private ModRecipes() {}
}
