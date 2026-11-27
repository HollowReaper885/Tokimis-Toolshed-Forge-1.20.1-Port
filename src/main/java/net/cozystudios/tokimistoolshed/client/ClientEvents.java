package net.cozystudios.tokimistoolshed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cozystudios.tokimistoolshed.TokimisToolshed;
import net.cozystudios.tokimistoolshed.item.AbacusItem;
import net.cozystudios.tokimistoolshed.item.ClippersItem;
import net.cozystudios.tokimistoolshed.item.ExcavatorItem;
import net.cozystudios.tokimistoolshed.item.HammerItem;
import net.cozystudios.tokimistoolshed.item.ScytheItem;
import net.cozystudios.tokimistoolshed.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** World-space outlines (3x3 preview + abacus box). */
@Mod.EventBusSubscriber(modid = TokimisToolshed.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    private static final int MAX_AXIS_LENGTH = 128;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        renderAOEOutline(mc, poseStack, buffers, camPos);
        renderAbacusOutline(mc, poseStack, buffers, camPos);

        buffers.endBatch(RenderType.lines());
    }

    private static void renderAOEOutline(Minecraft mc, PoseStack poseStack, MultiBufferSource buffers, Vec3 camPos) {
        Item held = mc.player.getMainHandItem().getItem();
        boolean isAreaTool = held instanceof ExcavatorItem || held instanceof HammerItem || held instanceof ClippersItem;
        boolean isScythe = held instanceof ScytheItem;
        if (!isAreaTool && !isScythe) return;

        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) return;
        if (mc.player.isShiftKeyDown()) return;

        BlockHitResult hit = (BlockHitResult) mc.hitResult;
        BlockPos center = hit.getBlockPos();
        Direction face = isScythe ? Direction.UP : hit.getDirection();
        Level level = mc.level;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                BlockPos targetPos = switch (face) {
                    case UP, DOWN -> center.offset(dx, 0, dy);
                    case NORTH, SOUTH -> center.offset(dx, dy, 0);
                    case EAST, WEST -> center.offset(0, dy, dx);
                };

                VoxelShape shape = level.getBlockState(targetPos).getShape(level, targetPos);
                if (shape.isEmpty()) continue;

                OutlineRenderer.drawShape(poseStack, buffers.getBuffer(RenderType.lines()), shape,
                        targetPos.getX() - camPos.x,
                        targetPos.getY() - camPos.y,
                        targetPos.getZ() - camPos.z,
                        0.0f, 0.0f, 0.0f, 0.4f);
            }
        }
    }

    private static void renderAbacusOutline(Minecraft mc, PoseStack poseStack, MultiBufferSource buffers, Vec3 camPos) {
        ItemStack abacus = getHeldAbacus(mc);
        if (abacus == null || !AbacusItem.isBound(abacus)) return;
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) return;

        BlockPos targetPos = ((BlockHitResult) mc.hitResult).getBlockPos();
        BlockPos boundPos = AbacusItem.getBoundPos(abacus);
        if (boundPos == null) return;
        if (AbacusItem.getDistance(abacus, targetPos) < 0) return;

        int clampedX = Math.max(boundPos.getX() - MAX_AXIS_LENGTH + 1, Math.min(targetPos.getX(), boundPos.getX() + MAX_AXIS_LENGTH - 1));
        int clampedY = Math.max(boundPos.getY() - MAX_AXIS_LENGTH + 1, Math.min(targetPos.getY(), boundPos.getY() + MAX_AXIS_LENGTH - 1));
        int clampedZ = Math.max(boundPos.getZ() - MAX_AXIS_LENGTH + 1, Math.min(targetPos.getZ(), boundPos.getZ() + MAX_AXIS_LENGTH - 1));

        int minX = Math.min(boundPos.getX(), clampedX);
        int maxX = Math.max(boundPos.getX(), clampedX);
        int minY = Math.min(boundPos.getY(), clampedY);
        int maxY = Math.max(boundPos.getY(), clampedY);
        int minZ = Math.min(boundPos.getZ(), clampedZ);
        int maxZ = Math.max(boundPos.getZ(), clampedZ);

        VoxelShape box = Shapes.box(0, 0, 0, maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);

        OutlineRenderer.drawShape(poseStack, buffers.getBuffer(RenderType.lines()), box,
                minX - camPos.x, minY - camPos.y, minZ - camPos.z,
                1.0f, 0.65f, 0.0f, 1.0f);
    }

    static ItemStack getHeldAbacus(Minecraft mc) {
        ItemStack mainHand = mc.player.getMainHandItem();
        if (mainHand.is(ModItems.ABACUS.get())) return mainHand;
        ItemStack offHand = mc.player.getOffhandItem();
        if (offHand.is(ModItems.ABACUS.get())) return offHand;
        return null;
    }
}
