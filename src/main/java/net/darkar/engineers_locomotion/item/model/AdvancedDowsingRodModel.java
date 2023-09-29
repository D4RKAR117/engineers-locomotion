package net.darkar.engineers_locomotion.item.model;

import mod.azure.azurelib.model.GeoModel;
import net.darkar.engineers_locomotion.EngineersLocomotionMod;
import net.darkar.engineers_locomotion.item.tool.AdvancedDowsingRodItem;
import net.minecraft.resources.ResourceLocation;

public class AdvancedDowsingRodModel extends GeoModel<AdvancedDowsingRodItem> {
	
	private static final ResourceLocation model = new ResourceLocation(EngineersLocomotionMod.MOD_ID, "geo/item/advanced_dowsing_rod.geo.json");
	private static final ResourceLocation texture = new ResourceLocation(EngineersLocomotionMod.MOD_ID, "textures/item/advanced_dowsing_rod.png");
	private static final ResourceLocation animation = new ResourceLocation(EngineersLocomotionMod.MOD_ID, "animations/item/advanced_dowsing_rod.animation.json");

	@Override
	public ResourceLocation getModelResource(AdvancedDowsingRodItem object) {
		return model;
	}

	@Override
	public ResourceLocation getTextureResource(AdvancedDowsingRodItem object) {
		return texture;
	}

	@Override
	public ResourceLocation getAnimationResource(AdvancedDowsingRodItem object) {
		return animation;
	}
	
}
