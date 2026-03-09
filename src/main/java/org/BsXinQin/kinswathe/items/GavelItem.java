package org.BsXinQin.kinswathe.items;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheDeathReasons;
import org.jetbrains.annotations.NotNull;

public class GavelItem extends Item {
    public GavelItem(@NotNull Settings settings) {
        super(settings);
    }

    @Override
    public @NotNull TypedActionResult<@NotNull ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        player.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void onStoppedUsing(@NotNull ItemStack stack, @NotNull World world, @NotNull LivingEntity livingEntity, int remainingUseTicks) {
        if (!(livingEntity instanceof PlayerEntity player) || player.isSpectator() || remainingUseTicks >= this.getMaxUseTime(stack, player) - 10)  {
            return;
        }
        HitResult hitResult = ProjectileUtil.getCollision(player, entity -> entity instanceof @NotNull PlayerEntity target && GameFunctions.isPlayerAliveAndSurvival(target), 3.0f);
        PlayerEntity targetPlayer = (hitResult instanceof @NotNull EntityHitResult entityHitResult) ? (PlayerEntity) entityHitResult.getEntity() : null;
        if (world.isClient || targetPlayer == null) {
            return;
        }

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        var lightning = new LightningEntity(net.minecraft.entity.EntityType.LIGHTNING_BOLT, targetPlayer.getWorld());
        lightning.setCosmetic(true);
        if (gameWorld.isInnocent(targetPlayer)) {
            lightning.refreshPositionAfterTeleport(player.getX(), player.getY(), player.getZ());
            player.getWorld().spawnEntity(lightning);
            GameFunctions.killPlayer(player, true, player, KinsWatheDeathReasons.DEATH_PENALTY_DEATH_REASON);
            player.playSound(WatheSounds.ITEM_PSYCHO_ARMOUR, 1.0f, 1.0f);
            targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, KinsWatheConfig.HANDLER.instance().JudgeAbilityGlowing * 20, 0, false, true, true));
        } else {
            lightning.refreshPositionAfterTeleport(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ());
            targetPlayer.getWorld().spawnEntity(lightning);
            GameFunctions.killPlayer(targetPlayer, true, player, KinsWatheDeathReasons.DEATH_PENALTY_DEATH_REASON);
            targetPlayer.playSound(WatheSounds.ITEM_PSYCHO_ARMOUR, 1.0f, 1.0f);
            player.swingHand(Hand.MAIN_HAND);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, KinsWatheConfig.HANDLER.instance().JudgeAbilityGlowing * 20, 0, false, true, true));
        }
        player.getInventory().removeStack(player.getInventory().selectedSlot);
    }

    @Override
    public void usageTick(@NotNull World world, @NotNull LivingEntity entity, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks <= 5 && entity instanceof PlayerEntity player) {
            player.stopUsingItem();
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity livingEntity) {
        return 200;
    }
}