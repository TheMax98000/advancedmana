package com.tomokisan.advancedmana;

import net.fabricmc.api.ClientModInitializer;

public class AdvancedManaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Initialisation côté client si nécessaire
        System.out.println("Advanced Mana client initialized!");
    }
}