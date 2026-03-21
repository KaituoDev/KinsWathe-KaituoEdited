package org.BsXinQin.kinswathe.client.mixin.roles.technician;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedHandledScreen;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LimitedInventoryScreen.class)
public abstract class TechnicianShopMixin extends LimitedHandledScreen<PlayerScreenHandler> {

    @Shadow @Final @NotNull public ClientPlayerEntity player;

    public TechnicianShopMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    void technicianShop(CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.isRole(player, KinsWatheRoles.TECHNICIAN)) {
            List<ShopEntry> entries = new ArrayList<>();
            entries.add(new ShopEntry(KinsWatheItems.WRENCH.getDefaultStack(),
                    ConfigWorldComponent.KEY.get(player.getWorld()).TechnicianWrenchPrice, ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(KinsWatheItems.CAPTURE_DEVICE.getDefaultStack(),
                    ConfigWorldComponent.KEY.get(player.getWorld()).TechnicianCaptureDevicePrice, ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(KinsWatheItems.POWER_RESTORATION.getDefaultStack(),
                    ConfigWorldComponent.KEY.get(player.getWorld()).TechnicianPowerRestorationPrice, ShopEntry.Type.TOOL));

            int apart = 36;
            int x = width / 2 - entries.size() * apart / 2 + 9;
            int shouldBeY = (this.height - 32) / 2;
            int y = shouldBeY - 46;

            for (int i = 0; i < entries.size(); ++i) {
                addDrawableChild(new LimitedInventoryScreen.StoreItemWidget(
                        (LimitedInventoryScreen) (Object) this,
                        x + apart * i,
                        y,
                        entries.get(i),
                        i
                ));
            }
        }
    }
}