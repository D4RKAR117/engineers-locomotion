package net.darkar.engineers_locomotion.item.tool;

import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager.ControllerRegistrar;
import mod.azure.azurelib.core.animation.Animation;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.darkar.engineers_locomotion.item.renderer.AdvancedDowsingRodRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		AnimationController<GeoAnimatable> controller = new AnimationController<GeoAnimatable>(this, ANIMATION_CONTROLLER,
			0, event -> PlayState.CONTINUE);
		controller.triggerableAnim("nothing_found", RawAnimation.begin()
			.then("nothing_found", Animation.LoopType.PLAY_ONCE));

		controller.triggerableAnim("found_pitch_positive_20", RawAnimation.begin()
			.then("found_pitch_positive_20", Animation.LoopType.PLAY_ONCE));

		controller.triggerableAnim("found_pitch_negative_20", RawAnimation.begin()
			.then("found_pitch_negative_20", Animation.LoopType.PLAY_ONCE));

		controller.triggerableAnim("found_pitch_positive_45", RawAnimation.begin()
			.then("found_pitch_positive_45", Animation.LoopType.PLAY_ONCE));

		controller.triggerableAnim("found_pitch_negative_45", RawAnimation.begin()
			.then("found_pitch_negative_45", Animation.LoopType.PLAY_ONCE));

		controller.triggerableAnim("found_pitch_positive_90", RawAnimation.begin()
			.then("found_pitch_positive_90", Animation.LoopType.PLAY_ONCE));

		controller.triggerableAnim("found_pitch_negative_90", RawAnimation.begin()
			.then("found_pitch_negative_90", Animation.LoopType.PLAY_ONCE));

		controllers.add(controller);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private final AdvancedDowsingRodRenderer renderer = null;

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
			BlockPos toPos = new BlockPos(chunkPos.getMaxBlockX(), playerBlockPos.getY(), chunkPos.getMaxBlockZ());
			BlockPos fromPos = new BlockPos(chunkPos.getMinBlockX(), lowerLevelPos, chunkPos.getMinBlockZ());
			var currentItemStack = player.getItemInHand(usedHand);

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
						notifyPlayerVeinFound(player, level.getBlockState(blockItem).getBlock(), blockItem);
						int playerViewAngle = getPlayerViewAngleRelativeToBlock(player, blockItem);
						String animationName = String.format("found_pitch_%s%d", playerViewAngle < 0 ? "negative_" : "positive_", Math.abs(playerViewAngle));
						triggerAnim(player,
							GeoItem.getOrAssignId(currentItemStack,
								(ServerLevel) level),
							ANIMATION_CONTROLLER, animationName);
					} else {
						triggerAnim(player,
							GeoItem.getOrAssignId(currentItemStack,
								(ServerLevel) level),
							ANIMATION_CONTROLLER, "nothing_found");
						notifyPlayerNoVeinFound(player);
					}
					break;
				}
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
	private void notifyPlayerVeinFound(@NotNull Player player, @NotNull Block block, @NotNull BlockPos pos) {
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
	private void notifyPlayerNoVeinFound(@NotNull Player player) {
		String message = "No vein found on this chunk";
		player.sendSystemMessage(Component.literal(message));
	}

	/**
	 * Gets the player view deviation from the target block position
	 * clamp the value between -90 and 90
	 * - player view angle = 0 -> player is looking at the block
	 * - player view angle = 90 -> player is looking at the block from the side
	 * - player view angle = 180 -> player is looking at the block from behind
	 * - if negative -> player is looking at the block from the other side
	 *
	 * @param player         The player to get the view angle from
	 * @param targetBlockPos The block position to get the view angle to
	 * @return The player view angle deviation from the target block position
	 */
	private int getPlayerViewAngleRelativeToBlock(Player player, BlockPos targetBlockPos) {
		Vec3 playerPos = player.position();
		BlockPos playerBlockPos = new BlockPos((int) Math.round(playerPos.x), (int) Math.round(playerPos.y), (int) Math.round(playerPos.z));
		Vec3 playerLookVec = player.getLookAngle();
		Vec3 playerLookVecNormalized = playerLookVec.normalize();
		Vec3 playerToBlockVec = new Vec3(targetBlockPos.getX() - playerBlockPos.getX(), targetBlockPos.getY() - playerBlockPos.getY(), targetBlockPos.getZ() - playerBlockPos.getZ());
		Vec3 playerToBlockVecNormalized = playerToBlockVec.normalize();
		double dotProduct = playerLookVecNormalized.dot(playerToBlockVecNormalized);
		double rawAngle = Math.toDegrees(Math.acos(dotProduct));
		int angle = (int) Math.round(rawAngle);


		int[] positiveAngleAnchors = new int[]{20, 45, 90};
		int[] negativeAnglesAnchors = new int[]{-20, -45, -90};
		
		
		boolean isNegative = angle < 0;
        int[] anchors = isNegative ? negativeAnglesAnchors : positiveAngleAnchors;
		int nearestAnchor = anchors[0];
		for (int anchor : anchors) {
			if (Math.abs(angle - anchor) < Math.abs(angle - nearestAnchor)) {
				nearestAnchor = anchor;
			}
		}
		angle = nearestAnchor;
		
		
		
		return angle;
		
	}
}
