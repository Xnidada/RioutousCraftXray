package com.xray.utils;

import net.minecraft.util.math.Vec3i;

/**
 * A bounding box representing a world 3D area in both world and chunk coords.
 */
public class WorldRegion {

	public int minX, minY, minZ, maxX, maxY, maxZ;
	public int minChunkX, minChunkY, minChunkZ, maxChunkX, maxChunkY, maxChunkZ;

	/**
	 * Constructs an exact-width world region from a player location.
	 * Vertical extend is 92 blocks down and 32 blocks up
	 * @param pos a world position
	 * @param radius the horizontal width/depth in blocks
	 */
	public WorldRegion(Vec3i pos, int radius)
	{
		int lower = radius / 2;
		minX = pos.getX() - lower;
		maxX = minX + radius - 1;
		minY = Math.max(0, pos.getY() - 92);
		maxY = Math.min(255, pos.getY() + 32);
		minZ = pos.getZ() - lower;
		maxZ = minZ + radius - 1;
		minChunkX = minX >> 4;
		maxChunkX = maxX >> 4;
		minChunkY = minY >> 4;
		maxChunkY = maxY >> 4;
		minChunkZ = minZ >> 4;
		maxChunkZ = maxZ >> 4;
	}
}
