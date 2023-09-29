package net.darkar.engineers_locomotion.item;

import net.darkar.engineers_locomotion.EngineersLocomotionMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.stringtemplate.v4.ST;

public class AllCreativeModeTabs {

	/**
	 * The deferred register for all creative mode tabs
	 */
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EngineersLocomotionMod.MOD_ID);
	
	public static final RegistryObject<CreativeModeTab> TOOLS_TAB = CREATIVE_TABS.register("tools_tab", () -> CreativeModeTab.builder()
		.icon(() -> new ItemStack(AllItems.ADVANCED_DOWSING_ROD.get()))
		.title(buildTabName("tools_tab"))
		.displayItems((displayParameters, output) -> {
			output.accept(AllItems.ADVANCED_DOWSING_ROD.get());
		})
		.build());

	/**
	 * Registers a creative mode tab into the creative mode tab registry
	 *
	 * @param eventBus The event bus to hook into for registration
	 */
	public static void register(IEventBus eventBus) {
		CREATIVE_TABS.register(eventBus);
	}
	
	public static Component buildTabName(String name) {
		String template = "creative_tab.%s.%s";
		String key = String.format(template, EngineersLocomotionMod.MOD_ID, name);
		return Component.translatable(key);
	}
}
