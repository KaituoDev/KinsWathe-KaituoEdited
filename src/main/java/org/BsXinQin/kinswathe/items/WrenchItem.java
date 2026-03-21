package org.BsXinQin.kinswathe.items;

import dev.doctor4t.wathe.block.SmallDoorBlock;
import dev.doctor4t.wathe.block_entity.DoorBlockEntity;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheSounds;
import dev.doctor4t.wathe.util.AdventureUsable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.jetbrains.annotations.NotNull;

public class WrenchItem extends Item implements AdventureUsable {
    public WrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public @NotNull ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        BlockEntity entity = world.getBlockEntity(pos);
        if (!(entity instanceof DoorBlockEntity)) {
            entity = world.getBlockEntity(pos.down());
        }

        if (entity instanceof DoorBlockEntity door && player != null) {
            boolean wasJammed = door.isJammed();
            boolean wasBlasted = door.isBlasted();

            if ((wasJammed || wasBlasted) && !world.isClient) {
                BlockPos lowerPos = pos;
                BlockState state = world.getBlockState(lowerPos);
                if (state.getBlock() instanceof SmallDoorBlock && state.get(SmallDoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                    lowerPos = lowerPos.down();
                    state = world.getBlockState(lowerPos);
                }

                if (wasJammed) {
                    door.setJammed(0);
                }
                if (wasBlasted) {
                    door.setBlasted(false);
                }
                door.markDirty();

                if (state.getBlock() instanceof SmallDoorBlock) {
                    DoorBlockEntity neighbor = SmallDoorBlock.getNeighborDoorEntity(state, world, lowerPos);
                    if (neighbor != null) {
                        if (neighbor.isJammed()) {
                            neighbor.setJammed(0);
                        }
                        if (neighbor.isBlasted()) {
                            neighbor.setBlasted(false);
                        }
                        neighbor.markDirty();
                    }
                }

                if (wasBlasted) {
                    world.playSound(null, lowerPos.getX() + 0.5, lowerPos.getY() + 1, lowerPos.getZ() + 0.5,
                            WatheSounds.ITEM_CROWBAR_PRY, SoundCategory.BLOCKS, 2.5f, 1.0f);
                } else if (wasJammed) {
                    world.playSound(null, lowerPos.getX() + 0.5, lowerPos.getY() + 1, lowerPos.getZ() + 0.5,
                            WatheSounds.ITEM_LOCKPICK_DOOR, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }

                if (!player.isCreative()) {
                    int cooldown = GameConstants.ITEM_COOLDOWNS.getOrDefault(this, 0);
                    if (cooldown > 0) {
                        player.getItemCooldownManager().set(this, cooldown);
                    }
                }

                player.swingHand(Hand.MAIN_HAND, true);
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }
}