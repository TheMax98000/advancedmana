package com.tomokisan.advancedmana.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import com.tomokisan.advancedmana.AdvancedMana;
import com.tomokisan.advancedmana.util.ManaHelper;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ManaDetectorBlockEntity extends BlockEntity implements IPeripheral {
    
    public ManaDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(AdvancedMana.MANA_DETECTOR_BLOCK_ENTITY, pos, state);
    }
    
    @Override
    public String getType() {
        return "mana_detector";
    }
    
    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }
    
    /**
     * Obtient la quantité actuelle de mana du bloc au-dessus
     * @return La quantité de mana
     */
    @LuaFunction
    public final int getMana() {
        return getManaFromAbove();
    }
    
    /**
     * Obtient la capacité maximale de mana du bloc au-dessus
     * @return La capacité maximale de mana
     */
    @LuaFunction
    public final int getMaxMana() {
        return getMaxManaFromAbove();
    }
    
    /**
     * Obtient des informations détaillées sur la mana du bloc au-dessus
     * @return Une table avec les informations de mana
     */
    @LuaFunction
    public final Map<String, Object> getManaInfo() {
        Map<String, Object> info = new HashMap<>();
        int currentMana = getManaFromAbove();
        int maxMana = getMaxManaFromAbove();
        
        info.put("current", currentMana);
        info.put("max", maxMana);
        info.put("percentage", maxMana > 0 ? (double) currentMana / maxMana * 100 : 0);
        info.put("isEmpty", currentMana == 0);
        info.put("isFull", currentMana >= maxMana && maxMana > 0);
        
        return info;
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