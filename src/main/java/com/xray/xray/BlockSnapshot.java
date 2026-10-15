package com.xray.xray;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

final class BlockSnapshot {
    final BlockPos pos;
    final IBlockState state;

    BlockSnapshot(BlockPos pos, IBlockState state) {
        this.pos = pos;
        this.state = state;
    }
}
