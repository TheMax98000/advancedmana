package com.tomokisan.advancedmana.peripheral;

import com.tomokisan.advancedmana.AdvancedMana;
import com.tomokisan.advancedmana.block.entity.ManaDetectorBlockEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ModPeripheralProvider {
    
    public static void register() {
        // Enregistrement du peripheral provider pour Fabric
        PeripheralLookup.get().registerForBlockEntity(
            (blockEntity, direction) -> {
                if (blockEntity instanceof ManaDetectorBlockEntity manaDetector) {
                    return new ManaDetectorPeripheral(manaDetector);
                }
                return null;
            },
            AdvancedMana.MANA_DETECTOR_BLOCK_ENTITY
        );
        
        AdvancedMana.LOGGER.info("Registered ManaDetector peripheral provider");
    }
}