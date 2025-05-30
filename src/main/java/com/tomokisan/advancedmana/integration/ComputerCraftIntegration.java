package com.tomokisan.advancedmana.integration;

import com.tomokisan.advancedmana.AdvancedMana;
import com.tomokisan.advancedmana.block.entity.ManaDetectorBlockEntity;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.math.Direction;

public class ComputerCraftIntegration {
    
    public static void register() {
        // Enregistrer le capability provider pour le Mana Detector
        ComputerCraftAPI.registerPeripheralProvider((world, pos, side) -> {
            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ManaDetectorBlockEntity manaDetector) {
                return manaDetector.getPeripheral(side);
            }
            return null;
        });
    }
}