package net.cozystudios.tokimistoolshed.client;

import net.cozystudios.tokimistoolshed.TokimisToolshed;
import net.cozystudios.tokimistoolshed.item.AbacusItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Shows the measured X/Y/Z size next to the crosshair while holding a bound abacus. */
@Mod.EventBusSubscriber(modid = TokimisToolshed.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AbacusHudOverlay {
    private static final int MAX_AXIS_LENGTH = 128;

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("abacus", (gui, guiGraphics, partialTick, screenWidth, screenHeight) ->
                render(guiGraphics, screenWidth, screenHeight));
    }

    private static void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (mc.options.hideGui) return;

        ItemStack abacusStack = ClientEvents.getHeldAbacus(mc);
        if (abacusStack == null || !AbacusItem.isBound(abacusStack)) return;

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos targetPos = ((BlockHitResult) hit).getBlockPos();
        BlockPos boundPos = AbacusItem.getBoundPos(abacusStack);
        if (boundPos == null) return;
        if (AbacusItem.getDistance(abacusStack, targetPos) < 0) return;

        int dx = Math.min(Math.abs(targetPos.getX() - boundPos.getX()), MAX_AXIS_LENGTH - 1);
        int dy = Math.min(Math.abs(targetPos.getY() - boundPos.getY()), MAX_AXIS_LENGTH - 1);
        int dz = Math.min(Math.abs(targetPos.getZ() - boundPos.getZ()), MAX_AXIS_LENGTH - 1);

        int iconX = (screenWidth / 2) + 10;
        int iconY = (screenHeight / 2) - 7;

        guiGraphics.renderItem(abacusStack, iconX, iconY);

        Font font = mc.font;

        if (dx > 0) {
            String xText = String.valueOf(dx + 1);
            guiGraphics.drawString(font, xText, iconX + 8 - font.width(xText) / 2, iconY - 10, 0xFFFFFFFF);
        }
        if (dz > 0) {
            String zText = String.valueOf(dz + 1);
            guiGraphics.drawString(font, zText, iconX + 18, iconY + 4, 0xFFFFFFFF);
        }
        if (dy > 0) {
            String yText = String.valueOf(dy + 1);
            guiGraphics.drawString(font, yText, iconX + 8 - font.width(yText) / 2, iconY + 18, 0xFFFFFFFF);
        }
    }
}
