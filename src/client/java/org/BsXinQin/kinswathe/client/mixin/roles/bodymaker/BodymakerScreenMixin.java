package org.BsXinQin.kinswathe.client.mixin.roles.bodymaker;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedHandledScreen;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.client.ui.BodymakerPlayerWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(LimitedInventoryScreen.class)
public abstract class BodymakerScreenMixin extends LimitedHandledScreen<PlayerScreenHandler> {
    @Shadow
    @Final
    public ClientPlayerEntity player;

    public BodymakerScreenMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    void renderBodymakerHeads(CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());

        if (gameWorldComponent.isRole(player, KinsWatheRoles.BODYMAKER)) {
            // 获取所有存活玩家实体
            List<AbstractClientPlayerEntity> players = MinecraftClient.getInstance().world.getPlayers();

            int apart = 36;
            int x = ((LimitedInventoryScreen) (Object) this).width / 2 - (players.size()) * apart / 2 + 9;
            int shouldBeY = (((LimitedInventoryScreen) (Object) this).height - 32) / 2;
            int y = shouldBeY + 80;

            for (int i = 0; i < players.size(); ++i) {
                AbstractClientPlayerEntity targetEntity = players.get(i);

                BodymakerPlayerWidget child = new BodymakerPlayerWidget(
                        ((LimitedInventoryScreen) (Object) this),
                        x + apart * i,
                        y,
                        targetEntity,   // 直接传递实体
                        i
                );
                addDrawableChild(child);
            }
        }
    }
}