package com.tomokisan.advancedmana.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tomokisan.advancedmana.AdvancedMana;
import com.tomokisan.advancedmana.block.entity.ManaDetectorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class ManaDetectorBlockEntityRenderer implements BlockEntityRenderer<ManaDetectorBlockEntity> {
    
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(AdvancedMana.MOD_ID, "textures/block/mana_detector_glow.png");
    
    public ManaDetectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        // Constructeur requis
    }
    
    @Override
    public void render(ManaDetectorBlockEntity blockEntity, float partialTick, PoseStack poseStack, 
                      MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        
        // Ne rendre l'effet que si du mana est détecté
        if (!blockEntity.hasManaDetected()) {
            return;
        }
        
        // Obtenir l'intensité du glow (animée)
        float glowIntensity = blockEntity.getGlowIntensity();
        if (glowIntensity <= 0.0f) {
            return;
        }
        
        // Configuration du rendu
        BlockState state = blockEntity.getBlockState();
        Direction facing = state.getValue(com.tomokisan.advancedmana.block.ManaDetectorBlock.FACING);
        
        // Buffer pour le rendu avec transparence et émission
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(GLOW_TEXTURE));
        
        poseStack.pushPose();
        
        // Rendre l'effet de glow sur les faces "side" (pas la face top/facing)
        for (Direction direction : Direction.values()) {
            if (direction == facing || direction == Direction.UP || direction == Direction.DOWN) {
                continue; // Skip la face qui regarde vers le bloc à analyser et les faces haut/bas
            }
            
            renderGlowOnFace(poseStack, vertexConsumer, direction, glowIntensity, packedOverlay);
        }
        
        poseStack.popPose();
    }
    
    private void renderGlowOnFace(PoseStack poseStack, VertexConsumer vertexConsumer, 
                                  Direction face, float intensity, int packedOverlay) {
        poseStack.pushPose();
        
        // Positionner selon la face
        switch (face) {
            case NORTH:
                poseStack.translate(0, 0, -0.001f); // Légèrement devant la face nord
                break;
            case SOUTH:
                poseStack.translate(0, 0, 1.001f); // Légèrement devant la face sud
                break;
            case WEST:
                poseStack.translate(-0.001f, 0, 0); // Légèrement devant la face ouest
                break;
            case EAST:
                poseStack.translate(1.001f, 0, 0); // Légèrement devant la face est
                break;
        }
        
        Matrix4f matrix = poseStack.last().pose();
        
        // Couleur bleue avec alpha basé sur l'intensité
        float red = 0.2f;
        float green = 0.6f;
        float blue = 1.0f;
        float alpha = intensity * 0.8f;
        
        // Coordonnées de la zone centrale (où sera l'effet de glow)
        float minX = 0.25f, maxX = 0.75f;
        float minY = 0.25f, maxY = 0.75f;
        float minZ = 0.25f, maxZ = 0.75f;
        
        // Light level élevé pour l'effet émissif
        int lightLevel = 240; // Maximum de luminosité
        
        // Dessiner le quad avec effet de glow
        switch (face) {
            case NORTH:
                // Face Nord (Z = 0)
                vertexConsumer.vertex(matrix, minX, minY, 0).color(red, green, blue, alpha)
                    .uv(0, 0).overlayCoords(packedOverlay).uv2(lightLevel).normal(0, 0, -1).endVertex();
                vertexConsumer.vertex(matrix, minX, maxY, 0).color(red, green, blue, alpha)
                    .uv(0, 1).overlayCoords(packedOverlay).uv2(lightLevel).normal(0, 0, -1).endVertex();
                vertexConsumer.vertex(matrix, maxX, maxY, 0).color(red, green, blue, alpha)
                    .uv(1, 1).overlayCoords(packedOverlay).uv2(lightLevel).normal(0, 0, -1).endVertex();
                vertexConsumer.vertex(matrix, maxX, minY, 0).color(red, green, blue, alpha)
                    .uv(1, 0).overlayCoords(packedOverlay).uv2(lightLevel).normal(0, 0, -1).endVertex();
                break;
            case SOUTH:
                // Face Sud (Z = 1)
                vertexConsumer.vertex(matrix, maxX, minY, 1).color(red, green, blue, alpha)
                    .uv(0, 0).overlayCoords(packedOverlay).uv2(lightLevel).normal(0, 0, 1).endVertex();
                vertexConsumer.vertex(matrix, maxX, maxY, 1).color(red, green, blue, alpha)
                    .uv(0, 1).overlayCoords(packedOverlay).uv2(lightLevel).normal(0, 0, 1).endVertex();
                vertexConsumer.vertex(matrix, minX, maxY, 1).color(red, green, blue, alpha)
                    .uv(1, 1).overlayCoords(packedOverlay).uv2(lightLevel).normal(0, 0, 1).endVertex();
                vertexConsumer.vertex(matrix, minX, minY, 1).color(red, green, blue, alpha)
                    .uv(1, 0).overlayCoords(packedOverlay).uv2(lightLevel).normal(0, 0, 1).endVertex();
                break;
            case WEST:
                // Face Ouest (X = 0)
                vertexConsumer.vertex(matrix, 0, minY, maxZ).color(red, green, blue, alpha)
                    .uv(0, 0).overlayCoords(packedOverlay).uv2(lightLevel).normal(-1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, 0, maxY, maxZ).color(red, green, blue, alpha)
                    .uv(0, 1).overlayCoords(packedOverlay).uv2(lightLevel).normal(-1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, 0, maxY, minZ).color(red, green, blue, alpha)
                    .uv(1, 1).overlayCoords(packedOverlay).uv2(lightLevel).normal(-1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, 0, minY, minZ).color(red, green, blue, alpha)
                    .uv(1, 0).overlayCoords(packedOverlay).uv2(lightLevel).normal(-1, 0, 0).endVertex();
                break;
            case EAST:
                // Face Est (X = 1)
                vertexConsumer.vertex(matrix, 1, minY, minZ).color(red, green, blue, alpha)
                    .uv(0, 0).overlayCoords(packedOverlay).uv2(lightLevel).normal(1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, 1, maxY, minZ).color(red, green, blue, alpha)
                    .uv(0, 1).overlayCoords(packedOverlay).uv2(lightLevel).normal(1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, 1, maxY, maxZ).color(red, green, blue, alpha)
                    .uv(1, 1).overlayCoords(packedOverlay).uv2(lightLevel).normal(1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, 1, minY, maxZ).color(red, green, blue, alpha)
                    .uv(1, 0).overlayCoords(packedOverlay).uv2(lightLevel).normal(1, 0, 0).endVertex();
                break;
        }
        
        poseStack.popPose();
    }
}