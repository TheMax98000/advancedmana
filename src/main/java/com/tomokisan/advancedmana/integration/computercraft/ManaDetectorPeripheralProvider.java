package com.tomokisan.advancedmana.integration.computercraft;

import com.tomokisan.advancedmana.block.entity.ManaDetectorBlockEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ManaDetectorPeripheralProvider {
    
    public static void register() {
        PeripheralLookup.get().registerForBlockEntity((blockEntity, direction) -> {
            if (blockEntity instanceof ManaDetectorBlockEntity manaDetector) {
                return manaDetector;
            }
            return null;
        }, com.tomokisan.advancedmana.AdvancedMana.MANA_DETECTOR_BLOCK_ENTITY);
    }
}