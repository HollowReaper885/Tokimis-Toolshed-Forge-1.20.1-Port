package net.cozystudios.tokimistoolshed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Small helper that draws a VoxelShape as lines (same look as vanilla's block outline). */
public final class OutlineRenderer {
    private OutlineRenderer() {}

    public static void drawShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape,
                                 double offX, double offY, double offZ,
                                 float r, float g, float b, float a) {
        PoseStack.Pose pose = poseStack.last();
        shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            float nx = (float) (x2 - x1);
            float ny = (float) (y2 - y1);
            float nz = (float) (z2 - z1);
            float len = Mth.sqrt(nx * nx + ny * ny + nz * nz);
            if (len == 0.0F) len = 1.0F;
            nx /= len;
            ny /= len;
            nz /= len;
            consumer.vertex(pose.pose(), (float) (x1 + offX), (float) (y1 + offY), (float) (z1 + offZ))
                    .color(r, g, b, a)
                    .normal(pose.normal(), nx, ny, nz)
                    .endVertex();
            consumer.vertex(pose.pose(), (float) (x2 + offX), (float) (y2 + offY), (float) (z2 + offZ))
                    .color(r, g, b, a)
                    .normal(pose.normal(), nx, ny, nz)
                    .endVertex();
        });
    }
}
