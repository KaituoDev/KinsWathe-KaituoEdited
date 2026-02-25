package org.BsXinQin.kinswathe.roles.bodymaker;

import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheEntities;
import lombok.SneakyThrows;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.BodyDeathReasonComponent;
import org.BsXinQin.kinswathe.packet.BodymakerC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BodymakerAbility {

    @SneakyThrows
    public static void register(@NotNull BodymakerC2SPacket payload, @NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        if (gameWorld.isRole(player, KinsWatheRoles.BODYMAKER) && GameFunctions.isPlayerAliveAndSurvival(player) && ability.cooldown <= 0) {
            ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(payload.target());
            if (target != null) {
                PlayerBodyEntity playerBody = WatheEntities.PLAYER_BODY.create(target.getWorld());
                if (playerBody != null) {
                    playerBody.setPlayerUuid(target.getUuid());
                    Vec3d spawnPos = player.getPos().add(player.getRotationVector().normalize().multiply(1));
                    playerBody.refreshPositionAndAngles(spawnPos.getX(), player.getY(), spawnPos.getZ(), player.getYaw(), 0f);
                    playerBody.setYaw(player.getYaw());
                    playerBody.setHeadYaw(player.getYaw());
                    playerBody.prevYaw = player.getYaw();
                    playerBody.prevHeadYaw = player.getYaw();
                    playerBody.bodyYaw = player.getYaw();
                    playerBody.prevBodyYaw = player.getYaw();
                    playerBody.setPitch(0f);
                    playerBody.age = 0;
                    target.getWorld().spawnEntity(playerBody);
                    BodyDeathReasonComponent bodyDeathReason = BodyDeathReasonComponent.KEY.get(playerBody);
                    bodyDeathReason.deathReason = Identifier.of(payload.deathReason());
                    if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
                        Class<?> bodyDeathReasonClass = Class.forName("org.agmas.noellesroles.coroner.BodyDeathReasonComponent");
                        Field keyField = bodyDeathReasonClass.getField("KEY");
                        Object componentKey = keyField.get(null);
                        Method getComponentMethod = componentKey.getClass().getMethod("get", Object.class);
                        Object deathReasonInstance = getComponentMethod.invoke(componentKey, playerBody);
                        Field deathReasonField = bodyDeathReasonClass.getField("deathReason");
                        Field playerRoleField = bodyDeathReasonClass.getField("playerRole");
                        Method syncMethod = bodyDeathReasonClass.getMethod("sync");
                        deathReasonField.set(deathReasonInstance, Identifier.of(payload.deathReason()));
                        if (!KinsWatheConfig.HANDLER.instance().BodymakerAbilityFakeRole) {
                            if (gameWorld.isRole(target, KinsWatheRoles.BODYMAKER)) {
                                playerRoleField.set(deathReasonInstance, WatheRoles.KILLER.identifier());
                            } else {
                                playerRoleField.set(deathReasonInstance, gameWorld.getRole(target) != null ? gameWorld.getRole(target).identifier() : WatheRoles.CIVILIAN.identifier());
                            }
                        } else {
                            playerRoleField.set(deathReasonInstance, Identifier.of(payload.role()));
                        }
                        syncMethod.invoke(deathReasonInstance);
                    }
                }
            }
            player.playSoundToPlayer(SoundEvents.ENTITY_SKELETON_CONVERTED_TO_STRAY, SoundCategory.PLAYERS, 1.0f, 1.0f);
            ability.setAbilityCooldown(KinsWatheConfig.HANDLER.instance().BodymakerAbilityCooldown);
        }
    }
}