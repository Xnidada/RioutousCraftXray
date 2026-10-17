package com.xray.client.gui;

import com.xray.injector.ContainerXrayInjector;
import com.xray.injector.TileXrayInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;
import java.util.List;

public class GuiXrayInjector extends GuiContainer {
    private final ContainerXrayInjector container;
    private int targetCount = -1;
    private int selectedTarget = -1;

    public GuiXrayInjector(InventoryPlayer player, TileXrayInjector tile) {
        super(new ContainerXrayInjector(player, tile));
        container = (ContainerXrayInjector) inventorySlots;
        xSize = 176;
        ySize = 166;
    }

    @Override public void initGui() {
        super.initGui();
        buttonList.add(new StyledButton(0, guiLeft + 8, guiTop + 61, 42, 18, I18n.format("gui.rioutouscraftxray.inject")));
        buttonList.add(new StyledButton(1, guiLeft + 53, guiTop + 61, 42, 18, I18n.format("gui.rioutouscraftxray.clear")));
        rebuildTargetButtons();
    }

    private void rebuildTargetButtons() {
        buttonList.removeIf(button -> button.id >= 100);
        List<String> targets = container.tile.getTargets();
        targetCount = targets.size();
        selectedTarget = container.tile.getSelectedTarget();
        for (int i = 0; i < targets.size(); i++) {
            String state = targets.get(i);
            state = shortTarget(state);
            String prefix = container.tile.getSelectedTarget() == i ? ">" : "";
            buttonList.add(new StyledButton(100 + i, guiLeft + 101, guiTop + 17 + i * 8,
                    67, 8, prefix + (i + 1) + ":" + state));
        }
    }

    private static String shortTarget(String state) {
        int colon = state.indexOf(':');
        if (colon >= 0) state = state.substring(colon + 1);
        int bracket = state.indexOf('[');
        if (bracket >= 0) state = state.substring(0, bracket);
        return state.length() > 6 ? state.substring(0, 6) : state;
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
        String title = I18n.format("container.rioutouscraftxray.injector");
        fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 0xE0E0E0);
        fontRenderer.drawStringWithShadow(I18n.format("gui.rioutouscraftxray.energy_short",
                compactEnergy(container.tile.getEnergy()), compactEnergy(TileXrayInjector.MAX_ENERGY)), 8, 20, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.glasses"), 8, 34, 0xD0D0D0);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.ore"), 30, 34, 0xD0D0D0);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.targets"), 101, 7, 0xD0D0D0);
        if (container.tile.getTargets().isEmpty()) fontRenderer.drawString("-", 158, 7, 0x808080);
    }

    @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xFF202020);
        drawRect(guiLeft + 1, guiTop + 1, guiLeft + xSize - 1, guiTop + ySize - 1, 0xFF555555);
        drawRect(guiLeft + 3, guiTop + 3, guiLeft + xSize - 3, guiTop + ySize - 3, 0xFF303030);

        drawPanel(guiLeft + 6, guiTop + 16, 88, 48);
        drawPanel(guiLeft + 98, guiTop + 14, 72, 69);
        drawRect(guiLeft + 6, guiTop + 79, guiLeft + xSize - 6, guiTop + 81, 0xFF171717);
        drawPanel(guiLeft + 6, guiTop + 82, 164, 77);

        drawSlotBackground(guiLeft + 8, guiTop + 41);
        drawSlotBackground(guiLeft + 30, guiTop + 41);
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            drawSlotBackground(guiLeft + 8 + col * 18, guiTop + 84 + row * 18);
        for (int col = 0; col < 9; col++) drawSlotBackground(guiLeft + 8 + col * 18, guiTop + 142);

        int height = (int) (28.0F * container.tile.getEnergy() / TileXrayInjector.MAX_ENERGY);
        drawRect(guiLeft + 82, guiTop + 34, guiLeft + 91, guiTop + 62, 0xFF111111);
        if (height > 0) drawRect(guiLeft + 83, guiTop + 62 - height, guiLeft + 90, guiTop + 62, 0xFF20D8FF);
    }

    private void drawPanel(int x, int y, int width, int height) {
        drawRect(x, y, x + width, y + height, 0xFF151515);
        drawRect(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF707070);
        drawRect(x + 2, y + 2, x + width - 2, y + height - 2, 0xFF383838);
    }

    private void drawSlotBackground(int x, int y) {
        drawRect(x - 1, y - 1, x + 17, y + 17, 0xFF111111);
        drawRect(x, y, x + 16, y + 16, 0xFF8B8B8B);
        drawRect(x + 1, y + 1, x + 15, y + 15, 0xFF373737);
    }

    private static String compactEnergy(double energy) {
        return ((int) energy / 1000) + "k";
    }

    private static final class StyledButton extends GuiButton {
        private StyledButton(int id, int x, int y, int width, int height, String text) {
            super(id, x, y, width, height, text);
        }

        @Override public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            if (!visible) return;
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            int fill = hovered ? 0xFF245C66 : 0xFF20292E;
            drawRect(x, y, x + width, y + height, fill);
            drawRect(x, y, x + width, y + 1, 0xFF6D9296);
            drawRect(x, y + height - 1, x + width, y + height, 0xFF0A1012);
            drawCenteredString(minecraft.fontRenderer, displayString, x + width / 2,
                    y + Math.max(1, (height - 8) / 2), 0xFFE0FFFF);
        }
    }
}
