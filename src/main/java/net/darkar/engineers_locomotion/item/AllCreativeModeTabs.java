package net.darkar.engineers_locomotion.item;

import net.darkar.engineers_locomotion.EngineersLocomotionMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class AllCreativeModeTabs {

	/**
	 * The deferred register for all creative mode tabs
	 */
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EngineersLocomotionMod.MOD_ID);

	/**
	 * Registers a creative mode tab into the creative mode tab registry
	 * @param eventBus The event bus to hook into for registration
	 */
	public static void register(IEventBus eventBus) {
		CREATIVE_TABS.register(eventBus);
	}
}
