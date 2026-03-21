package org.BsXinQin.kinswathe.entities;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.BsXinQin.kinswathe.component.PlayerEffectComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CaptureDeviceEntity extends Entity {
    private UUID ownerUuid;
    private int lifeTime = 0;

    public CaptureDeviceEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public void setOwner(UUID owner) {
        this.ownerUuid = owner;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    protected void readCustomDataFromNbt(@NotNull NbtCompound nbt) {
        if (nbt.containsUuid("owner")) {
            ownerUuid = nbt.getUuid("owner");
        }
        lifeTime = nbt.getInt("lifeTime");
    }

    @Override
    protected void writeCustomDataToNbt(@NotNull NbtCompound nbt) {
        if (ownerUuid != null) {
            nbt.putUuid("owner", ownerUuid);
        }
        nbt.putInt("lifeTime", lifeTime);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) return;

        ConfigWorldComponent config = ConfigWorldComponent.KEY.get(this.getWorld());
        int maxLifetimeTicks = config.TechnicianCaptureDeviceLifetimeSeconds * 20;
        int radius = config.TechnicianCaptureDeviceRadius;
        int stunTicks = config.TechnicianCaptureDeviceStunSeconds * 20;

        lifeTime++;
        if (lifeTime > maxLifetimeTicks) {
            this.discard();
            return;
        }

        Box box = this.getBoundingBox().expand(radius);
        List<PlayerEntity> players = this.getWorld().getEntitiesByClass(PlayerEntity.class, box, player ->
                player.isAlive() && !player.isSpectator() && !player.getUuid().equals(ownerUuid)
        );

        if (!players.isEmpty()) {
            // 定身 + 缓慢
            for (PlayerEntity player : players) {
                PlayerEffectComponent.KEY.get(player).setStunTicks(stunTicks);
                if (player instanceof ServerPlayerEntity sp) {
                    sp.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, stunTicks, 1, false, false, true));
                    sp.sendMessage(Text.literal("你被陷阱捕捉到了！").formatted(Formatting.RED), true);
                    sp.playSoundToPlayer(SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.0f, 1.0f);
                }
            }

            // 生成报告
            if (ownerUuid != null) {
                PlayerEntity owner = getWorld().getPlayerByUuid(ownerUuid);
                if (owner != null) {
                    // 移除旧报告
                    for (int i = 0; i < owner.getInventory().size(); i++) {
                        ItemStack stack = owner.getInventory().getStack(i);
                        if (stack.isOf(Items.PAPER)) {
                            Text name = stack.get(DataComponentTypes.CUSTOM_NAME);
                            if (name != null && name.getString().equals("捕捉检测报告")) {
                                owner.getInventory().setStack(i, ItemStack.EMPTY);
                            }
                        }
                    }

                    // 创建新报告
                    ItemStack reportStack = Items.PAPER.getDefaultStack();
                    reportStack.set(DataComponentTypes.CUSTOM_NAME,
                            Text.literal("捕捉检测报告").formatted(Formatting.RESET, Formatting.GOLD));

                    List<Text> loreLines = new ArrayList<>();
                    loreLines.add(Text.translatable("item.kinswathe.capture_report.tooltip", players.size())
                            .formatted(Formatting.GRAY));
                    for (PlayerEntity p : players) {
                        loreLines.add(Text.literal(" - " + p.getName().getString()).formatted(Formatting.WHITE));
                    }
                    reportStack.set(DataComponentTypes.LORE, new LoreComponent(loreLines));

                    owner.getInventory().offerOrDrop(reportStack);

                    if (owner instanceof ServerPlayerEntity sp) {
                        sp.playSoundToPlayer(SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    }
                }
            }

            this.discard();
        }
    }
}