package net.darkar.engineers_locomotion;

import com.mojang.logging.LogUtils;
import net.darkar.engineers_locomotion.block.AllBlocks;
import net.darkar.engineers_locomotion.item.AllItems;
import net.darkar.engineers_locomotion.item.AllCreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EngineersLocomotionMod.MOD_ID)
public class EngineersLocomotionMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "engineers_locomotion";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public EngineersLocomotionMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		// Load all the custom deferred registries
		AllItems.register(modEventBus);
		AllBlocks.register(modEventBus);
		AllCreativeModeTabs.register(modEventBus);

        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("[ENGINEERS LOCOMOTION] Common setup");

    }
	
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("[ENGINEERS LOCOMOTION] Server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("[ENGINEERS LOCOMOTION] Client setup");
        }
    }
}
