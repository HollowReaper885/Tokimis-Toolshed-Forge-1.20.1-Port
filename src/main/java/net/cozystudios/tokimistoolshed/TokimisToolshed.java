package net.cozystudios.tokimistoolshed;

import net.cozystudios.tokimistoolshed.compat.CopperCompat;
import net.cozystudios.tokimistoolshed.compat.ObsidianCompat;
import net.cozystudios.tokimistoolshed.loot.ModLootModifiers;
import net.cozystudios.tokimistoolshed.registry.CopperBucketBehaviors;
import net.cozystudios.tokimistoolshed.registry.ModCreativeTabs;
import net.cozystudios.tokimistoolshed.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TokimisToolshed.MOD_ID)
public class TokimisToolshed {
    public static final String MOD_ID = "tokimistoolshed";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public TokimisToolshed() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Optional compat items must be added to the DeferredRegister before it is registered to the bus.
        if (ModList.get().isLoaded("copperagebackport")) {
            CopperCompat.register(ModItems.ITEMS);
            LOGGER.info("Copper Age Backport detected! Registering copper excavator and hammer.");
        }
        if (ModList.get().isLoaded("obsidianequipmentrework")) {
            ObsidianCompat.register(ModItems.ITEMS);
            LOGGER.info("Obsidian Equipment Reworked detected! Registering obsidian excavator and hammer.");
        }

        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modBus);

        modBus.addListener(this::commonSetup);
        LOGGER.info("TokimisToolshed Initialized!");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CopperBucketBehaviors::register);
    }
}
