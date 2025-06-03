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
    
    // Variables pour stocker les données de mana lues
    private int mana = 0;
    private int manaCap = 0;
    private int tickCounter = 0;
    private static final int DETECTION_INTERVAL = 10; // Toutes les 0.5 secondes (10 ticks)
    
    public ManaDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(AdvancedMana.MANA_DETECTOR_BLOCK_ENTITY, pos, blockState);
    }
    
    // Méthode appelée à chaque tick du serveur
    public static void tick(Level level, BlockPos pos, BlockState state, ManaDetectorBlockEntity blockEntity) {
        if (level.isClientSide()) return; // Seulement côté serveur
        
        blockEntity.tickCounter++;
        
        // Effectuer la lecture des NBT toutes les 0.5 secondes
        if (blockEntity.tickCounter >= DETECTION_INTERVAL) {
            blockEntity.tickCounter = 0;
            blockEntity.readManaFromAdjacentBlock(level, pos, state);
        }
    }
    
    // Lecture des NBT tags mana et manaCap du bloc adjacent
    private void readManaFromAdjacentBlock(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(com.tomokisan.advancedmana.block.ManaDetectorBlock.FACING);
        BlockPos targetPos = pos.relative(facing);
        
        // Récupérer le BlockEntity du bloc adjacent
        BlockEntity adjacentBlockEntity = level.getBlockEntity(targetPos);
        
        int newMana = 0;
        int newManaCap = 0;
        
        if (adjacentBlockEntity != null) {
            // Utiliser getUpdateTag() qui est public pour lire les NBT
            CompoundTag adjacentNBT = adjacentBlockEntity.getUpdateTag();
            
            // Extraire les valeurs mana et manaCap
            if (adjacentNBT.contains("mana")) {
                newMana = adjacentNBT.getInt("mana");
            }
            if (adjacentNBT.contains("manaCap")) {
                newManaCap = adjacentNBT.getInt("manaCap");
            }
        }
        
        // Mettre à jour seulement si les valeurs ont changé
        if (newMana != this.mana || newManaCap != this.manaCap) {
            this.mana = newMana;
            this.manaCap = newManaCap;
            
            // Marquer comme modifié pour sauvegarder
            setChanged();
            
            // Synchroniser avec le client
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
            
            // Log pour debug
            AdvancedMana.LOGGER.info("ManaDetector at {} read: mana={}, manaCap={}", getBlockPos(), mana, manaCap);
        }
    }
    
    // Getters publics
    public int getMana() {
        return mana;
    }
    
    public int getManaCap() {
        return manaCap;
    }
    
    public boolean hasValidMana() {
        return mana > 0 || manaCap > 0;
    }
    
    // Sauvegarde des données dans le NBT
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("mana", mana);
        tag.putInt("manaCap", manaCap);
        tag.putInt("TickCounter", tickCounter);
    }
    
    // Chargement des données depuis le NBT
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        mana = tag.getInt("mana");
        manaCap = tag.getInt("manaCap");
        tickCounter = tag.getInt("TickCounter");
    }
    
    // Synchronisation client-serveur
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
    
    // ComputerCraft integration will be handled via a separate peripheral class
}