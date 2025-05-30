package com.tomokisan.advancedmana;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import com.tomokisan.advancedmana.block.ManaDetectorBlock;
import com.tomokisan.advancedmana.block.entity.ManaDetectorBlockEntity;

import net.minecraft.block.entity.BlockEntityType;

public class AdvancedMana implements ModInitializer {
    public static final String MOD_ID = "advancedmana";
    
    // Blocs
    public static final Block MANA_DETECTOR_BLOCK = new ManaDetectorBlock(
        FabricBlockSettings.create().strength(2.0f).requiresTool()
    );
    
    // Items
    public static final Item MANA_DETECTOR_ITEM = new BlockItem(
        MANA_DETECTOR_BLOCK,
        new FabricItemSettings()
    );
    
    // Block Entities
    public static final BlockEntityType<ManaDetectorBlockEntity> MANA_DETECTOR_BLOCK_ENTITY =
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MOD_ID, "mana_detector"),
            BlockEntityType.Builder.create(
                ManaDetectorBlockEntity::new,
                MANA_DETECTOR_BLOCK
            ).build(null)
        );
    
    @Override
    public void onInitialize() {
        // Enregistrer le bloc
        Registry.register(
            Registries.BLOCK,
            new Identifier(MOD_ID, "mana_detector"),
            MANA_DETECTOR_BLOCK
        );
        
        // Enregistrer l'item du bloc
        Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "mana_detector"),
            MANA_DETECTOR_ITEM
        );
        
        System.out.println("Advanced Mana mod initialized!");
    }
}