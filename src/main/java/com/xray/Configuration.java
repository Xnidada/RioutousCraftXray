package com.xray;

import com.xray.reference.Reference;
import net.minecraftforge.common.config.Config;

@Config(modid = Reference.MOD_ID, name = "rioutouscraftxray", type = Config.Type.INSTANCE)
public class Configuration
{
    @Config.Name("Fading Affect")
    @Config.Comment({
            "Fade outlines as they get farther from the player."
    })
    public static boolean shouldFade = true;

    @Config.Name("Outline thickness")
    @Config.Comment({
            "Set the thickness of the X-Ray block outlines."
    })
    @Config.RangeDouble(min = 0.5, max = 5.0)
    public static double outlineThickness = 1f;

    @Config.Name("Additional ore blocks")
    @Config.Comment({
            "Block registry IDs accepted by the injector when a mod does not provide an OreDictionary entry.",
            "Use modid:block or modid:block:metadata; metadata may be * for every variant."
    })
    public static String[] additionalOreBlocks = {};
}
