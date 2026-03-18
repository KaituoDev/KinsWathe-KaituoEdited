package org.BsXinQin.kinswathe.packet.items;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheDeathReasons;
import org.jetbrains.annotations.NotNull;

public record GavelC2SPacket(int target) implements CustomPayload {

    public static final Identifier GAVEL_PLAYLOAD_ID = Identifier.of(KinsWathe.MOD_ID, "gavel");
    public static final Id<GavelC2SPacket> ID = new Id<>(GAVEL_PLAYLOAD_ID);
    public static final PacketCodec<PacketByteBuf, GavelC2SPacket> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, GavelC2SPacket::target, GavelC2SPacket::new);
    public @NotNull Id<? extends @NotNull CustomPayload> getId() {
        return ID;
    }

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<GavelC2SPacket> {
        @Override
        public void receive(@NotNull GavelC2SPacket payload, ServerPlayNetworking.@NotNull Context context) {
            ServerPlayerEntity player = context.player();
            if (!(player.getServerWorld().getEntityById(payload.target()) instanceof @NotNull PlayerEntity target)) {
                return;
            }
            if (target.distanceTo(player) > 3.0F) {
                return;
            }

            ServerWorld world = player.getServerWorld();
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
            var lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.setCosmetic(true);
            if (gameWorld.isInnocent(target)) {
                lightning.refreshPositionAfterTeleport(player.getX(), player.getY(), player.getZ());
                player.getWorld().spawnEntity(lightning);
                GameFunctions.killPlayer(player, true, player, KinsWatheDeathReasons.DEATH_PENALTY_DEATH_REASON);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, KinsWatheConfig.HANDLER.instance().JudgeAbilityGlowing * 20, 0, false, true, true));
            } else {
                lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
                target.getWorld().spawnEntity(lightning);
                GameFunctions.killPlayer(target, true, target, KinsWatheDeathReasons.DEATH_PENALTY_DEATH_REASON);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, KinsWatheConfig.HANDLER.instance().JudgeAbilityGlowing * 20, 0, false, true, true));
                player.swingHand(Hand.MAIN_HAND);
            }
            player.getInventory().removeStack(player.getInventory().selectedSlot);
        }
    }
}
