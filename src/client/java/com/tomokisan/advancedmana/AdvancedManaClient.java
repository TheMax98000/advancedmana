package com.tomokisan.advancedmana;

import com.tomokisan.advancedmana.client.render.ManaDetectorBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class AdvancedManaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Enregistrer le renderer pour le détecteur de mana
		BlockEntityRendererRegistry.register(
			AdvancedMana.MANA_DETECTOR_BLOCK_ENTITY, 
			ManaDetectorBlockEntityRenderer::new
		);
	}
}