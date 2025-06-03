package com.tomokisan.advancedmana.integration.computercraft;

import com.tomokisan.advancedmana.block.entity.ManaDetectorBlockEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ManaDetectorPeripheralProvider implements IPeripheralProvider {
    
    @Override
    public IPeripheral getPeripheral(Level level, BlockPos pos, Direction side) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ManaDetectorBlockEntity manaDetector) {
            return manaDetector;
        }
        return null;
    }
}