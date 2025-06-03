package com.tomokisan.advancedmana;

import com.tomokisan.advancedmana.block.ManaDetectorBlock;
import com.tomokisan.advancedmana.block.entity.ManaDetectorBlockEntity;
import com.tomokisan.advancedmana.integration.computercraft.ManaDetectorPeripheralProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancedMana implements ModInitializer {
	public static final String MOD_ID = "advanced-mana";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Déclaration du bloc ManaDetector
	public static final Block MANA_DETECTOR = Registry.register(
		BuiltInRegistries.BLOCK,
		new ResourceLocation(MOD_ID, "mana_detector"),
		new ManaDetectorBlock()
	);

	// Déclaration de l'item du bloc
	public static final BlockItem MANA_DETECTOR_ITEM = Registry.register(
		BuiltInRegistries.ITEM,
		new ResourceLocation(MOD_ID, "mana_detector"),
		new BlockItem(MANA_DETECTOR, new FabricItemSettings())
	);

	// Déclaration du BlockEntity
	public static final BlockEntityType<ManaDetectorBlockEntity> MANA_DETECTOR_BLOCK_ENTITY = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		new ResourceLocation(MOD_ID, "mana_detector_block_entity"),
		FabricBlockEntityTypeBuilder.create(ManaDetectorBlockEntity::new, MANA_DETECTOR).build()
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		LOGGER.info("Advanced Mana mod loaded with ManaDetector block and BlockEntity!");

		// Ajouter le bloc à l'onglet créatif des blocs fonctionnels
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
			content.accept(MANA_DETECTOR_ITEM);
		});
		
		// Enregistrer le provider de périphérique CC:Tweaked (Fabric API)
		ManaDetectorPeripheralProvider.register();
		
		LOGGER.info("CC:Tweaked peripheral provider registered for ManaDetector!");
	}
}