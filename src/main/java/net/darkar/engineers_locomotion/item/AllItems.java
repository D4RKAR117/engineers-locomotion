package net.darkar.engineers_locomotion.item;

import net.darkar.engineers_locomotion.EngineersLocomotionMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AllItems {

	/**
	 * The deferred register for all items
	 */
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			EngineersLocomotionMod.MOD_ID);


	/**
	 * Registers an item into the item registry
	 * @param eventBus The event bus to hook into for registration
	 */
	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}
}
