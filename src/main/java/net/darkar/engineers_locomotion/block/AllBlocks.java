package net.darkar.engineers_locomotion.block;

import net.darkar.engineers_locomotion.EngineersLocomotionMod;
import net.darkar.engineers_locomotion.item.AllItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AllBlocks {
	
	/**
	 * The deferred register for all blocks
	 */
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			EngineersLocomotionMod.MOD_ID);
	
	/**
	 * Registers a block into the block registry
	 * 
	 * @param name The name of the block
	 * @param block The block supplier that will be used to create the block
	 * @return The registered block
	 */
	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
		RegistryObject<T> registeredBlock = BLOCKS.register(name, block);
		registerBlockItem(name, registeredBlock);
		return registeredBlock;
	}
	
	/**
	 * Registers a block's block item into the item registry
	 * 
	 * @param name The name of the block
	 * @param block The block to register
	 * @return The registered block item
	 */
	private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
		return  AllItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
	}

	/**
	 * Registers all blocks into the block registry 
	 * @param eventBus The event bus to hook into for registration
	 */
	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}
}
