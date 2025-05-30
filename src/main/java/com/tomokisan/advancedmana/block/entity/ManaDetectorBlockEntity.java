package com.tomokisan.advancedmana.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import com.tomokisan.advancedmana.AdvancedMana;
import com.tomokisan.advancedmana.util.ManaHelper;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

public class ManaDetectorBlockEntity extends BlockEntity {
    
    private final ManaDetectorPeripheral peripheral;
    
    public ManaDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(AdvancedMana.MANA_DETECTOR_BLOCK_ENTITY, pos, state);
        this.peripheral = new ManaDetectorPeripheral(this);
    }
    
    // Nouvelle API CC:Tweaked - retourne le périphérique quand demandé
    @Nullable
    public IPeripheral getPeripheral(Direction side) {
        // Ne pas fournir de périphérique sur le côté supérieur (où se trouve le bloc à analyser)
        if (side == Direction.UP) {
            return null;
        }
        return peripheral;
    }
    
    /**
     * Obtient la quantité de mana du bloc au-dessus
     */
    public int getManaFromAbove() {
        if (world == null) return 0;
        
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);
        BlockEntity aboveEntity = world.getBlockEntity(abovePos);
        
        return ManaHelper.getManaFromBlock(aboveState, aboveEntity, world, abovePos);
    }
    
    /**
     * Obtient la capacité maximale de mana du bloc au-dessus
     */
    public int getMaxManaFromAbove() {
        if (world == null) return 0;
        
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);
        BlockEntity aboveEntity = world.getBlockEntity(abovePos);
        
        return ManaHelper.getMaxManaFromBlock(aboveState, aboveEntity, world, abovePos);
    }
}