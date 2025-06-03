package com.tomokisan.advancedmana.block;

import com.tomokisan.advancedmana.block.entity.ManaDetectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ManaDetectorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    
    // Forme légèrement plus petite que le bloc complet pour un effet visuel
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

    public ManaDetectorBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // Méthode pour détecter la mana - vous pouvez l'implémenter selon vos besoins
    public boolean detectsMana(BlockGetter world, BlockPos pos) {
        // Logique de détection de mana à implémenter
        return false;
    }

    // Création du BlockEntity
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ManaDetectorBlockEntity(pos, state);
    }

    // Gestion du ticker pour que le BlockEntity puisse fonctionner
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, com.tomokisan.advancedmana.AdvancedMana.MANA_DETECTOR_BLOCK_ENTITY, ManaDetectorBlockEntity::tick);
    }

    // Interaction avec le bloc (clic droit)
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        
        // Si le joueur tient un item ET fait sneak (Shift+clic), toujours afficher les infos
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof ManaDetectorBlockEntity manaDetector) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "Mana: " + manaDetector.getMana() + 
                        "/" + manaDetector.getManaCap() + 
                        " | Valid: " + manaDetector.hasValidMana()
                    ));
                }
            }
            return InteractionResult.SUCCESS;
        }
        
        // Si le joueur tient un item (sans Shift), permettre le placement
        if (!heldItem.isEmpty()) {
            return InteractionResult.PASS;
        }
        
        // Si mains vides, afficher les infos
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ManaDetectorBlockEntity manaDetector) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "Mana: " + manaDetector.getMana() + 
                    "/" + manaDetector.getManaCap() + 
                    " | Valid: " + manaDetector.hasValidMana()
                ));
            }
        }
        
        return InteractionResult.SUCCESS;
    }

    // Rendu normal (pas d'entité invisible)
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}