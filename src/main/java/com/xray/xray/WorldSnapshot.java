package com.xray.xray;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.ArrayList;
import java.util.List;

/** Captures Minecraft world data on the client thread before async filtering. */
final class WorldSnapshot {
    private WorldSnapshot() {}

    static List<BlockSnapshot> capture(World world, com.xray.utils.WorldRegion box) {
        List<BlockSnapshot> result = new ArrayList<>();
        for (int chunkX = box.minChunkX; chunkX <= box.maxChunkX; chunkX++) {
            int x = chunkX << 4;
            int lowX = x < box.minX ? box.minX - x : 0;
            int highX = x + 15 > box.maxX ? box.maxX - x : 15;
            for (int chunkZ = box.minChunkZ; chunkZ <= box.maxChunkZ; chunkZ++) {
                // Never call getChunkFromChunkCoords here: it can synchronously generate/load
                // a chunk.  The scan must only inspect chunks already present on the client.
                Chunk chunk = world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
                if (chunk == null || !chunk.isLoaded()) continue;
                ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
                int z = chunkZ << 4;
                int lowZ = z < box.minZ ? box.minZ - z : 0;
                int highZ = z + 15 > box.maxZ ? box.maxZ - z : 15;
                for (int section = box.minChunkY; section <= box.maxChunkY; section++) {
                    ExtendedBlockStorage ebs = storage[section];
                    if (ebs == null) continue;
                    int y = section << 4;
                    int lowY = y < box.minY ? box.minY - y : 0;
                    int highY = y + 15 > box.maxY ? box.maxY - y : 15;
                    for (int i = lowX; i <= highX; i++) for (int j = lowY; j <= highY; j++) for (int k = lowZ; k <= highZ; k++) {
                        IBlockState state = ebs.get(i, j, k);
                        if (state.getBlock() != Blocks.AIR) result.add(new BlockSnapshot(new net.minecraft.util.math.BlockPos(x + i, y + j, z + k), state));
                    }
                }
            }
        }
        return result;
    }
}
