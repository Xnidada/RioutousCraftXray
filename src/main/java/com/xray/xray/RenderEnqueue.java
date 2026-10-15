package com.xray.xray;

import com.xray.Configuration;
import com.xray.reference.block.BlockData;
import com.xray.reference.block.BlockInfo;
import com.xray.item.ItemXrayGlasses;
import com.xray.utils.OutlineColor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;

/** Filters a main-thread world snapshot off-thread and prepares render data. */
public class RenderEnqueue implements Runnable {
    private final List<BlockSnapshot> snapshot;
    private final HashMap<String, BlockData> blocks;
    private final ItemXrayGlasses glasses;
    private final ItemStack glassesStack;
    private final BlockPos playerPos;
    private final int radius;

    public RenderEnqueue(List<BlockSnapshot> snapshot, HashMap<String, BlockData> blocks,
                         ItemXrayGlasses glasses, ItemStack glassesStack,
                         BlockPos playerPos, int radius) {
        this.snapshot = snapshot;
        this.blocks = blocks;
        this.glasses = glasses;
        this.glassesStack = glassesStack;
        this.playerPos = playerPos;
        this.radius = radius;
    }

    @Override public void run() {
        List<BlockInfo> renderQueue = new java.util.ArrayList<>();
        for (BlockSnapshot entry : snapshot) {
            IBlockState current = entry.state;
            if (Controller.blackList.contains(current.getBlock())) continue;
            IBlockState defaultState = current.getBlock().getDefaultState();
            boolean poweredTarget = glasses.hasTarget(glassesStack, current.toString())
                    || glasses.hasTarget(glassesStack, defaultState.toString());
            boolean currentExists = poweredTarget || blocks.containsKey(current.toString());
            boolean defaultExists = poweredTarget || blocks.containsKey(defaultState.toString());
            if (!currentExists && !defaultExists) continue;

            BlockData data = blocks.get(currentExists ? current.toString() : defaultState.toString());
            if (poweredTarget && data == null) data = targetData(current);
            if (data == null || !data.isDrawing()) continue;

            double distance = Math.sqrt(entry.pos.distanceSq(playerPos));
            double alpha = !Configuration.shouldFade ? 255 : Math.max(0, ((radius - distance) / radius) * 255);
            renderQueue.add(new BlockInfo(entry.pos, data.getColor().getColor(), alpha));
        }
        renderQueue.sort((a, b) -> Double.compare(b.distanceSq(playerPos), a.distanceSq(playerPos)));
        Render.replaceOres(renderQueue);
    }

    /** Updates one block immediately after a main-thread block event. */
    public static void checkBlock(BlockPos pos, IBlockState state, boolean add) {
        if (!Controller.drawOres()) return;
        String defaultState = state.getBlock().getDefaultState().toString();
        boolean poweredTarget = Controller.drawsTarget(state.toString()) || Controller.drawsTarget(defaultState);
        if (!poweredTarget && Controller.getBlockStore().getStore().isEmpty()) return;
        if (!poweredTarget && !Controller.getBlockStore().getStore().containsKey(defaultState)) return;
        if (!add) {
            Render.removeOre(new BlockInfo(pos, null, 0.0));
            return;
        }

        BlockData data = Controller.getBlockStore().getStore().get(defaultState);
        if (data == null && poweredTarget) data = targetData(state);
        if (data == null) return;
        double radius = Controller.getRadius();
        double distance = Math.sqrt(pos.distanceSq(com.xray.client.ClientState.mc.player.getPosition()));
        double alpha = !Configuration.shouldFade ? 255 : Math.max(0, ((radius - distance) / radius) * 255);
        Render.addOre(new BlockInfo(pos, data.getColor().getColor(), alpha));
    }

    private static BlockData targetData(IBlockState state) {
        return new BlockData(state.toString(), state.getBlock().getLocalizedName(), Block.getStateId(state),
                new OutlineColor(255, 255, 255), new ItemStack(state.getBlock()), true, 0);
    }
}
