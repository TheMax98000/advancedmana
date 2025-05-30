package com.tomokisan.advancedmana.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Method;

public class ManaHelper {
    
    /**
     * Obtient la quantité de mana d'un bloc donné
     */
    public static int getManaFromBlock(BlockState state, BlockEntity entity, World world, BlockPos pos) {
        if (entity == null) return 0;
        
        // Botania - Mana Pool
        if (isBotaniaManaPool(entity)) {
            return getBotaniaMana(entity);
        }
        
        // Autres mods de mana peuvent être ajoutés ici
        // Par exemple : Ars Nouveau, Mahou Tsukai, etc.
        
        return 0;
    }
    
    /**
     * Obtient la capacité maximale de mana d'un bloc donné
     */
    public static int getMaxManaFromBlock(BlockState state, BlockEntity entity, World world, BlockPos pos) {
        if (entity == null) return 0;
        
        // Botania - Mana Pool
        if (isBotaniaManaPool(entity)) {
            return getBotaniaMaxMana(entity);
        }
        
        // Autres mods de mana peuvent être ajoutés ici
        
        return 0;
    }
    
    /**
     * Vérifie si l'entité est un Mana Pool de Botania
     */
    private static boolean isBotaniaManaPool(BlockEntity entity) {
        try {
            Class<?> manaPoolClass = Class.forName("vazkii.botania.common.block.mana.ManaPoolBlockEntity");
            return manaPoolClass.isInstance(entity);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Obtient la mana d'un Mana Pool de Botania via réflexion
     */
    private static int getBotaniaMana(BlockEntity entity) {
        try {
            Method getCurrentManaMethod = entity.getClass().getMethod("getCurrentMana");
            Object result = getCurrentManaMethod.invoke(entity);
            return (Integer) result;
        } catch (Exception e) {
            // Essayer avec d'autres noms de méthodes possibles
            try {
                Method getManaMethod = entity.getClass().getMethod("getMana");
                Object result = getManaMethod.invoke(entity);
                return (Integer) result;
            } catch (Exception e2) {
                // Essayer d'accéder au champ directement
                try {
                    var field = entity.getClass().getDeclaredField("mana");
                    field.setAccessible(true);
                    Object result = field.get(entity);
                    return (Integer) result;
                } catch (Exception e3) {
                    System.err.println("Could not get mana from Botania mana pool: " + e3.getMessage());
                    return 0;
                }
            }
        }
    }
    
    /**
     * Obtient la capacité maximale d'un Mana Pool de Botania via réflexion
     */
    private static int getBotaniaMaxMana(BlockEntity entity) {
        try {
            Method getMaxManaMethod = entity.getClass().getMethod("getMaxMana");
            Object result = getMaxManaMethod.invoke(entity);
            return (Integer) result;
        } catch (Exception e) {
            // Essayer avec d'autres noms possibles
            try {
                Method getCapacityMethod = entity.getClass().getMethod("getCapacity");
                Object result = getCapacityMethod.invoke(entity);
                return (Integer) result;
            } catch (Exception e2) {
                // Valeur par défaut pour les mana pools de Botania
                // Un mana pool normal a 1 000 000 de mana
                // Un diluted mana pool a 10 000 de mana
                // Un fabulous mana pool a 1 000 000 de mana aussi
                
                String className = entity.getClass().getSimpleName().toLowerCase();
                if (className.contains("diluted")) {
                    return 10000;
                } else {
                    return 1000000; // Valeur par défaut
                }
            }
        }
    }
}