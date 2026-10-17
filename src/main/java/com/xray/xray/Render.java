package com.xray.xray;

import com.xray.Configuration;
import com.xray.reference.block.BlockInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Render
{
    public static List<BlockInfo> ores = Collections.synchronizedList( new ArrayList<>() ); // this is accessed by threads

	public static void clearOres() {
		synchronized (ores) { ores.clear(); }
	}

	public static void replaceOres(List<BlockInfo> blocks) {
		synchronized (ores) {
			ores.clear();
			ores.addAll(blocks);
		}
	}

	public static void addOre(BlockInfo block) {
		synchronized (ores) { ores.add(block); }
	}

	public static void removeOre(BlockInfo block) {
		synchronized (ores) { ores.remove(block); }
	}

    private static final int GL_FRONT_AND_BACK = 1032;
    private static final int GL_LINE = 6913;
    private static final int GL_FILL = 6914;
    private static final int GL_LINES = 1;

	public static void drawOres( float playerX, float playerY, float playerZ )
	{
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
		beginBlockOutlines();
        try {
            buffer.setTranslation( -playerX, -playerY, -playerZ );

		    synchronized (ores) {
			    for (BlockInfo b : ores) {
				    buffer.begin( GL_LINES, DefaultVertexFormats.POSITION_COLOR );
				    renderBlockBounding(buffer, b, (int) b.alpha);
				    tessellator.draw();
			        }
		    }
        } finally {
            buffer.setTranslation( 0, 0, 0 );
			endBlockOutlines();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
        }
	}

	private static void beginBlockOutlines() {
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.glLineWidth((float) Configuration.outlineThickness);
	}

	private static void endBlockOutlines() {
		GlStateManager.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
	}

	private static void renderBlockBounding(BufferBuilder buffer, BlockInfo b, int opacity) {
		if (b == null) return;
		final float size = 1.0f;
		int red = b.color[0], green = b.color[1], blue = b.color[2];
		int x = b.getX(), y = b.getY(), z = b.getZ();

		buffer.pos(x, y + size, z).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y + size, z).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y + size, z).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y + size, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y + size, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y + size, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y + size, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y + size, z).color(red, green, blue, opacity).endVertex();

		buffer.pos(x + size, y, z).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y, z).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y, z).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y, z).color(red, green, blue, opacity).endVertex();

		buffer.pos(x + size, y, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y + size, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y, z).color(red, green, blue, opacity).endVertex();
		buffer.pos(x + size, y + size, z).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y + size, z + size).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y, z).color(red, green, blue, opacity).endVertex();
		buffer.pos(x, y + size, z).color(red, green, blue, opacity).endVertex();
	}
}
