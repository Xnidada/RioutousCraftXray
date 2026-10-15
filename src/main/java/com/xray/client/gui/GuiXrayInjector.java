package com.xray.client.gui;

import com.xray.injector.ContainerXrayInjector;
import com.xray.injector.TileXrayInjector;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;

public class GuiXrayInjector extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("rioutouscraftxray", "textures/gui/injector.png");
    private final ContainerXrayInjector container;
    private int targetCount = -1;
    private int selectedTarget = -1;

    public GuiXrayInjector(InventoryPlayer player, TileXrayInjector tile) {
        super(new ContainerXrayInjector(player, tile));
        container = (ContainerXrayInjector) inventorySlots;
        xSize = 256;
        ySize = 220;
    }

    @Override public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(0, guiLeft + 104, guiTop + 30, 70, 20, I18n.format("gui.rioutouscraftxray.inject")));
        buttonList.add(new GuiButton(1, guiLeft + 104, guiTop + 54, 70, 20, I18n.format("gui.rioutouscraftxray.clear")));
        rebuildTargetButtons();
    }

    private void rebuildTargetButtons() {
        buttonList.removeIf(button -> button.id >= 100);
        List<String> targets = container.tile.getTargets();
        targetCount = targets.size();
        selectedTarget = container.tile.getSelectedTarget();
        for (int i = 0; i < targets.size(); i++) {
            String state = targets.get(i);
            if (state.length() > 22) state = state.substring(0, 22);
            String prefix = container.tile.getSelectedTarget() == i ? "> " : "  ";
            buttonList.add(new GuiButton(100 + i, guiLeft + 174, guiTop + 24 + i * 22, 76, 20, prefix + (i + 1) + ": " + state));
        }
    }

    @Override public void updateScreen() {
        super.updateScreen();
        if (targetCount != container.tile.getTargets().size()
                || selectedTarget != container.tile.getSelectedTarget()) rebuildTargetButtons();
    }

    @Override protected void actionPerformed(GuiButton button) throws IOException {
        if ((button.id >= 0 && button.id <= 1) || button.id >= 100) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, button.id);
        }
    }

    @Override protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("container.rioutouscraftxray.injector"), 8, 6, 0x404040);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.glasses"), 38, 22, 0x404040);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.ore"), 76, 22, 0x404040);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.energy", container.tile.getEnergy()), 8, 82, 0x20D8FF);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.targets"), 174, 8, 0x404040);
    }

    @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
