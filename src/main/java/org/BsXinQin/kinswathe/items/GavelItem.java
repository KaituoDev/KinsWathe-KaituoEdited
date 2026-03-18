package org.BsXinQin.kinswathe.items;

import dev.doctor4t.wathe.game.GameFunctions;
import lombok.SneakyThrows;
import net.minecraft.entity.LivingEntity;
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
import org.BsXinQin.kinswathe.packet.items.GavelC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

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

    @Override @SneakyThrows
    public void onStoppedUsing(@NotNull ItemStack stack, @NotNull World world, @NotNull LivingEntity livingEntity, int remainingUseTicks) {
        if (!(livingEntity instanceof @NotNull PlayerEntity player) || player.isSpectator())  {
            return;
        }
        if (remainingUseTicks >= this.getMaxUseTime(stack, player) - 10 || remainingUseTicks <= 5) {
            return;
        }
        if (!world.isClient) {
            return;
        }

        HitResult hitResult = ProjectileUtil.getCollision(player, entity -> entity instanceof @NotNull PlayerEntity target && GameFunctions.isPlayerAliveAndSurvival(target), 3.0F);
        if (hitResult instanceof @NotNull EntityHitResult entityHitResult) {
            Class<?> networkingClass = Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking");
            Method getMethod = networkingClass.getMethod("send", net.minecraft.network.packet.CustomPayload.class);
            getMethod.invoke(null, new GavelC2SPacket(entityHitResult.getEntity().getId()));
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