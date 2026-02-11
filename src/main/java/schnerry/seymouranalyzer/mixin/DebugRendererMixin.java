package schnerry.seymouranalyzer.mixin;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import schnerry.seymouranalyzer.render.BlockHighlighter;

/**
 * DebugRendererMixin for 1.21.10
 *
 * WorldRenderEvents was removed in 1.21.9, so we use a direct mixin.
 * Injects into DebugRenderer.render to draw block highlights alongside debug rendering.
 */
@Mixin(net.minecraft.client.render.debug.DebugRenderer.class)
public class DebugRendererMixin {

    /**
     * Inject at the TAIL of DebugRenderer.render to draw our highlights
     * after vanilla debug rendering but in the same render pass.
     */
    @Inject(
        method = "render",
        at = @At("TAIL")
    )
    private void onDebugRender(
            MatrixStack matrices,
            Frustum frustum,
            VertexConsumerProvider.Immediate vertexConsumers,
            double cameraX,
            double cameraY,
            double cameraZ,
            boolean lateRender,
            CallbackInfo ci
    ) {
        // Only render during the late render pass (after translucent blocks)
        if (!lateRender) return;

        BlockHighlighter highlighter = BlockHighlighter.getInstance();
        if (!highlighter.hasHighlights()) return;

        Vec3d cameraPos = new Vec3d(cameraX, cameraY, cameraZ);
        highlighter.renderHighlights(matrices, vertexConsumers, cameraPos);
    }
}



