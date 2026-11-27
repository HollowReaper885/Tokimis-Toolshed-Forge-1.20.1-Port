package net.cozystudios.tokimistoolshed.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.cozystudios.tokimistoolshed.item.ClippersItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Replacement for the Fabric "ShearsLootMixin": when a block is broken with Clippers, the block's loot table is
 * rolled as if the tool were vanilla shears, so Clippers drop exactly what shears would
 * (leaves, cobwebs, vines, etc.).
 */
public class ClippersLootModifier extends LootModifier {
    public static final Supplier<Codec<ClippersLootModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, ClippersLootModifier::new)));

    public ClippersLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        // Only act for Clippers; vanilla shears (and anything else) fall through, which also prevents recursion.
        if (tool == null || !(tool.getItem() instanceof ClippersItem)) return generatedLoot;

        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (state == null || origin == null) return generatedLoot;

        LootParams.Builder builder = new LootParams.Builder(context.getLevel())
                .withParameter(LootContextParams.ORIGIN, origin)
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.SHEARS))
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, context.getParamOrNull(LootContextParams.BLOCK_ENTITY))
                .withOptionalParameter(LootContextParams.THIS_ENTITY, context.getParamOrNull(LootContextParams.THIS_ENTITY))
                .withOptionalParameter(LootContextParams.EXPLOSION_RADIUS, context.getParamOrNull(LootContextParams.EXPLOSION_RADIUS));

        LootTable table = context.getLevel().getServer().getLootData().getLootTable(state.getBlock().getLootTable());
        return table.getRandomItems(builder.create(LootContextParamSets.BLOCK));
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
