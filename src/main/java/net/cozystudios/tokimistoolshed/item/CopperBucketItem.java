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
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CopperBucketItem extends BucketItem {
    private final Fluid bucketFluid;
    private final Supplier<Item> emptyBucket;
    private final Supplier<Item> waterBucket;
    private final Supplier<Item> lavaBucket;
    private final Supplier<Item> powderSnowBucket;

    @SuppressWarnings("deprecation")
    public CopperBucketItem(Fluid fluid, Supplier<Item> emptyBucket, Supplier<Item> waterBucket,
                            Supplier<Item> lavaBucket, Supplier<Item> powderSnowBucket, Properties properties) {
        super(fluid, properties);
        this.bucketFluid = fluid;
        this.emptyBucket = emptyBucket;
        this.waterBucket = waterBucket;
        this.lavaBucket = lavaBucket;
        this.powderSnowBucket = powderSnowBucket;
    }

    private ItemStack handleUse(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(world, user,
                this.bucketFluid == Fluids.EMPTY
                        ? ClipContext.Fluid.SOURCE_ONLY
                        : ClipContext.Fluid.NONE);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockPos pos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos offsetPos = pos.relative(direction);

        if (this.bucketFluid == Fluids.EMPTY) {
            if (!world.mayInteract(user, pos) || !user.mayUseItemAt(offsetPos, direction, stack)) {
                return null;
            }

            BlockState blockState = world.getBlockState(pos);
            if (blockState.getBlock() instanceof BucketPickup drainable) {
                ItemStack drainedStack = drainable.pickupBlock(world, pos, blockState);
                if (!drainedStack.isEmpty()) {
                    user.awardStat(Stats.ITEM_USED.get(this));
                    drainable.getPickupSound(blockState).ifPresent(sound ->
                            user.playSound(sound, 1.0F, 1.0F));
                    world.gameEvent(user, GameEvent.FLUID_PICKUP, pos);

                    Item filledItem;
                    if (drainedStack.is(Items.WATER_BUCKET)) {
                        filledItem = waterBucket.get();
                    } else if (drainedStack.is(Items.LAVA_BUCKET)) {
                        filledItem = lavaBucket.get();
                    } else if (drainedStack.is(Items.POWDER_SNOW_BUCKET)) {
                        filledItem = powderSnowBucket.get();
                    } else {
                        return ItemUtils.createFilledResult(stack, user, drainedStack);
                    }

                    ItemStack filledStack = new ItemStack(filledItem);
                    filledStack.setDamageValue(stack.getDamageValue());
                    return ItemUtils.createFilledResult(stack, user, filledStack);
                }
            }
        } else {
            BlockState blockState = world.getBlockState(pos);
            BlockPos placePos = canBlockContainFluid(world, pos, blockState) ? pos : offsetPos;

            if (!world.mayInteract(user, placePos) || !user.mayUseItemAt(placePos, direction, stack)) {
                return null;
            }

            if (this.emptyContents(user, world, placePos, hitResult, stack)) {
                this.checkExtraContent(user, world, stack, placePos);
                user.awardStat(Stats.ITEM_USED.get(this));
                return getEmptiedCopperStack(stack, user, world);
            }
        }

        return null;
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
