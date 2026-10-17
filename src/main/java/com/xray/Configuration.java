package com.xray;

import com.xray.reference.Reference;
import net.minecraftforge.common.config.Config;

@Config(modid = Reference.MOD_ID, name = "rioutouscraftxray", type = Config.Type.INSTANCE)
public class Configuration
{
    private static final int[] DEFAULT_RADIUS = {4, 6, 8, 12, 14, 16};
    private static final int[] DEFAULT_ENERGY_PER_SECOND = {100, 100, 100, 100, 100, 100};
    private static final int[] DEFAULT_CAPACITY = {30000, 60000, 100000, 150000, 200000, 300000};
    private static final int[] DEFAULT_TARGETS = {1, 2, 3, 4, 6, 8};

    @Config.Name("Injector maximum EU")
    @Config.Comment("Maximum EU stored by the X-Ray Injector.")
    public static int injectorMaxEnergy = 10000;

    @Config.Name("Injector action cost")
    @Config.Comment("EU consumed by one Inject or Clear operation.")
    public static int injectorActionCost = 2500;

    @Config.Name("Glasses radius")
    @Config.Comment("透视范围，按 L1 到 L6 顺序填写。")
    public static int[] glassesRadius = DEFAULT_RADIUS.clone();

    @Config.Name("Glasses EU per second")
    @Config.Comment("每秒耗电量，按 L1 到 L6 顺序填写。")
    public static int[] glassesEnergyPerSecond = DEFAULT_ENERGY_PER_SECOND.clone();

    @Config.Name("Glasses capacity")
    @Config.Comment("眼镜最大电量，按 L1 到 L6 顺序填写。")
    public static int[] glassesCapacity = DEFAULT_CAPACITY.clone();

    @Config.Name("Glasses target slots")
    @Config.Comment("每级可注入的矿物数量，按 L1 到 L6 顺序填写。")
    public static int[] glassesTargetSlots = DEFAULT_TARGETS.clone();

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
            "Server-side block registry IDs accepted by the injector when a mod does not provide an OreDictionary entry.",
            "Use modid:block or modid:block:metadata; metadata may be * for every variant."
    })
    public static String[] additionalOreBlocks = {};

    public static void sanitize() {
        injectorMaxEnergy = Math.max(1, injectorMaxEnergy);
        injectorActionCost = Math.max(1, Math.min(injectorMaxEnergy, injectorActionCost));
        glassesRadius = normalize(glassesRadius, DEFAULT_RADIUS, 1);
        glassesEnergyPerSecond = normalize(glassesEnergyPerSecond, DEFAULT_ENERGY_PER_SECOND, 1);
        glassesCapacity = normalize(glassesCapacity, DEFAULT_CAPACITY, 1);
        glassesTargetSlots = normalize(glassesTargetSlots, DEFAULT_TARGETS, 1);
        if (additionalOreBlocks == null) additionalOreBlocks = new String[0];
    }

    public static void applySynced(int maxEnergy, int actionCost, int[] radius, int[] energyPerSecond,
                                   int[] capacity, int[] targetSlots) {
        injectorMaxEnergy = maxEnergy;
        injectorActionCost = actionCost;
        glassesRadius = radius == null ? null : radius.clone();
        glassesEnergyPerSecond = energyPerSecond == null ? null : energyPerSecond.clone();
        glassesCapacity = capacity == null ? null : capacity.clone();
        glassesTargetSlots = targetSlots == null ? null : targetSlots.clone();
        sanitize();
    }

    private static int[] normalize(int[] values, int[] defaults, int minimum) {
        int[] result = values == null || values.length != defaults.length ? defaults.clone() : values.clone();
        for (int i = 0; i < result.length; i++) result[i] = Math.max(minimum, result[i]);
        return result;
    }
}
