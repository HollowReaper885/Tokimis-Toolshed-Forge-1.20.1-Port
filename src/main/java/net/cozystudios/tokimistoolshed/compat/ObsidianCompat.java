package net.cozystudios.tokimistoolshed.compat;

import net.cozystudios.tokimistoolshed.item.ModToolMaterials;
import net.cozystudios.tokimistoolshed.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** Obsidian tools, only registered when Obsidian Equipment Reworked is installed. */
public class ObsidianCompat {
    public static RegistryObject<Item> OBSIDIAN_EXCAVATOR;
    public static RegistryObject<Item> OBSIDIAN_HAMMER;
    public static RegistryObject<Item> OBSIDIAN_SCYTHE;
    public static RegistryObject<Item> OBSIDIAN_LUMBER_AXE;

    public static void register(DeferredRegister<Item> reg) {
        OBSIDIAN_EXCAVATOR = ModItems.excavator(reg, "obsidian_excavator", ModToolMaterials.OBSIDIAN, 2.75F, -3.0F, true);
        OBSIDIAN_HAMMER = ModItems.hammer(reg, "obsidian_hammer", ModToolMaterials.OBSIDIAN, 2.75F, -3.2F, true);
        OBSIDIAN_SCYTHE = ModItems.scythe(reg, "obsidian_scythe", ModToolMaterials.OBSIDIAN, 3.75F, -2.6F, true);
        OBSIDIAN_LUMBER_AXE = ModItems.lumberAxe(reg, "obsidian_lumber_axe", ModToolMaterials.OBSIDIAN, 5.0F, -3.0F, true);
    }
}
