package com.xray.xray;

import com.xray.client.ClientState;
import com.xray.utils.WorldRegion;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import com.xray.item.ItemXrayGlasses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Controller
{
    // Block blackList
	public static ArrayList blackList = new ArrayList<Block>() {{
		add(Blocks.AIR);
		add(Blocks.BEDROCK);
		add(Blocks.STONE);
		add(Blocks.GRASS);
		add(Blocks.DIRT);
	}};

	private static Vec3i lastPlayerPos = null;

	// Thread management
	private static Future task;
    private static ExecutorService executor;

    // Public accessors
	public static ItemXrayGlasses getGlasses() {
		if (ClientState.mc == null || ClientState.mc.player == null) return null;
		ItemStack stack = ClientState.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() instanceof ItemXrayGlasses && ((ItemXrayGlasses) stack.getItem()).isUsable(stack) ? (ItemXrayGlasses) stack.getItem() : null;
	}
	public static ItemStack getGlassesStack() {
		return ClientState.mc.player == null ? ItemStack.EMPTY : ClientState.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
	}
	public static boolean drawsTarget(String state) {
		ItemXrayGlasses item = getGlasses();
		if (item == null) return false;
		return item.hasTarget(getGlassesStack(), state);
	}
    /** X-Ray is active automatically while a configured, powered helmet is worn. */
	public static boolean drawOres() {
		return ClientState.mc != null && ClientState.mc.world != null
				&& ClientState.mc.player != null && getGlasses() != null;
	}

	public static int getRadius() { ItemXrayGlasses glasses = getGlasses(); return glasses == null ? 0 : glasses.radius; }

	/**
	 * Precondition: world and player must be not null
	 * Has player moved since the last region scan?
	 * This method does not update the last player location so consecutive
	 * calls yield the same result.
	 * @return true if the player has moved since the last blockFinder call
	 */
	private static boolean playerHasMoved()
	{
		return lastPlayerPos == null
			|| lastPlayerPos.getX() != ClientState.mc.player.getPosition().getX()
			|| lastPlayerPos.getZ() != ClientState.mc.player.getPosition().getZ();
	}

	private static void updatePlayerPosition()
	{
		lastPlayerPos = ClientState.mc.player.getPosition();
	}

	/**
	 * Starts a region scan thread if possible, that is if:
	 * - we actually want to draw ores
	 * - we are not already scanning an area
	 * - either the player has moved since the last call
	 * - or we want to (and can) force a scan
	 *
	 * @param force should we force a block scan even if the player hasn't moved?
	 */
	public static synchronized void requestBlockFinder( boolean force )
	{
		if (!drawOres()) {
			if (executor != null || task != null) shutdownExecutor();
			return;
		}
		if (executor == null || executor.isShutdown()) executor = Executors.newSingleThreadExecutor();
		if ((task == null || task.isDone()) && (force || playerHasMoved()))
		{
			updatePlayerPosition(); // since we're about to run, update the last known position
			WorldRegion region = new WorldRegion( lastPlayerPos, getRadius() ); // the region to scan for ores
			ItemXrayGlasses glasses = getGlasses();
			if (glasses == null) return;
			List<BlockSnapshot> snapshot = WorldSnapshot.capture(ClientState.mc.world, region);
			task = executor.submit(new RenderEnqueue(snapshot, glasses, getGlassesStack().copy(), new net.minecraft.util.math.BlockPos(lastPlayerPos), glasses.radius));
		}
	}

	/**
	 * To be called at least when the game shutsdown
	 */
	public static void shutdownExecutor()
	{
		try { executor.shutdownNow(); }
		catch (Throwable ignore) {}
		executor = null;
		task = null;
		lastPlayerPos = null;
		Render.clearOres();
	}
}
