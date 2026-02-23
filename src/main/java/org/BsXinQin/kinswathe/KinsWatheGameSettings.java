package org.BsXinQin.kinswathe;

import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.api.event.AllowPlayerDeath;
import dev.doctor4t.wathe.api.event.GameEvents;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheEntities;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.index.WatheSounds;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.GameSafeComponent;
import org.BsXinQin.kinswathe.packet.AbilityC2SPacket;
import org.BsXinQin.kinswathe.packet.BodymakerC2SPacket;
import org.agmas.harpymodloader.events.ResetPlayerEvent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.coroner.BodyDeathReasonComponent;
import org.jetbrains.annotations.NotNull;

import static org.BsXinQin.kinswathe.KinsWatheRoles.BODYMAKER;

public class KinsWatheGameSettings {

    private static boolean GAME_START = false;
    private static boolean GAME_STOP = false;

    /// 设置游戏开始和结束功能
    public static void betterGameSettings() {
        //注册游戏开始时事件
        GameEvents.ON_GAME_START.register((gameMode) -> {GAME_START = true;});
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (GAME_START) {
                //切换物品栏
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    player.getInventory().selectedSlot = 8;
                }
                //指令
                setCommands(server);
                //游戏安全时间
                setGameSafeTime(server);
                GAME_START = false;
            }
        });
        //注册游戏结束时事件
        GameEvents.ON_GAME_STOP.register((gameMode) -> {GAME_STOP = true;});
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (GAME_STOP) {
                //指令
                setCommands(server);
                //游戏安全时间
                GameSafeComponent.resetGlobalSafeTicks();
                GAME_STOP = false;
            }
        });
    }

    /// 设置指令
    public static void setCommands(@NotNull MinecraftServer server) {
        server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "kill @e[type=wathe:player_body]");
        server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "kill @e[type=item]");
        server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "effect clear @a");
        if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
            server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), "kill @e[type=noellesroles:cube]");
        }
    }

    /// 设置游戏安全时间
    public static void setGameSafeTime(@NotNull MinecraftServer server) {
        if (!KinsWatheConfig.HANDLER.instance().EnableStartSafeTime) return;
        GameSafeComponent.resetGlobalSafeTicks();
        for (ServerPlayerEntity serverPlayer : server.getPlayerManager().getPlayerList()) {
            if (serverPlayer == null) return;
            //KinsWathe物品安全时间
            serverPlayer.getItemCooldownManager().set(KinsWatheItems.BLOWGUN, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(KinsWatheItems.HUNTING_KNIFE, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(KinsWatheItems.KNOCKOUT_DRUG, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(KinsWatheItems.POISON_INJECTOR, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            //Wathe物品安全时间
            serverPlayer.getItemCooldownManager().set(WatheItems.KNIFE, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(WatheItems.GRENADE, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(WatheItems.REVOLVER, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            serverPlayer.getItemCooldownManager().set(WatheItems.PSYCHO_MODE, GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            //HarpySimpleRoles物品安全时间
            if (FabricLoader.getInstance().isModLoaded("harpysimpleroles")) {
                serverPlayer.getItemCooldownManager().set(Registries.ITEM.get(Identifier.of("harpysimpleroles", "toxin")), GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
                serverPlayer.getItemCooldownManager().set(Registries.ITEM.get(Identifier.of("harpysimpleroles", "bandit_revolver")), GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            }
            //StarryExpress物品安全时间
            if (FabricLoader.getInstance().isModLoaded("starexpress")) {
                serverPlayer.getItemCooldownManager().set(Registries.ITEM.get(Identifier.of("starexpress", "tape")), GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            }
            //StupidExpress物品安全时间
            if (FabricLoader.getInstance().isModLoaded("stupid_express")) {
                serverPlayer.getItemCooldownManager().set(Registries.ITEM.get(Identifier.of("stupid_express", "lighter")), GameConstants.getInTicks(0, KinsWatheConfig.HANDLER.instance().StartingCooldown));
            }
            GameSafeComponent playerSafe = GameSafeComponent.KEY.get(serverPlayer);
            playerSafe.startGameSafe();
        }
    }

    /// 初始化配置文件
    public static void initializeConfig() {
        KinsWatheConfig.HANDLER.load();
    }

    /// 注册网络数据包
    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(AbilityC2SPacket.ID, AbilityC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(BodymakerC2SPacket.ID,BodymakerC2SPacket.CODEC);
        //看代码给我看迷糊了这结构和noellesroles完全不一样毕竟我是个半吊子我完全只会在原基础修改，只能委屈心沁自己重新整理了
        //造尸怪的服务器端数据包处理器
        ServerPlayNetworking.registerGlobalReceiver(BodymakerC2SPacket.ID, (payload, context) -> {
            PlayerEntity bodymaker = context.player();
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(bodymaker.getWorld());

            if (gameWorld.isRole(bodymaker, BODYMAKER) && GameFunctions.isPlayerAliveAndSurvival(bodymaker)) {
                // 使用 KinsWathe 的冷却组件
                AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(bodymaker);

                if (abilityComp.cooldown > 0) {
                    return; // 冷却中
                }

                ServerPlayerEntity target = bodymaker.getServer().getPlayerManager().getPlayer(payload.target());
                if (target != null && target.isAlive() && !target.isSpectator() && !target.isCreative()) {
                    // 创建伪造尸体
                    PlayerBodyEntity body = WatheEntities.PLAYER_BODY.create(target.getWorld());
                    if (body != null) {
                        // ... 设置尸体属性...
                        body.setPlayerUuid(target.getUuid());
                        Vec3d spawnPos = bodymaker.getPos().add(bodymaker.getRotationVector().normalize().multiply(1));
                        body.refreshPositionAndAngles(spawnPos.getX(), bodymaker.getY(), spawnPos.getZ(), bodymaker.getYaw(), 0f);
                        body.setYaw(bodymaker.getYaw());
                        body.setHeadYaw(bodymaker.getYaw());
                        body.prevYaw = bodymaker.getYaw();
                        body.prevHeadYaw = bodymaker.getYaw();
                        body.bodyYaw = bodymaker.getYaw();
                        body.prevBodyYaw = bodymaker.getYaw();
                        body.setPitch(0f);
                        body.age = 0;
                        target.getWorld().spawnEntity(body);

                        // 设置死亡原因为"伪造的"
                        BodyDeathReasonComponent deathComp = BodyDeathReasonComponent.KEY.get(body);
                        deathComp.deathReason = KinsWatheRoles.FAKE_DEATH_REASON;
                        deathComp.playerRole = gameWorld.getRole(target) != null ?
                                gameWorld.getRole(target).identifier() : WatheRoles.CIVILIAN.identifier();
                        deathComp.sync();

                        bodymaker.playSoundToPlayer(WatheSounds.UI_SHOP_BUY, SoundCategory.PLAYERS, 1.0f, 1.0f);

                        // 设置冷却：45秒（直接操作 cooldown 字段并同步）
                        abilityComp.cooldown = GameConstants.getInTicks(0, 45); // 45秒的刻数
                        abilityComp.sync();

                        bodymaker.sendMessage(Text.translatable("tip.bodymaker.success", target.getName().getString()), true);
                    }
                }
            }
        });
    }

    /// 注册游戏事件
    public static void registerEvents() {
        //死亡事件
        AllowPlayerDeath.EVENT.register(((player, killer, identifier) -> {
            if (identifier == GameConstants.DeathReasons.FELL_OUT_OF_TRAIN) return true;
            return !GameSafeComponent.KEY.get(player).isGameSafe;
        }));
    }

    /// 重置事件
    public static void resetEvents() {
        ResetPlayerEvent.EVENT.register(player -> {
            GameSafeComponent.KEY.get(player).reset();
            AbilityPlayerComponent.KEY.get(player).reset();
        });
    }

    /// 初始化方法
    public static void init() {
        //设置游戏开始和结束功能
        betterGameSettings();
        //初始化配置文件
        initializeConfig();
        //注册网络数据包
        registerPackets();
        //注册游戏事件
        registerEvents();
        //重置事件
        resetEvents();
    }
}
