package com.tomokisan.advancedmana.block.entity;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.util.HashMap;
import java.util.Map;

public class ManaDetectorPeripheral implements IPeripheral {
    private final ManaDetectorBlockEntity blockEntity;
    
    public ManaDetectorPeripheral(ManaDetectorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }
    
    @Override
    public String getType() {
        return "mana_detector";
    }
    
    @Override
    public boolean equals(IPeripheral other) {
        return this == other;
    }
    
    /**
     * Obtient la quantité actuelle de mana du bloc au-dessus
     * @return La quantité de mana
     */
    @LuaFunction
    public final int getMana() {
        return blockEntity.getManaFromAbove();
    }
    
    /**
     * Obtient la capacité maximale de mana du bloc au-dessus
     * @return La capacité maximale de mana
     */
    @LuaFunction
    public final int getMaxMana() {
        return blockEntity.getMaxManaFromAbove();
    }
    
    /**
     * Obtient des informations détaillées sur la mana du bloc au-dessus
     * @return Une table avec les informations de mana
     */
    @LuaFunction
    public final Map<String, Object> getManaInfo() {
        Map<String, Object> info = new HashMap<>();
        int currentMana = blockEntity.getManaFromAbove();
        int maxMana = blockEntity.getMaxManaFromAbove();
        
        info.put("current", currentMana);
        info.put("max", maxMana);
        info.put("percentage", maxMana > 0 ? (double) currentMana / maxMana * 100 : 0);
        info.put("isEmpty", currentMana == 0);
        info.put("isFull", currentMana >= maxMana && maxMana > 0);
        
        return info;
    }
}