package com.tomokisan.advancedmana.integration;

import net.fabricmc.loader.api.FabricLoader;

public class ComputerCraftIntegration {
    
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("computercraft")) {
            // CC:Tweaked sur Fabric devrait automatiquement détecter les BlockEntity
            // qui ont une méthode getPeripheral(Direction)
            System.out.println("CC:Tweaked detected - Mana Detector peripherals should be available");
        }
    }
}