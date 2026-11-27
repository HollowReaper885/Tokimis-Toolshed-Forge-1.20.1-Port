package net.cozystudios.tokimistoolshed.item;

import net.cozystudios.tokimistoolshed.util.LastBreakData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClippersItem extends ShearsItem {
    private static boolean isClipping = false;

    public ClippersItem(int durability, Properties properties) {
        super(properties.durability(durability));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
        boolean result = super.mineBlock(stack, world, state, pos, miner);

        if (world.isClientSide || isClipping) return result;
        if (!(miner instanceof ServerPlayer serverPlayer)) return result;
        boolean isCreative = serverPlayer.gameMode.getGameModeForPlayer().isCreative();
        boolean isSurvival = serverPlayer.gameMode.getGameModeForPlayer().isSurvival();
        if (!isClippable(state)) return result;
        if (serverPlayer.isShiftKeyDown()) return result;
        if (!isCreative && !isSurvival) return result;

        try {
            isClipping = true;

            Direction face = LastBreakData.getFace(serverPlayer);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;

                    BlockPos targetPos = getOffsetPos(pos, face, dx, dy);
                    BlockState targetState = world.getBlockState(targetPos);

                    if (targetState.isAir()) continue;
                    if (!isClippable(targetState)) continue;
                    if (targetState.getDestroySpeed(world, targetPos) < 0) continue;

                    serverPlayer.gameMode.destroyBlock(targetPos);
                }
            }
        } finally {
            isClipping = false;
        }

        return result;
    }

    private boolean isClippable(BlockState state) {
        if (state.is(BlockTags.LEAVES)) return true;
        if (state.is(BlockTags.WOOL)) return true;
        Block block = state.getBlock();
        if (block instanceof BushBlock && !(block instanceof CropBlock)) return true;
        if (state.is(Blocks.COBWEB)) return true;
        if (state.is(Blocks.MOSS_BLOCK)) return true;
        if (state.is(Blocks.MOSS_CARPET)) return true;
        if (state.is(Blocks.VINE)) return true;
        if (state.is(Blocks.GLOW_LICHEN)) return true;
        if (state.is(Blocks.HANGING_ROOTS)) return true;
        if (state.is(Blocks.NETHER_SPROUTS)) return true;
        return false;
    }

    private BlockPos getOffsetPos(BlockPos origin, Direction face, int xOffset, int yOffset) {
        return switch (face) {
            case UP, DOWN -> origin.offset(xOffset, 0, yOffset);
            case NORTH, SOUTH -> origin.offset(xOffset, yOffset, 0);
            case EAST, WEST -> origin.offset(0, yOffset, xOffset);
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.literal("\u00a77Clips blocks in a \u00a7a3\u00d73 \u00a77area"));
        tooltip.add(Component.literal("\u00a77Hold \u00a7eShift \u00a77for single block"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
