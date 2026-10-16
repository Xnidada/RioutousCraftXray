package com.xray.client.gui;

import com.xray.injector.ContainerXrayInjector;
import com.xray.injector.TileXrayInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

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
        xSize = 194;
        ySize = 168;
    }

    @Override public void initGui() {
        super.initGui();
        buttonList.add(new StyledButton(0, guiLeft + 57, guiTop + 21, 29, 16, I18n.format("gui.rioutouscraftxray.inject")));
        buttonList.add(new StyledButton(1, guiLeft + 57, guiTop + 40, 29, 16, I18n.format("gui.rioutouscraftxray.clear")));
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
            buttonList.add(new StyledButton(100 + i, guiLeft + 86, guiTop + 19 + i * 9,
                    47, 10, prefix + (i + 1) + ":" + state));
        }
    }

    private static String shortTarget(String state) {
        int colon = state.indexOf(':');
        if (colon >= 0) state = state.substring(colon + 1);
        int bracket = state.indexOf('[');
        if (bracket >= 0) state = state.substring(0, bracket);
        return state.length() > 4 ? state.substring(0, 4) : state;
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
        fontRenderer.drawString(title, 97 - fontRenderer.getStringWidth(title) / 2, 7, 0xD0D0D0);
        fontRenderer.drawStringWithShadow(I18n.format("gui.rioutouscraftxray.energy_short",
                compactEnergy(container.tile.getEnergy()), compactEnergy(TileXrayInjector.MAX_ENERGY)), 14, 28, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.glasses"), 14, 39, 0xD0D0D0);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.ore"), 38, 39, 0xD0D0D0);
        fontRenderer.drawString(I18n.format("gui.rioutouscraftxray.targets"), 86, 8, 0xD0D0D0);
        if (container.tile.getTargets().isEmpty()) fontRenderer.drawString("-", 132, 8, 0x808080);
    }

    @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        int height = MathHelper.ceil(14.0F * (float) container.tile.getEnergy() / TileXrayInjector.MAX_ENERGY);
        if (height > 0) drawRect(guiLeft + 19, guiTop + 38 - height, guiLeft + 33, guiTop + 38, 0xFF20D8FF);
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
