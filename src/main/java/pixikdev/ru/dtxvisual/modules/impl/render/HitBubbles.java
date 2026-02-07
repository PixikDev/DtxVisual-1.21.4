package pixikdev.ru.dtxvisual.modules.impl.render;

import pixikdev.ru.dtxvisual.DtxVisual;
import pixikdev.ru.dtxvisual.client.events.impl.EventAttackEntity;
import pixikdev.ru.dtxvisual.client.events.impl.EventRender3D;
import pixikdev.ru.dtxvisual.client.managers.ThemeManager;
import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.modules.settings.impl.NumberSetting;
import pixikdev.ru.dtxvisual.client.util.animations.Animation;
import pixikdev.ru.dtxvisual.client.util.animations.Easing;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.gl.ShaderProgramKeys;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import java.util.Optional;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HitBubbles extends Module implements ThemeManager.ThemeChangeListener {

    
    private final NumberSetting size = new NumberSetting("setting.size", 0.5f, 0.2f, 2.0f, 0.05f);
    private final NumberSetting lifeTime = new NumberSetting("setting.lifeTime", 0.8f, 0.2f, 2.0f, 0.05f);

    private static final long GROW_MS = 400L; 

    private final Identifier bubbleTex = DtxVisual.id("textures/bubble.png"); 

    private final List<HitCircle> hitCircles = new CopyOnWriteArrayList<>();
    private final ThemeManager themeManager;
    private Color currentColor;

    public HitBubbles() {
        super("HitBubbles", Category.Render, "module.hitbubbles.description");
        this.themeManager = ThemeManager.getInstance();
        this.currentColor = themeManager.getThemeColor();
        themeManager.addThemeChangeListener(this);
    }

    @Override
    public void onThemeChanged(ThemeManager.Theme theme) {
        this.currentColor = theme.getBackgroundColor();
    }

    @Override
    public void onDisable() {
        themeManager.removeThemeChangeListener(this);
        hitCircles.clear();
        super.onDisable();
    }

    @EventHandler
    private void onAttackEntity(EventAttackEntity e) {
        if (fullNullCheck()) return;
        if (e.getTarget() == mc.player) return;
        if (!(e.getTarget() instanceof LivingEntity entity)) return;
        if (!entity.isAlive()) return;
        if (!e.isEffectsAllowed()) return;
        
        
        if (!e.canProcess()) return;

        
        Vec3d hitPos;
        HitResult ch = mc.crosshairTarget;
        if (ch != null && ch.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ehr = (EntityHitResult) ch;
            if (ehr.getEntity() == e.getTarget()) {
                hitPos = ehr.getPos();
            } else {
                hitPos = computeHitOnEntityAABB(entity);
            }
        } else {
            hitPos = computeHitOnEntityAABB(entity);
        }
        if (hitPos == null) {
            hitPos = entity.getPos().add(0, entity.getHeight() / 2f, 0);
        }

        
        Vec3d cameraPos = mc.getEntityRenderDispatcher().camera.getPos();
        Vec3d toCamera = cameraPos.subtract(hitPos).normalize();

        
        float yaw = (float) Math.toDegrees(Math.atan2(toCamera.z, toCamera.x));

        
        float pitch = (float) Math.toDegrees(Math.atan2(-toCamera.y, Math.sqrt(toCamera.x * toCamera.x + toCamera.z * toCamera.z)));

        hitCircles.add(new HitCircle(hitPos, (long) (lifeTime.getValue() * 1000), yaw, pitch));
    }

    @EventHandler
    public void onRender3D(EventRender3D.Game e) {
        if (fullNullCheck()) return;

        long now = System.currentTimeMillis();
        
        hitCircles.removeIf(c -> (now - c.spawnTime) > c.ttl);

        for (HitCircle c : hitCircles) {
            long age = now - c.spawnTime;

            c.grow.update(true);
            float growVal = c.grow.getValue();

            
            float timeFade = (float) Math.pow(1.0 - (age / (float) c.ttl), 0.8);
            float alphaK = Math.max(0f, Math.min(1f, growVal * timeFade));
            int alpha = (int) (255 * alphaK);
            if (alpha <= 2) continue;

            float r = size.getValue() * (0.6f + 0.4f * growVal); 
            // Live theme color each frame for gradient themes
            Color themeColor = themeManager.getCurrentTheme().getBackgroundColor();
            Color drawColor = new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), alpha);

            
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            RenderSystem.setShaderTexture(0, bubbleTex);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                    GL11.GL_ONE, GL11.GL_ZERO
            );
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.depthMask(false);
            
            
            Vec3d cameraPos = mc.getEntityRenderDispatcher().camera.getPos();
            
            
            
            
            
            double yawRadians = Math.toRadians(c.rotationYaw);
            double pitchRadians = Math.toRadians(c.rotationPitch);
            
            double cosYaw = Math.cos(yawRadians);
            double sinYaw = Math.sin(yawRadians);
            double cosPitch = Math.cos(pitchRadians);
            double sinPitch = Math.sin(pitchRadians);
            
            
            
            Vec3d forward = new Vec3d(
                cosYaw * cosPitch,
                -sinPitch,
                sinYaw * cosPitch
            ).normalize();
            
            
            Vec3d worldUp = new Vec3d(0, 1, 0);
            Vec3d right = worldUp.crossProduct(forward).normalize();
            
            
            Vec3d up = forward.crossProduct(right).normalize();
            
            
            Vec3d scaledRight = right.multiply(r);
            Vec3d scaledUp = up.multiply(r);
            
            
            Vec3d topLeft = c.origin.add(scaledUp).subtract(scaledRight);
            Vec3d topRight = c.origin.add(scaledUp).add(scaledRight);
            Vec3d bottomLeft = c.origin.subtract(scaledUp).subtract(scaledRight);
            Vec3d bottomRight = c.origin.subtract(scaledUp).add(scaledRight);
            
            
            topLeft = topLeft.subtract(cameraPos);
            topRight = topRight.subtract(cameraPos);
            bottomLeft = bottomLeft.subtract(cameraPos);
            bottomRight = bottomRight.subtract(cameraPos);
            
            
            Matrix4f matrix = e.getMatrices().peek().getPositionMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            
            float red = drawColor.getRed() / 255.0f;
            float green = drawColor.getGreen() / 255.0f;
            float blue = drawColor.getBlue() / 255.0f;
            float alphaValue = drawColor.getAlpha() / 255.0f;
            
            
            buffer.vertex(matrix, (float) bottomLeft.x, (float) bottomLeft.y, (float) bottomLeft.z)
                    .texture(0.0f, 1.0f).color(red, green, blue, alphaValue);
            buffer.vertex(matrix, (float) bottomRight.x, (float) bottomRight.y, (float) bottomRight.z)
                    .texture(1.0f, 1.0f).color(red, green, blue, alphaValue);
            buffer.vertex(matrix, (float) topRight.x, (float) topRight.y, (float) topRight.z)
                    .texture(1.0f, 0.0f).color(red, green, blue, alphaValue);
            buffer.vertex(matrix, (float) topLeft.x, (float) topLeft.y, (float) topLeft.z)
                    .texture(0.0f, 0.0f).color(red, green, blue, alphaValue);
            
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            
            
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }
    }

    private Vec3d computeHitOnEntityAABB(LivingEntity entity) {
        Vec3d start = mc.player.getEyePos();
        Vec3d dir = mc.player.getRotationVec(1.0f);
        Vec3d end = start.add(dir.multiply(6.0));
        Box bb = entity.getBoundingBox();
        Optional<Vec3d> res = bb.raycast(start, end);
        return res.orElse(null);
    }

    private static class HitCircle {
        final Vec3d origin; 
        final long spawnTime; 
        final long ttl; 
        final Animation grow; 
        final float rotationYaw; 
        final float rotationPitch; 

        HitCircle(Vec3d origin, long ttl, float rotationYaw, float rotationPitch) {
            this.origin = origin;
            this.spawnTime = System.currentTimeMillis();
            this.ttl = ttl;
            this.grow = new Animation((int) GROW_MS, 1f, true, Easing.BOTH_SINE);
            this.rotationYaw = rotationYaw;
            this.rotationPitch = rotationPitch;
        }
    }
}
