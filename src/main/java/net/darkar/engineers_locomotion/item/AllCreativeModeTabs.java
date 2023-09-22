package net.darkar.engineers_locomotion.item;

import net.darkar.engineers_locomotion.EngineersLocomotionMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class AllCreativeModeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EngineersLocomotionMod.MOD_ID);
	
	public static void register(IEventBus eventBus) {
		CREATIVE_TABS.register(eventBus);
	}
}
