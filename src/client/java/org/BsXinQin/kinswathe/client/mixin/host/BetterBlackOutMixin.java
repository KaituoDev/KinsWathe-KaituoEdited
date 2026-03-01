package org.BsXinQin.kinswathe.client.mixin.host;

import dev.doctor4t.wathe.Wathe;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import org.BsXinQin.kinswathe.client.KinsWatheInitializeClient;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class BetterBlackOutMixin {

    @Unique private static long INSIDE_TIME = 0;
    @Unique private static boolean OUTSIDE = true;

    @Unique private static long OUTSIDE_TIME = 0;
    @Unique private static boolean WAS_INSIDE = false;

    @Inject(method = "render", at = @At("HEAD"))
    private void getBetterBlackoutHud(@NotNull DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (!ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).EnableBetterBlackout) return;
        long currentTime = System.currentTimeMillis();
        long blackoutTime = KinsWatheInitializeClient.BLACKOUT_TIME;
        if (currentTime < blackoutTime) {
            boolean isOutside = Wathe.isSkyVisibleAdjacent(MinecraftClient.getInstance().player);
            if (OUTSIDE && !isOutside) {
                INSIDE_TIME = currentTime;
            }
            if (!OUTSIDE && isOutside) {
                OUTSIDE_TIME = currentTime;
                WAS_INSIDE = true;
            }
            OUTSIDE = isOutside;
            if (WatheClient.isPlayerAliveAndInSurvival() && !(WatheClient.isInstinctEnabled() || MinecraftClient.getInstance().player.hasStatusEffect(StatusEffects.NIGHT_VISION))) {
                int targetAlpha = getBlackoutAlpha(blackoutTime, currentTime);
                int finalAlpha;
                if (isOutside) {
                    if (WAS_INSIDE) {
                        long timeOutside = currentTime - OUTSIDE_TIME;
                        if (timeOutside < 1000) {
                            float progress = (float) timeOutside / 1000;
                            finalAlpha = (int) (targetAlpha * (1 - progress));
                            finalAlpha = MathHelper.clamp(finalAlpha, 0, targetAlpha);
                        } else {
                            WAS_INSIDE = false;
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    long timeInside = currentTime - INSIDE_TIME;
                    if (timeInside < 1000) {
                        float progress = (float) timeInside / 1000;
                        finalAlpha = (int) (targetAlpha * progress);
                        finalAlpha = MathHelper.clamp(finalAlpha, 0, targetAlpha);
                    } else {
                        finalAlpha = targetAlpha;
                    }
                }
                if (finalAlpha > 0) {
                    context.fill(0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), (finalAlpha << 24));
                }
            }
        }
    }

    @Mixin(SoundSystem.class)
    public static class SoundSystemMixin {
        @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"))
        private void onPlaySound(@NotNull SoundInstance sound, CallbackInfo ci) {
            if (MinecraftClient.getInstance().player == null) return;
            if (!ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).EnableBetterBlackout) return;
            if (sound.getId().equals(WatheSounds.AMBIENT_BLACKOUT.getId())) {
                KinsWatheInitializeClient.BLACKOUT_TIME = System.currentTimeMillis() + (GameConstants.BLACKOUT_MAX_DURATION * 50L);
            }
        }
    }

    @Unique
    private static int getBlackoutAlpha(long blackoutTime, long currentTime) {
        long startTime = blackoutTime - (GameConstants.BLACKOUT_MAX_DURATION * 50L);
        long fadeStartTime = startTime + (GameConstants.BLACKOUT_MIN_DURATION * 50L);
        int alpha;
        if (currentTime < fadeStartTime) {
            alpha = (int) (255 * 0.8f);
        } else {
            long fadeDuration = (GameConstants.BLACKOUT_MAX_DURATION - GameConstants.BLACKOUT_MIN_DURATION) * 50L;
            long fadeElapsed = currentTime - fadeStartTime;
            float progress = (float) fadeElapsed / fadeDuration;
            progress = MathHelper.clamp(progress, 0f, 1f);
            alpha = (int) (255 * 0.8f * (1 - progress));
        }
        return alpha;
    }
}