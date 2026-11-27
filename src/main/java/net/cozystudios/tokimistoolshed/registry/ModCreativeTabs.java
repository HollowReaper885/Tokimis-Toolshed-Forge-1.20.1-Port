package net.cozystudios.tokimistoolshed.registry;

import net.cozystudios.tokimistoolshed.TokimisToolshed;
import net.cozystudios.tokimistoolshed.compat.CopperCompat;
import net.cozystudios.tokimistoolshed.compat.ObsidianCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TokimisToolshed.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TOOLSHED_TAB = TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.IRON_EXCAVATOR.get()))
                    .title(Component.translatable("itemGroup.tokimistoolshed.tab"))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.WOODEN_EXCAVATOR.get());
                        output.accept(ModItems.STONE_EXCAVATOR.get());
                        add(output, CopperCompat.COPPER_EXCAVATOR);
                        output.accept(ModItems.IRON_EXCAVATOR.get());
                        output.accept(ModItems.GOLDEN_EXCAVATOR.get());
                        output.accept(ModItems.DIAMOND_EXCAVATOR.get());
                        add(output, ObsidianCompat.OBSIDIAN_EXCAVATOR);
                        output.accept(ModItems.NETHERITE_EXCAVATOR.get());

                        output.accept(ModItems.WOODEN_HAMMER.get());
                        output.accept(ModItems.STONE_HAMMER.get());
                        add(output, CopperCompat.COPPER_HAMMER);
                        output.accept(ModItems.IRON_HAMMER.get());
                        output.accept(ModItems.GOLDEN_HAMMER.get());
                        output.accept(ModItems.DIAMOND_HAMMER.get());
                        add(output, ObsidianCompat.OBSIDIAN_HAMMER);
                        output.accept(ModItems.NETHERITE_HAMMER.get());

                        output.accept(ModItems.WOODEN_SCYTHE.get());
                        output.accept(ModItems.STONE_SCYTHE.get());
                        add(output, CopperCompat.COPPER_SCYTHE);
                        output.accept(ModItems.IRON_SCYTHE.get());
                        output.accept(ModItems.GOLDEN_SCYTHE.get());
                        output.accept(ModItems.DIAMOND_SCYTHE.get());
                        add(output, ObsidianCompat.OBSIDIAN_SCYTHE);
                        output.accept(ModItems.NETHERITE_SCYTHE.get());

                        output.accept(ModItems.WOODEN_LUMBER_AXE.get());
                        output.accept(ModItems.STONE_LUMBER_AXE.get());
                        add(output, CopperCompat.COPPER_LUMBER_AXE);
                        output.accept(ModItems.IRON_LUMBER_AXE.get());
                        output.accept(ModItems.GOLDEN_LUMBER_AXE.get());
                        output.accept(ModItems.DIAMOND_LUMBER_AXE.get());
                        add(output, ObsidianCompat.OBSIDIAN_LUMBER_AXE);
                        output.accept(ModItems.NETHERITE_LUMBER_AXE.get());

                        output.accept(ModItems.CLIPPERS.get());
                        output.accept(ModItems.COPPER_BUCKET.get());
                        output.accept(ModItems.COPPER_WATER_BUCKET.get());
                        output.accept(ModItems.COPPER_LAVA_BUCKET.get());
                        output.accept(ModItems.COPPER_POWDER_SNOW_BUCKET.get());
                        output.accept(ModItems.COPPER_MILK_BUCKET.get());
                        output.accept(ModItems.TROWEL.get());
                        output.accept(ModItems.ABACUS.get());
                    })
                    .build());

    private static void add(CreativeModeTab.Output output, RegistryObject<Item> item) {
        if (item != null) {
            output.accept(item.get());
        }
    }
}
