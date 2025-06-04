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
    
    // Variables pour l'animation
    private boolean hasManaDetected = false;
    private long animationTime = 0;
    private boolean wasDetectingMana = false;
    
    public ManaDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(AdvancedMana.MANA_DETECTOR_BLOCK_ENTITY, pos, blockState);
    }
    
    // Méthode appelée à chaque tick du serveur
    public static void tick(Level level, BlockPos pos, BlockState state, ManaDetectorBlockEntity blockEntity) {
        if (level.isClientSide()) {
            // Côté client : gérer l'animation
            blockEntity.animationTime++;
            return;
        }
        
        // Côté serveur
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
            
            // Déterminer si on a du mana détecté
            boolean nowDetectingMana = this.mana > 0 || this.manaCap > 0;
            
            if (nowDetectingMana != this.wasDetectingMana) {
                this.hasManaDetected = nowDetectingMana;
                this.wasDetectingMana = nowDetectingMana;
                
                // Marquer comme modifié pour sauvegarder
                setChanged();
                
                // Synchroniser avec le client pour l'animation
                if (level != null) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
                
                // Log pour debug
                AdvancedMana.LOGGER.info("ManaDetector at {} detection state changed: {}", getBlockPos(), nowDetectingMana);
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
    
    // Getters pour l'animation
    public boolean hasManaDetected() {
        return hasManaDetected;
    }
    
    public float getGlowIntensity() {
        if (!hasManaDetected) return 0.0f;
        
        // Animation sinusoïdale pour l'effet de pulsation
        // Période d'environ 60 ticks (3 secondes)
        float phase = (animationTime % 60) / 60.0f;
        return 0.3f + 0.7f * (float)Math.sin(phase * Math.PI * 2);
    }
    
    // Sauvegarde des données dans le NBT
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("mana", mana);
        tag.putInt("manaCap", manaCap);
        tag.putInt("TickCounter", tickCounter);
        tag.putBoolean("HasManaDetected", hasManaDetected);
        tag.putLong("AnimationTime", animationTime);
    }
    
    // Chargement des données depuis le NBT
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        mana = tag.getInt("mana");
        manaCap = tag.getInt("manaCap");
        tickCounter = tag.getInt("TickCounter");
        hasManaDetected = tag.getBoolean("HasManaDetected");
        animationTime = tag.getLong("AnimationTime");
        wasDetectingMana = hasManaDetected;
    }
    
    // Synchronisation client-serveur
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("mana", mana);
        tag.putInt("manaCap", manaCap);
        tag.putBoolean("HasManaDetected", hasManaDetected);
        return tag;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    // ComputerCraft integration will be handled via a separate peripheral class
}