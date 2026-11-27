package net.cozystudios.tokimistoolshed.event;

import net.cozystudios.tokimistoolshed.TokimisToolshed;
import net.cozystudios.tokimistoolshed.item.ClippersItem;
import net.cozystudios.tokimistoolshed.item.ExcavatorItem;
import net.cozystudios.tokimistoolshed.item.HammerItem;
import net.cozystudios.tokimistoolshed.item.LumberAxeItem;
import net.cozystudios.tokimistoolshed.item.ScytheItem;
import net.cozystudios.tokimistoolshed.registry.ModItems;
import net.cozystudios.tokimistoolshed.util.LastBreakData;
import net.cozystudios.tokimistoolshed.util.LeafDecayScheduler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge-side replacements for the Fabric callbacks / mixins of the original mod.
 */
@Mod.EventBusSubscriber(modid = TokimisToolshed.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    /** Replaces Fabric's AttackBlockCallback: remember which face the player started breaking. */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) return;
        if (event.getFace() == null) return;
        LastBreakData.setFace(event.getEntity(), event.getFace());
    }

    /**
     * Replaces Fabric's PlayerBlockBreakEvents.AFTER: in creative mode vanilla never calls Item#mineBlock,
     * so trigger the area / tree behaviour manually.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer) || !player.isCreative()) return;
        if (!(event.getLevel() instanceof Level level) || level.isClientSide) return;

        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        if (item instanceof HammerItem || item instanceof ExcavatorItem || item instanceof ScytheItem
                || item instanceof LumberAxeItem || item instanceof ClippersItem) {
            item.mineBlock(stack, level, event.getState(), event.getPos(), player);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LeafDecayScheduler.onServerTick();
        }
    }

    /** Replaces Fabric's FuelRegistry entries: wooden tools burn for 300 ticks. */
    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        Item item = event.getItemStack().getItem();
        if (item == ModItems.WOODEN_EXCAVATOR.get()
                || item == ModItems.WOODEN_HAMMER.get()
                || item == ModItems.WOODEN_SCYTHE.get()
                || item == ModItems.WOODEN_LUMBER_AXE.get()) {
            event.setBurnTime(300);
        }
    }

    /** Replaces CowMilkMixin: right-clicking an adult cow with an empty copper bucket gives a copper milk bucket. */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Cow cow)) return;

        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ModItems.COPPER_BUCKET.get()) return;
        if (!cow.isAlive() || cow.isBaby()) return;

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();

        if (!player.level().isClientSide) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);

            ItemStack milkStack = new ItemStack(ModItems.COPPER_MILK_BUCKET.get());
            milkStack.setDamageValue(stack.getDamageValue());

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (stack.isEmpty()) {
                player.setItemInHand(hand, milkStack);
            } else if (!player.getInventory().add(milkStack)) {
                player.drop(milkStack, false);
            }
        }

        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
        event.setCanceled(true);
    }
}
