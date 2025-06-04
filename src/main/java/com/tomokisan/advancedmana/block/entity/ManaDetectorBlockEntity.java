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
        
        // CORRECTION : Toujours mettre à jour les valeurs
        this.mana = newMana;
        this.manaCap = newManaCap;
        
        // Calculer l'état de détection actuel
        boolean nowDetectingMana = this.mana > 0 || this.manaCap > 0;
        
        // CORRECTION : Vérifier aussi l'état du blockstate pour être sûr
        BlockState currentState = level.getBlockState(pos);
        boolean blockStateDetecting = currentState.getValue(com.tomokisan.advancedmana.block.ManaDetectorBlock.DETECTING_MANA);
        
        // Mettre à jour si l'état a changé OU si le blockstate est désynchronisé
        if (nowDetectingMana != this.wasDetectingMana || nowDetectingMana != blockStateDetecting) {
            this.wasDetectingMana = nowDetectingMana;
            
            BlockState newState = currentState.setValue(com.tomokisan.advancedmana.block.ManaDetectorBlock.DETECTING_MANA, nowDetectingMana);
            level.setBlock(pos, newState, 3);
            level.getLightEngine().checkBlock(pos);
            setChanged();
            level.sendBlockUpdated(pos, newState, newState, 3);
            
            AdvancedMana.LOGGER.info("ManaDetector at {} detection state changed: {} (mana={}, manaCap={})", 
                pos, nowDetectingMana, newMana, newManaCap);
        }
        
        // Log pour debug (commentez cette ligne si trop de spam)
        // AdvancedMana.LOGGER.info("ManaDetector at {} read: mana={}, manaCap={}, detecting={}", 
        //     pos, mana, manaCap, nowDetectingMana);
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
        tag.putBoolean("WasDetectingMana", wasDetectingMana); // CORRECTION : Sauvegarder l'état
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        mana = tag.getInt("mana");
        manaCap = tag.getInt("manaCap");
        tickCounter = tag.getInt("TickCounter");
        wasDetectingMana = tag.getBoolean("WasDetectingMana"); // CORRECTION : Charger l'état
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("mana", mana);
        tag.putInt("manaCap", manaCap);
        tag.putBoolean("WasDetectingMana", wasDetectingMana); // CORRECTION : Synchroniser l'état
        return tag;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}