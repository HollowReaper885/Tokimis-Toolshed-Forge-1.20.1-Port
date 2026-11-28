package net.cozystudios.tokimistoolshed.registry;

import net.cozystudios.tokimistoolshed.TokimisToolshed;
import net.cozystudios.tokimistoolshed.item.AbacusItem;
import net.cozystudios.tokimistoolshed.item.ClippersItem;
import net.cozystudios.tokimistoolshed.item.CopperBucketItem;
import net.cozystudios.tokimistoolshed.item.CopperMilkBucketItem;
import net.cozystudios.tokimistoolshed.item.CopperPowderSnowBucketItem;
import net.cozystudios.tokimistoolshed.item.ExcavatorItem;
import net.cozystudios.tokimistoolshed.item.HammerItem;
import net.cozystudios.tokimistoolshed.item.LumberAxeItem;
import net.cozystudios.tokimistoolshed.item.ModToolMaterials;
import net.cozystudios.tokimistoolshed.item.ScytheItem;
import net.cozystudios.tokimistoolshed.item.TrowelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TokimisToolshed.MOD_ID);

    private static Item.Properties props(boolean fireproof) {
        Item.Properties p = new Item.Properties();
        return fireproof ? p.fireResistant() : p;
    }

    public static RegistryObject<Item> excavator(DeferredRegister<Item> reg, String name, Tier tier, float damage, float speed, boolean fireproof) {
        return reg.register(name, () -> new ExcavatorItem(tier, damage, speed, props(fireproof)));
    }

    public static RegistryObject<Item> hammer(DeferredRegister<Item> reg, String name, Tier tier, float damage, float speed, boolean fireproof) {
        return reg.register(name, () -> new HammerItem(tier, damage, speed, props(fireproof)));
    }

    public static RegistryObject<Item> scythe(DeferredRegister<Item> reg, String name, Tier tier, float damage, float speed, boolean fireproof) {
        return reg.register(name, () -> new ScytheItem(tier, damage, speed, props(fireproof)));
    }

    public static RegistryObject<Item> lumberAxe(DeferredRegister<Item> reg, String name, Tier tier, float damage, float speed, boolean fireproof) {
        return reg.register(name, () -> new LumberAxeItem(tier, damage, speed, props(fireproof)));
    }

    // Excavators
    public static final RegistryObject<Item> WOODEN_EXCAVATOR = excavator(ITEMS, "wooden_excavator", ModToolMaterials.WOOD, 1.0F, -3.0F, false);
    public static final RegistryObject<Item> STONE_EXCAVATOR = excavator(ITEMS, "stone_excavator", ModToolMaterials.STONE, 1.5F, -3.0F, false);
    public static final RegistryObject<Item> IRON_EXCAVATOR = excavator(ITEMS, "iron_excavator", ModToolMaterials.IRON, 2.0F, -3.0F, false);
    public static final RegistryObject<Item> GOLDEN_EXCAVATOR = excavator(ITEMS, "golden_excavator", ModToolMaterials.GOLD, 1.0F, -3.2F, false);
    public static final RegistryObject<Item> DIAMOND_EXCAVATOR = excavator(ITEMS, "diamond_excavator", ModToolMaterials.DIAMOND, 2.5F, -3.0F, false);
    public static final RegistryObject<Item> NETHERITE_EXCAVATOR = excavator(ITEMS, "netherite_excavator", ModToolMaterials.NETHERITE, 3.0F, -3.0F, true);

    // Hammers
    public static final RegistryObject<Item> WOODEN_HAMMER = hammer(ITEMS, "wooden_hammer", ModToolMaterials.WOOD, 1.0F, -3.2F, false);
    public static final RegistryObject<Item> STONE_HAMMER = hammer(ITEMS, "stone_hammer", ModToolMaterials.STONE, 1.5F, -3.2F, false);
    public static final RegistryObject<Item> IRON_HAMMER = hammer(ITEMS, "iron_hammer", ModToolMaterials.IRON, 2.0F, -3.2F, false);
    public static final RegistryObject<Item> GOLDEN_HAMMER = hammer(ITEMS, "golden_hammer", ModToolMaterials.GOLD, 1.0F, -2.8F, false);
    public static final RegistryObject<Item> DIAMOND_HAMMER = hammer(ITEMS, "diamond_hammer", ModToolMaterials.DIAMOND, 2.5F, -3.2F, false);
    public static final RegistryObject<Item> NETHERITE_HAMMER = hammer(ITEMS, "netherite_hammer", ModToolMaterials.NETHERITE, 3.0F, -3.2F, true);

    // Scythes
    public static final RegistryObject<Item> WOODEN_SCYTHE = scythe(ITEMS, "wooden_scythe", ModToolMaterials.WOOD, 5.0F, -2.6F, false);
    public static final RegistryObject<Item> STONE_SCYTHE = scythe(ITEMS, "stone_scythe", ModToolMaterials.STONE, 6.0F, -2.6F, false);
    public static final RegistryObject<Item> IRON_SCYTHE = scythe(ITEMS, "iron_scythe", ModToolMaterials.IRON, 7.0F, -2.6F, false);
    public static final RegistryObject<Item> GOLDEN_SCYTHE = scythe(ITEMS, "golden_scythe", ModToolMaterials.GOLD, 5.0F, -2.6F, false);
    public static final RegistryObject<Item> DIAMOND_SCYTHE = scythe(ITEMS, "diamond_scythe", ModToolMaterials.DIAMOND, 8.0F, -2.6F, false);
    public static final RegistryObject<Item> NETHERITE_SCYTHE = scythe(ITEMS, "netherite_scythe", ModToolMaterials.NETHERITE, 9.0F, -2.6F, true);

    // Lumber axes
    public static final RegistryObject<Item> WOODEN_LUMBER_AXE = lumberAxe(ITEMS, "wooden_lumber_axe", ModToolMaterials.WOOD, 6.0F, -3.4F, false);
    public static final RegistryObject<Item> STONE_LUMBER_AXE = lumberAxe(ITEMS, "stone_lumber_axe", ModToolMaterials.STONE, 10.0F, -3.4F, false);
    public static final RegistryObject<Item> IRON_LUMBER_AXE = lumberAxe(ITEMS, "iron_lumber_axe", ModToolMaterials.IRON, 10.0F, -3.3F, false);
    public static final RegistryObject<Item> GOLDEN_LUMBER_AXE = lumberAxe(ITEMS, "golden_lumber_axe", ModToolMaterials.GOLD, 6.0F, -3.2F, false);
    public static final RegistryObject<Item> DIAMOND_LUMBER_AXE = lumberAxe(ITEMS, "diamond_lumber_axe", ModToolMaterials.DIAMOND, 10.0F, -3.2F, false);
    public static final RegistryObject<Item> NETHERITE_LUMBER_AXE = lumberAxe(ITEMS, "netherite_lumber_axe", ModToolMaterials.NETHERITE, 11.0F, -3.2F, true);

    public static final RegistryObject<Item> CLIPPERS =
            ITEMS.register("clippers", () -> new ClippersItem(500, new Item.Properties()));

    // Copper buckets. The suppliers refer to each other lazily, so declaration order does not matter.
    public static final RegistryObject<Item> COPPER_BUCKET = ITEMS.register("copper_bucket",
            () -> new CopperBucketItem(Fluids.EMPTY, ModItems::copperBucket, ModItems::copperWaterBucket,
                    ModItems::copperLavaBucket, ModItems::copperPowderSnowBucket,
                    new Item.Properties().durability(16)));
    public static final RegistryObject<Item> COPPER_WATER_BUCKET = ITEMS.register("copper_water_bucket",
            () -> new CopperBucketItem(Fluids.WATER, ModItems::copperBucket, ModItems::copperWaterBucket,
                    ModItems::copperLavaBucket, ModItems::copperPowderSnowBucket,
                    new Item.Properties().durability(16)));
    public static final RegistryObject<Item> COPPER_LAVA_BUCKET = ITEMS.register("copper_lava_bucket",
            () -> new CopperBucketItem(Fluids.LAVA, ModItems::copperBucket, ModItems::copperWaterBucket,
                    ModItems::copperLavaBucket, ModItems::copperPowderSnowBucket,
                    new Item.Properties().durability(16)));
    public static final RegistryObject<Item> COPPER_POWDER_SNOW_BUCKET = ITEMS.register("copper_powder_snow_bucket",
            () -> new CopperPowderSnowBucketItem(ModItems::copperBucket,
                    new Item.Properties().durability(16)));
    public static final RegistryObject<Item> COPPER_MILK_BUCKET = ITEMS.register("copper_milk_bucket",
            () -> new CopperMilkBucketItem(ModItems::copperBucket,
                    new Item.Properties().durability(16)));

    public static final RegistryObject<Item> TROWEL =
            ITEMS.register("trowel", () -> new TrowelItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ABACUS =
            ITEMS.register("abacus", () -> new AbacusItem(new Item.Properties().stacksTo(1)));

    private static Item copperBucket() { return COPPER_BUCKET.get(); }
    private static Item copperWaterBucket() { return COPPER_WATER_BUCKET.get(); }
    private static Item copperLavaBucket() { return COPPER_LAVA_BUCKET.get(); }
    private static Item copperPowderSnowBucket() { return COPPER_POWDER_SNOW_BUCKET.get(); }
}
