package net.cozystudios.tokimistoolshed.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbacusItem extends Item {
    private static final String TAG_BOUND_X = "boundPosX";
    private static final String TAG_BOUND_Y = "boundPosY";
    private static final String TAG_BOUND_Z = "boundPosZ";
    private static final int UNBOUND_SENTINEL = -999;

    public AbacusItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();

        if (isBound(stack)) {
            unbind(stack);
            if (!world.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Abacus unbound").withStyle(ChatFormatting.GOLD),
                        true
                );
            }
            return InteractionResult.SUCCESS;
        } else {
            BlockPos pos = context.getClickedPos();
            bind(stack, pos);
            if (!world.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Abacus bound to " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
                                .withStyle(ChatFormatting.GOLD),
                        true
                );
            }
            return InteractionResult.SUCCESS;
        }
    }

    public static boolean isBound(ItemStack stack) {
        if (!stack.hasTag()) return false;
        CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains(TAG_BOUND_Y)) return false;
        return nbt.getInt(TAG_BOUND_Y) != UNBOUND_SENTINEL;
    }

    @Nullable
    public static BlockPos getBoundPos(ItemStack stack) {
        if (!isBound(stack)) return null;
        CompoundTag nbt = stack.getTag();
        if (nbt == null) return null;
        return new BlockPos(
                nbt.getInt(TAG_BOUND_X),
                nbt.getInt(TAG_BOUND_Y),
                nbt.getInt(TAG_BOUND_Z)
        );
    }

    public static void bind(ItemStack stack, BlockPos pos) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(TAG_BOUND_X, pos.getX());
        nbt.putInt(TAG_BOUND_Y, pos.getY());
        nbt.putInt(TAG_BOUND_Z, pos.getZ());
    }

    public static void unbind(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(TAG_BOUND_Y, UNBOUND_SENTINEL);
    }

    public static int getDistance(ItemStack stack, @Nullable BlockPos target) {
        BlockPos bound = getBoundPos(stack);
        if (bound == null || target == null) return -1;
        return bound.distManhattan(target);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (isBound(stack)) {
            BlockPos pos = getBoundPos(stack);
            if (pos != null) {
                tooltip.add(Component.literal("Bound to: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
                        .withStyle(ChatFormatting.GOLD));
            }
        } else {
            tooltip.add(Component.literal("Right-click a block to bind").withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.literal("Right-click again to unbind").withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
