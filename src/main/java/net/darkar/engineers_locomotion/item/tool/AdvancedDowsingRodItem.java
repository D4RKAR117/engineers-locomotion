package net.darkar.engineers_locomotion.item.tool;

import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager.ControllerRegistrar;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.darkar.engineers_locomotion.item.renderer.AdvancedDowsingRodRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AdvancedDowsingRodItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
	
	private final String ANIMATION_CONTROLLER = "advanced_dowsing_rod_controller";

	public AdvancedDowsingRodItem(Properties itemProperties) {
		super(itemProperties);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		controllers.add(new AnimationController<GeoAnimatable>(this, ANIMATION_CONTROLLER,
			event -> PlayState.CONTINUE));
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private AdvancedDowsingRodRenderer renderer = null;

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				if (renderer == null)
					return new AdvancedDowsingRodRenderer();
				return this.renderer;
			}
		});
	}

	/**
	 * Checks if the block at the given position is an ore
	 *
	 * @param pos   The position to check
	 * @param level The level to check
	 * @return True if the block at the given position is an ore, false otherwise
	 */
	boolean isOre(BlockPos pos, Level level) {
		BlockState blockState = level.getBlockState(pos);
		Block[] ores = new Block[]{
			Blocks.COAL_ORE,
			Blocks.IRON_ORE,
			Blocks.GOLD_ORE,
			Blocks.LAPIS_ORE,
			Blocks.REDSTONE_ORE,
			Blocks.DIAMOND_ORE,
			Blocks.EMERALD_ORE,
			Blocks.COPPER_ORE,
		};
		for (Block ore : ores) {
			if (blockState.is(ore)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
		if (!level.isClientSide()) {
			// return if the current dimension is not the overworld
			if (!level.dimension().location().equals(Level.OVERWORLD.location()))
				return super.use(level, player, usedHand);


			// Defines the area to search for ores
			int lowerLevelPos = level.getMinBuildHeight();
			Vec3 playerPos = player.position();
			BlockPos playerBlockPos = new BlockPos((int) Math.round(playerPos.x), (int) Math.round(playerPos.y), (int) Math.round(playerPos.z));
			LevelChunk chunk = level.getChunkAt(playerBlockPos);
			ChunkPos chunkPos = chunk.getPos();
			BlockPos toPos = new BlockPos(chunkPos.getMaxBlockX(), lowerLevelPos, chunkPos.getMaxBlockZ());
			BlockPos fromPos = new BlockPos(chunkPos.getMinBlockX(), playerBlockPos.getY(), chunkPos.getMinBlockZ());

			// Defines the to search for similar of the first ore found
			// TODO: Make this configurable
			int oreScanRadius = 3;
			int minOresInVein = 3;


			var searchAreaResult = BlockPos.betweenClosed(fromPos, toPos);
			for (BlockPos blockItem : searchAreaResult) {
				if (isOre(blockItem, level)) {
					int oreDensity = 0;
					BlockPos positiveRadius = blockItem.offset(oreScanRadius, oreScanRadius, oreScanRadius);
					BlockPos negativeRadius = blockItem.offset(-oreScanRadius, -oreScanRadius, -oreScanRadius);

					var veinScanResult = BlockPos.betweenClosed(negativeRadius, positiveRadius);
					for (BlockPos veinItemPos : veinScanResult) {
						if (isOre(veinItemPos, level)) {
							oreDensity++;
						}
					}

					if (oreDensity >= minOresInVein) {
						NotifyPlayerVeinFound(player, level.getBlockState(blockItem).getBlock(), blockItem);
					} else {
						NotifyPlayerNoVeinFound(player);
					}
					break;
				}
				NotifyPlayerNoVeinFound(player);
				break;
			}
		}
		return super.use(level, player, usedHand);
	}

	/**
	 * Notifies the player that a vein was found
	 *
	 * @param player The player to notify
	 * @param block  The ore block found
	 * @param pos    The position of the main vein block
	 */
	private void NotifyPlayerVeinFound(Player player, Block block, BlockPos pos) {
		String BlockName = I18n.get(block.getDescriptionId());
		String template = "Vein of %s found at x: %d, y: %d, z: %d";
		String message = String.format(template, BlockName, pos.getX(), pos.getY(), pos.getZ());
		player.sendSystemMessage(Component.literal(message));
	}

	/**
	 * Notifies the player that no vein was found
	 *
	 * @param player The player to notify
	 */
	private void NotifyPlayerNoVeinFound(Player player) {
		String message = "No vein found on this chunk";
		player.sendSystemMessage(Component.literal(message));
	}
}
