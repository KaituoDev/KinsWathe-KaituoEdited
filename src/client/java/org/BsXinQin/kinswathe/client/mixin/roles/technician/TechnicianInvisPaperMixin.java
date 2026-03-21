package org.BsXinQin.kinswathe.client.mixin.roles.technician;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemFeatureRenderer.class)
public class TechnicianInvisPaperMixin {
    @WrapOperation(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack hideMainHandItem(LivingEntity entity, Operation<ItemStack> original) {
        ItemStack stack = original.call(entity);
        if (entity instanceof PlayerEntity player && stack.isOf(KinsWatheItems.CAPTURE_DEVICE) && GameWorldComponent.KEY.get(player.getWorld()).isRole(player, KinsWatheRoles.TECHNICIAN)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @WrapOperation(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack hideOffHandItem(LivingEntity entity, Operation<ItemStack> original) {
        ItemStack stack = original.call(entity);
        if (entity instanceof PlayerEntity player && stack.isOf(KinsWatheItems.CAPTURE_DEVICE) && GameWorldComponent.KEY.get(player.getWorld()).isRole(player, KinsWatheRoles.TECHNICIAN)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }
}