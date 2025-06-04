package com.tomokisan.advancedmana.block.entity;

import com.tomokisan.advancedmana.AdvancedMana;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ManaDetectorBlockEntity extends BlockEntity {
    
    private int mana = 0;
    private int manaCap = 0;
    private int tickCounter = 0;
    private static final int DETECTION_INTERVAL = 10;
    private boolean wasDetectingMana = false;
    
    public ManaDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(AdvancedMana.MANA_DETECTOR_BLOCK_ENTITY, pos, blockState);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, ManaDetectorBlockEntity blockEntity) {
        if (level.isClientSide()) return;
        
        blockEntity.tickCounter++;
        if (blockEntity.tickCounter >= DETECTION_INTERVAL) {
            blockEntity.tickCounter = 0;
            blockEntity.readManaFromAdjacentBlock(level, pos, state);
        }
    }
    
    private void readManaFromAdjacentBlock(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(com.tomokisan.advancedmana.block.ManaDetectorBlock.FACING);
        BlockPos targetPos = pos.relative(facing);
        
        BlockEntity adjacentBlockEntity = level.getBlockEntity(targetPos);
        
        int newMana = 0;
        int newManaCap = 0;
        
        if (adjacentBlockEntity != null) {
            CompoundTag adjacentNBT = adjacentBlockEntity.getUpdateTag();
            
            if (adjacentNBT.contains("mana")) {
                newMana = adjacentNBT.getInt("mana");
            }
            if (adjacentNBT.contains("manaCap")) {
                newManaCap = adjacentNBT.getInt("manaCap");
            }
        }
        
        if (newMana != this.mana || newManaCap != this.manaCap) {
            this.mana = newMana;
            this.manaCap = newManaCap;
            
            boolean nowDetectingMana = this.mana > 0 || this.manaCap > 0;
            
            if (nowDetectingMana != this.wasDetectingMana) {
                this.wasDetectingMana = nowDetectingMana;
                
                BlockState newState = state.setValue(com.tomokisan.advancedmana.block.ManaDetectorBlock.DETECTING_MANA, nowDetectingMana);
                level.setBlock(pos, newState, 3);
                level.getLightEngine().checkBlock(pos);
                setChanged();
                level.sendBlockUpdated(pos, newState, newState, 3);
                
                AdvancedMana.LOGGER.info("ManaDetector at {} detection state changed: {}", pos, nowDetectingMana);
            }
            
            AdvancedMana.LOGGER.info("ManaDetector at {} read: mana={}, manaCap={}", pos, mana, manaCap);
        }
    }
    
    public int getMana() {
        return mana;
    }
    
    public int getManaCap() {
        return manaCap;
    }
    
    public boolean hasValidMana() {
        return mana > 0 || manaCap > 0;
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("mana", mana);
        tag.putInt("manaCap", manaCap);
        tag.putInt("TickCounter", tickCounter);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        mana = tag.getInt("mana");
        manaCap = tag.getInt("manaCap");
        tickCounter = tag.getInt("TickCounter");
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("mana", mana);
        tag.putInt("manaCap", manaCap);
        return tag;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}