package com.tomokisan.advancedmana.peripheral;

import com.tomokisan.advancedmana.block.entity.ManaDetectorBlockEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManaDetectorPeripheral implements IPeripheral {
    private final ManaDetectorBlockEntity blockEntity;
    
    public ManaDetectorPeripheral(ManaDetectorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }
    
    @Override
    @NotNull
    public String getType() {
        return "mana_detector";
    }
    
    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }
    
    @Override
    @NotNull
    public Object getTarget() {
        return blockEntity;
    }
    
    @Override
    public void attach(@NotNull IComputerAccess computer) {
        // Called when a computer connects to this peripheral
    }
    
    @Override
    public void detach(@NotNull IComputerAccess computer) {
        // Called when a computer disconnects from this peripheral
    }
    
    // === Lua Functions ===
    
    @LuaFunction
    public final int getMana() {
        return blockEntity.getMana();
    }
    
    @LuaFunction
    public final int getManaCap() {
        return blockEntity.getManaCap();
    }
    
    @LuaFunction
    public final boolean hasValidMana() {
        return blockEntity.hasValidMana();
    }
    
    @LuaFunction
    public final double getManaPercentage() {
        if (blockEntity.getManaCap() <= 0) return 0.0;
        return (double) blockEntity.getMana() / (double) blockEntity.getManaCap() * 100.0;
    }
}