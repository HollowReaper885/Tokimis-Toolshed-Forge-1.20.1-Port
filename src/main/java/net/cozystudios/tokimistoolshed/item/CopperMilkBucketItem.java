package net.cozystudios.tokimistoolshed.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CopperMilkBucketItem extends Item {
    private final Supplier<Item> emptyBucket;

    public CopperMilkBucketItem(Supplier<Item> emptyBucket, Properties properties) {
        super(properties);
        this.emptyBucket = emptyBucket;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!world.isClientSide) {
            // Same curing behaviour as a vanilla milk bucket (Forge-aware).
            user.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
        }

        if (user instanceof Player player && !player.getAbilities().instabuild) {
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

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.literal("\u00a77Weathers with use"));
        tooltip.add(Component.literal("\u00a77\u00a7c" + (stack.getMaxDamage() - stack.getDamageValue()) + " \u00a77uses before breaking"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
