package net.cozystudios.tokimistoolshed.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;

public class CopperBucketBehaviors {

    public static void register() {
        registerCauldronBehaviors();
        registerDispenserBehaviors();
    }

    private static BlockState waterCauldron() {
        return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
    }

    private static BlockState powderSnowCauldron() {
        return Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
    }

    private static void registerCauldronBehaviors() {
        Item copperBucket = ModItems.COPPER_BUCKET.get();
        Item waterBucket = ModItems.COPPER_WATER_BUCKET.get();
        Item lavaBucket = ModItems.COPPER_LAVA_BUCKET.get();
        Item powderSnowBucket = ModItems.COPPER_POWDER_SNOW_BUCKET.get();

        CauldronInteraction fillWater = (state, world, pos, player, hand, stack) ->
                fillCauldron(world, pos, player, hand, stack, waterCauldron(), SoundEvents.BUCKET_EMPTY);
        CauldronInteraction fillLava = (state, world, pos, player, hand, stack) ->
                fillCauldron(world, pos, player, hand, stack, Blocks.LAVA_CAULDRON.defaultBlockState(),
                        SoundEvents.BUCKET_EMPTY_LAVA);
        CauldronInteraction fillPowderSnow = (state, world, pos, player, hand, stack) ->
                fillCauldron(world, pos, player, hand, stack, powderSnowCauldron(),
                        SoundEvents.BUCKET_EMPTY_POWDER_SNOW);

        // Empty cauldron: pour contents in
        CauldronInteraction.EMPTY.put(waterBucket, fillWater);
        CauldronInteraction.EMPTY.put(lavaBucket, fillLava);
        CauldronInteraction.EMPTY.put(powderSnowBucket, fillPowderSnow);

        // Water cauldron
        CauldronInteraction.WATER.put(copperBucket, (state, world, pos, player, hand, stack) -> {
            int level = state.getValue(LayeredCauldronBlock.LEVEL);
            if (level == 3) {
                return emptyCauldron(world, pos, player, hand, stack, waterBucket, SoundEvents.BUCKET_FILL);
            }
            return InteractionResult.PASS;
        });
        CauldronInteraction.WATER.put(waterBucket, fillWater);
        CauldronInteraction.WATER.put(lavaBucket, fillLava);

        // Lava cauldron
        CauldronInteraction.LAVA.put(copperBucket, (state, world, pos, player, hand, stack) ->
                emptyCauldron(world, pos, player, hand, stack, lavaBucket, SoundEvents.BUCKET_FILL_LAVA));
        CauldronInteraction.LAVA.put(waterBucket, fillWater);
        CauldronInteraction.LAVA.put(powderSnowBucket, fillPowderSnow);

        // Powder snow cauldron
        CauldronInteraction.POWDER_SNOW.put(copperBucket, (state, world, pos, player, hand, stack) -> {
            int level = state.getValue(LayeredCauldronBlock.LEVEL);
            if (level == 3) {
                return emptyCauldron(world, pos, player, hand, stack, powderSnowBucket, SoundEvents.BUCKET_FILL_POWDER_SNOW);
            }
            return InteractionResult.PASS;
        });
        CauldronInteraction.POWDER_SNOW.put(powderSnowBucket, fillPowderSnow);
        CauldronInteraction.POWDER_SNOW.put(lavaBucket, fillLava);
    }

    private static InteractionResult fillCauldron(Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack,
                                                  BlockState cauldronState, SoundEvent sound) {
        if (!world.isClientSide) {
            player.setItemInHand(hand, damageBucketAndSwap(stack, player, world, ModItems.COPPER_BUCKET.get()));
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            world.setBlockAndUpdate(pos, cauldronState);
            world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    private static InteractionResult emptyCauldron(Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack,
                                                   Item filledBucket, SoundEvent sound) {
        if (!world.isClientSide) {
            ItemStack filledStack = new ItemStack(filledBucket);
            filledStack.setDamageValue(stack.getDamageValue());
            player.setItemInHand(hand, filledStack);
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            world.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    private static ItemStack damageBucketAndSwap(ItemStack original, Player player, Level world, Item targetBucket) {
        if (player.getAbilities().instabuild) {
            return original;
        }

        int newDamage = original.getDamageValue() + 1;
        if (newDamage >= original.getMaxDamage()) {
            if (!world.isClientSide) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(targetBucket);
        result.setDamageValue(newDamage);
        return result;
    }

    private static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.COPPER_WATER_BUCKET.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                ServerLevel world = pointer.getLevel();
                BlockPos pos = pointer.getPos().relative(pointer.getBlockState().getValue(DispenserBlock.FACING));
                BlockState targetState = world.getBlockState(pos);

                if (targetState.canBeReplaced() || targetState.is(Blocks.WATER)) {
                    world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                    world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return dispenseDamagedBucket(stack);
                }
                return super.execute(pointer, stack);
            }
        });

        DispenserBlock.registerBehavior(ModItems.COPPER_LAVA_BUCKET.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                ServerLevel world = pointer.getLevel();
                BlockPos pos = pointer.getPos().relative(pointer.getBlockState().getValue(DispenserBlock.FACING));
                BlockState targetState = world.getBlockState(pos);

                if (targetState.canBeReplaced() || targetState.is(Blocks.LAVA)) {
                    world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
                    world.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return dispenseDamagedBucket(stack);
                }
                return super.execute(pointer, stack);
            }
        });

        DispenserBlock.registerBehavior(ModItems.COPPER_POWDER_SNOW_BUCKET.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                ServerLevel world = pointer.getLevel();
                BlockPos pos = pointer.getPos().relative(pointer.getBlockState().getValue(DispenserBlock.FACING));
                BlockState targetState = world.getBlockState(pos);

                if (targetState.canBeReplaced()) {
                    world.setBlockAndUpdate(pos, Blocks.POWDER_SNOW.defaultBlockState());
                    world.playSound(null, pos, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return dispenseDamagedBucket(stack);
                }
                return super.execute(pointer, stack);
            }
        });

        DispenserBlock.registerBehavior(ModItems.COPPER_BUCKET.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                ServerLevel world = pointer.getLevel();
                BlockPos pos = pointer.getPos().relative(pointer.getBlockState().getValue(DispenserBlock.FACING));
                BlockState targetState = world.getBlockState(pos);

                Item filledBucket = null;
                SoundEvent sound = null;

                if (targetState.getFluidState().is(Fluids.WATER) && targetState.getFluidState().isSource()) {
                    filledBucket = ModItems.COPPER_WATER_BUCKET.get();
                    sound = SoundEvents.BUCKET_FILL;
                } else if (targetState.getFluidState().is(Fluids.LAVA) && targetState.getFluidState().isSource()) {
                    filledBucket = ModItems.COPPER_LAVA_BUCKET.get();
                    sound = SoundEvents.BUCKET_FILL_LAVA;
                } else if (targetState.is(Blocks.POWDER_SNOW)) {
                    filledBucket = ModItems.COPPER_POWDER_SNOW_BUCKET.get();
                    sound = SoundEvents.BUCKET_FILL_POWDER_SNOW;
                }

                if (filledBucket != null) {
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                    ItemStack filledStack = new ItemStack(filledBucket);
                    filledStack.setDamageValue(stack.getDamageValue());
                    return filledStack;
                }
                return super.execute(pointer, stack);
            }
        });
    }

    private static ItemStack dispenseDamagedBucket(ItemStack original) {
        int newDamage = original.getDamageValue() + 1;
        if (newDamage >= original.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = new ItemStack(ModItems.COPPER_BUCKET.get());
        result.setDamageValue(newDamage);
        return result;
    }
}
