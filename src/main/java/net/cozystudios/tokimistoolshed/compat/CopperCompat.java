package net.cozystudios.tokimistoolshed.compat;

import net.cozystudios.tokimistoolshed.item.ModToolMaterials;
import net.cozystudios.tokimistoolshed.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** Copper tools, only registered when Copper Age Backport is installed (same as the Fabric version). */
public class CopperCompat {
    public static RegistryObject<Item> COPPER_EXCAVATOR;
    public static RegistryObject<Item> COPPER_HAMMER;
    public static RegistryObject<Item> COPPER_SCYTHE;
    public static RegistryObject<Item> COPPER_LUMBER_AXE;

    public static void register(DeferredRegister<Item> reg) {
        COPPER_EXCAVATOR = ModItems.excavator(reg, "copper_excavator", ModToolMaterials.COPPER, 4.0F, -3.0F, false);
        COPPER_HAMMER = ModItems.hammer(reg, "copper_hammer", ModToolMaterials.COPPER, 1.75F, -3.2F, false);
        COPPER_SCYTHE = ModItems.scythe(reg, "copper_scythe", ModToolMaterials.COPPER, 6.0F, -2.6F, false);
        COPPER_LUMBER_AXE = ModItems.lumberAxe(reg, "copper_lumber_axe", ModToolMaterials.COPPER, 10.0F, -3.4F, false);
    }
}
