package com.tomokisan.advancedmana;

import net.fabricmc.api.ClientModInitializer;

public class AdvancedManaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Le rendu se fait maintenant via les blockstates et textures animées
		// Plus besoin de renderer personnalisé !
	}
}