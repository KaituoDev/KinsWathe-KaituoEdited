package org.BsXinQin.kinswathe.client.ui;

import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.util.ShopEntry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.packet.BodymakerC2SPacket;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class BodymakerPlayerWidget extends ButtonWidget {
    public final LimitedInventoryScreen screen;
    public final AbstractClientPlayerEntity targetEntity;

    public BodymakerPlayerWidget(LimitedInventoryScreen screen, int x, int y,
                                   @NotNull AbstractClientPlayerEntity targetEntity,
                                   int index) {
        super(x, y, 16, 16, Text.literal(""),
                (button) -> ClientPlayNetworking.send(new BodymakerC2SPacket(targetEntity.getUuid())),
                DEFAULT_NARRATION_SUPPLIER);
        this.screen = screen;
        this.targetEntity = targetEntity;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        org.BsXinQin.kinswathe.component.AbilityPlayerComponent abilityComp =
                org.BsXinQin.kinswathe.component.AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        if (abilityComp.cooldown > 0) {
            context.setShaderColor(0.25f, 0.25f, 0.25f, 0.5f);
        }

        super.renderWidget(context, mouseX, mouseY, delta);
        context.drawGuiTexture(ShopEntry.Type.WEAPON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
        PlayerSkinDrawer.draw(context, targetEntity.getSkinTextures().texture(), this.getX(), this.getY(), 16);

        if (abilityComp.cooldown > 0) {
            int remainingSeconds = abilityComp.cooldown / 20;
            context.drawText(MinecraftClient.getInstance().textRenderer,
                    remainingSeconds + "s", this.getX(), this.getY(), Color.RED.getRGB(), true);
        }

        context.setShaderColor(1f, 1f, 1f, 1f);

        if (this.isHovered()) {
            this.drawShopSlotHighlight(context, this.getX(), this.getY(), 0);
            Text name = targetEntity.getName();
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, name,
                    this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(name) / 2, this.getY() - 9);
        }
    }

    private void drawShopSlotHighlight(DrawContext context, int x, int y, int z) {
        int color = -1862287543;
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 14, color, color, z);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 14, x + 15, y + 15, color, color, z);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 15, x + 14, y + 16, color, color, z);
    }

    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {}
}