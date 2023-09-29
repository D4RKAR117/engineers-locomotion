package net.darkar.engineers_locomotion.item.renderer;

import mod.azure.azurelib.renderer.GeoItemRenderer;
import net.darkar.engineers_locomotion.item.model.AdvancedDowsingRodModel;
import net.darkar.engineers_locomotion.item.tool.AdvancedDowsingRodItem;

public class AdvancedDowsingRodRenderer extends GeoItemRenderer<AdvancedDowsingRodItem> {
	
	public AdvancedDowsingRodRenderer() {
		super(new AdvancedDowsingRodModel());
	}
}
