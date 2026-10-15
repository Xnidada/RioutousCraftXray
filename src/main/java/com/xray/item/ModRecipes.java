package com.xray.item;

import com.xray.reference.Reference;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModRecipes {
    @SubscribeEvent public static void register(RegistryEvent.Register<IRecipe> e) {
        register(e.getRegistry());
    }
    public static void register(IForgeRegistry<IRecipe> registry) {
        for (ItemXrayGlasses glasses : ModItems.GLASSES) {
            net.minecraft.util.ResourceLocation id = new net.minecraft.util.ResourceLocation("rioutouscraftxray", "glasses_l" + glasses.tier + "_ore");
            if (!registry.containsKey(id)) registry.register(new XrayGlassesRecipe(glasses));
        }
    }
    private ModRecipes() {}
}
