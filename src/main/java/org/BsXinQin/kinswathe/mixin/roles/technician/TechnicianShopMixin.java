package org.BsXinQin.kinswathe.mixin.roles.technician;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.cca.WorldBlackoutComponent;
import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.KinsWatheShops;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerShopComponent.class)
public abstract class TechnicianShopMixin {

    @Shadow public int balance;
    @Shadow @Final @NotNull private PlayerEntity player;
    @Shadow public abstract void sync();

    @Unique private static final int WRENCH_INDEX = 0;
    @Unique private static final int CAPTURE_DEVICE_INDEX = 1;
    @Unique private static final int POWER_RESTORATION_INDEX = 2;

    @Inject(method = "tryBuy", at = @At("HEAD"), cancellable = true)
    void technicianBuy(int index, @NotNull CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (!gameWorld.isRole(player, KinsWatheRoles.TECHNICIAN)) return;

        ConfigWorldComponent config = ConfigWorldComponent.KEY.get(player.getWorld());

        if (index == WRENCH_INDEX) {
            purchaseWrench(config.TechnicianWrenchPrice);
        } else if (index == CAPTURE_DEVICE_INDEX) {
            purchaseCaptureDevice(config.TechnicianCaptureDevicePrice);
        } else if (index == POWER_RESTORATION_INDEX) {
            purchasePowerRestoration(config.TechnicianPowerRestorationPrice);
        } else {
            return;
        }
        ci.cancel();
    }

    private void purchaseWrench(int price) {
        if (balance >= price) {
            balance -= price;
            sync();
            player.giveItemStack(KinsWatheItems.WRENCH.getDefaultStack());
            KinsWatheShops.playBuySound(player);
        } else {
            KinsWatheShops.playFailSound(player);
        }
    }

    private void purchaseCaptureDevice(int price) {
        if (balance >= price) {
            balance -= price;
            sync();
            player.giveItemStack(KinsWatheItems.CAPTURE_DEVICE.getDefaultStack());
            KinsWatheShops.playBuySound(player);
        } else {
            KinsWatheShops.playFailSound(player);
        }
    }

    private void purchasePowerRestoration(int price) {
        World world = player.getWorld();
        WorldBlackoutComponent blackout = WorldBlackoutComponent.KEY.get(world);
        if (!blackout.isBlackoutActive()) {
            player.sendMessage(Text.literal("当前不是停电状态，无法购买").formatted(Formatting.RED), true);
            KinsWatheShops.playFailSound(player);
            return;
        }
        if (balance < price) {
            player.sendMessage(Text.literal("金币不足").formatted(Formatting.RED), true);
            KinsWatheShops.playFailSound(player);
            return;
        }
        if (player.getItemCooldownManager().isCoolingDown(KinsWatheItems.POWER_RESTORATION)) {
            player.sendMessage(Text.literal("电力恢复系统冷却中").formatted(Formatting.RED), true);
            KinsWatheShops.playFailSound(player);
            return;
        }

        balance -= price;
        sync();

        blackout.reset();
        for (ServerPlayerEntity p : world.getServer().getPlayerManager().getPlayerList()) {
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false, false));
        }
        world.playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.getItemCooldownManager().set(KinsWatheItems.POWER_RESTORATION, GameConstants.getInTicks(0, ConfigWorldComponent.KEY.get(player.getWorld()).TechnicianPowerRestorationCooldown));

        KinsWatheShops.playBuySound(player);


    }

}

