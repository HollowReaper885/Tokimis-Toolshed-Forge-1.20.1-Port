package net.cozystudios.tokimistoolshed.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CopperPowderSnowBucketItem extends Item {
    private final Supplier<Item> emptyBucket;

    public CopperPowderSnowBucketItem(Supplier<Item> emptyBucket, Properties properties) {
        super(properties);
        this.emptyBucket = emptyBucket;
    }

    private ItemStack handleUse(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.NONE);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockPos pos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos placePos = pos.relative(direction);

        if (!world.mayInteract(user, placePos) || !user.mayUseItemAt(placePos, direction, stack)) {
            return null;
        }

        BlockState targetState = world.getBlockState(placePos);
        if (!targetState.canBeReplaced()) {
            return null;
        }

        world.setBlockAndUpdate(placePos, Blocks.POWDER_SNOW.defaultBlockState());
        world.gameEvent(user, GameEvent.BLOCK_PLACE, placePos);
        world.playSound(null, placePos, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, SoundSource.BLOCKS, 1.0F, 1.0F);
        user.awardStat(Stats.ITEM_USED.get(this));

        return getEmptiedCopperStack(stack, user, world);
    }

    private ItemStack getEmptiedCopperStack(ItemStack stack, Player player, Level world) {
        if (player.getAbilities().instabuild) {
            return stack;
        }

        int newDamage = stack.getDamageValue() + 1;
        if (newDamage >= stack.getMaxDamage()) {
            if (!world.isClientSide) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(emptyBucket.get());
        result.setDamageValue(newDamage);
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack result = handleUse(world, user, hand);
        if (result == null) return InteractionResultHolder.pass(user.getItemInHand(hand));
        return InteractionResultHolder.sidedSuccess(result, world.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.literal("\u00a77Weathers with use"));
        tooltip.add(Component.literal("\u00a77\u00a7c" + (stack.getMaxDamage() - stack.getDamageValue()) + " \u00a77uses before breaking"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
