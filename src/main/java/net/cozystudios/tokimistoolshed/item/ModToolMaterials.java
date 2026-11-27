package net.cozystudios.tokimistoolshed.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * Tool tiers. Values taken from the Fabric 1.20.1 build (v1.3).
 * Mining levels use vanilla's integer levels; Forge falls back to those for unsorted tiers.
 */
public enum ModToolMaterials implements Tier {
    WOOD(144, 2.0f, 1.0f, 0, 10, () -> Ingredient.of(Items.OAK_PLANKS)),
    STONE(324, 4.0f, 1.5f, 1, 8, () -> Ingredient.of(Items.COBBLESTONE)),
    COPPER(477, 5.0f, 1.75f, 1, 11, () -> Ingredient.of(Items.COPPER_INGOT)),
    IRON(621, 6.0f, 2.0f, 2, 14, () -> Ingredient.of(Items.IRON_INGOT)),
    GOLD(81, 12.0f, 1.0f, 1, 4, () -> Ingredient.of(Items.GOLD_INGOT)),
    DIAMOND(3906, 8.0f, 2.5f, 3, 10, () -> Ingredient.of(Items.DIAMOND)),
    NETHERITE(5076, 9.0f, 3.0f, 4, 15, () -> Ingredient.of(Items.NETHERITE_INGOT)),
    OBSIDIAN(4000, 8.5f, 2.75f, 4, 12,
            () -> Ingredient.of(ItemTags.create(new ResourceLocation("obsidianequipmentrework", "obsidian_ingots"))));

    private final int durability;
    private final float miningSpeedMultiplier;
    private final float attackDamage;
    private final int miningLevel;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;
    private Ingredient repairCache;

    ModToolMaterials(int durability, float miningSpeedMultiplier, float attackDamage,
                     int miningLevel, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.durability = durability;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamage = attackDamage;
        this.miningLevel = miningLevel;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() { return durability; }

    @Override
    public float getSpeed() { return miningSpeedMultiplier; }

    @Override
    public float getAttackDamageBonus() { return attackDamage; }

    @Override
    public int getLevel() { return miningLevel; }

    @Override
    public int getEnchantmentValue() { return enchantability; }

    @Override
    public Ingredient getRepairIngredient() {
        if (repairCache == null) {
            repairCache = repairIngredient.get();
        }
        return repairCache;
    }
}
