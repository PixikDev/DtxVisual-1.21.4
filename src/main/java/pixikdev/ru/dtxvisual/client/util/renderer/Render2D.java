package pixikdev.ru.dtxvisual.client.util.renderer;
import pixikdev.ru.dtxvisual.client.render.builders.Builder;
import pixikdev.ru.dtxvisual.client.render.builders.states.QuadColorState;
import pixikdev.ru.dtxvisual.client.render.builders.states.QuadRadiusState;
import pixikdev.ru.dtxvisual.client.render.builders.states.SizeState;
import pixikdev.ru.dtxvisual.client.render.renderers.impl.*;
import pixikdev.ru.dtxvisual.client.util.Wrapper;

import pixikdev.ru.dtxvisual.client.util.renderer.fonts.Instance;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.Tessellator;


@UtilityClass
public class   Render2D implements Wrapper {

    public void drawRoundedRect(MatrixStack stack, float x, float y, float width, float height, float radius, Color color) {
        BuiltRectangle built = Builder.rectangle()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius))
                .color(new QuadColorState(color))
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }

    public void drawRockstarRoundedRect(MatrixStack stack, float x, float y, float width, float height, float radius, Color color) {
        RockstarBuiltRectangle built = Builder.rockstarRectangle()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius))
                .color(new QuadColorState(color))
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }

    public void drawBlurredRect(MatrixStack stack, float x, float y, float width, float height, float radius, float blurRadius, Color color) {
        
        
        int layers = 3;
        float baseAlpha = color.getAlpha();
        for (int i = 0; i < layers; i++) {
            float t = (i + 1) / (float) layers;
            float falloff = (1f - t);
            int a = Math.max(0, Math.min(255, (int) (baseAlpha * (0.6f * falloff + 0.2f))));
            float spread = Math.max(1f, Math.min(12f, blurRadius * 0.5f)) * (i + 1) * 0.5f;
            float rad = radius + i * 0.6f;
            drawRoundedRect(
                    stack,
                    x - spread,
                    y - spread,
                    width + spread * 2f,
                    height + spread * 2f,
                    rad,
                    new Color(color.getRed(), color.getGreen(), color.getBlue(), a)
            );
        }
    }

    public void drawRockstarBlurredRect(MatrixStack stack, float x, float y, float width, float height, float radius, float blurRadius, Color color) {
        
        
//        int layers = 3;
//        float baseAlpha = color.getAlpha();
//        for (int i = 0; i < layers; i++) {
//            float t = (i + 1) / (float) layers;
//            float falloff = (1f - t);
//            int a = Math.max(0, Math.min(255, (int) (baseAlpha * (0.6f * falloff + 0.2f))));
//            float spread = Math.max(1f, Math.min(12f, blurRadius * 0.5f)) * (i + 1) * 0.5f;
//            float rad = radius + i * 0.6f;
        RockstarBuiltBlur built = Builder.rockstarBlur()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius))
                .color(new QuadColorState(color))
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
        drawRockstarRoundedRect(
                stack,
                x,
                y,
                width,
                height,
                radius,
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 60)
        );
//            drawRockstarRoundedRect(
//                    stack,
//                    x - spread,
//                    y - spread,
//                    width + spread * 2f,
//                    height + spread * 2f,
//                    rad,
//                    new Color(color.getRed(), color.getGreen(), color.getBlue(), a)
//            );
//        }
    }

    
    
    public void drawShaderBlurRect(MatrixStack stack, float x, float y, float width, float height, float radius, float blurRadius, Color color) {
        BuiltBlur built = Builder.blur()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius))
                .blurRadius(blurRadius)
                .color(new QuadColorState(color))
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }

    public void drawBorder(MatrixStack stack, float x, float y, float width, float height, float radius, float internalSmoothness, float externalSmoothness, Color color) {
        BuiltBorder built = Builder.border()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius))
                .smoothness(internalSmoothness, externalSmoothness)
                .color(new QuadColorState(color))
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }
    public void drawRoundedRect2(MatrixStack stack, float x, float y, float width, float height, float radius1, float radius2, float radius3, float radius4, Color color) {
        BuiltRectangle built = Builder.rectangle()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius1, radius2, radius3, radius4))
                .color(new QuadColorState(color))
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }

    public void drawStyledRect(MatrixStack stack, float x, float y, float width, float height, float radius, Color color, int blurAlpha) {
        drawBlurredRect(stack, x, y, width, height, radius, 10f, new Color(255, 255, 255, blurAlpha));
        drawRoundedRect(stack, x, y, width, height, radius, color);
    }
    
    public void drawRect(MatrixStack stack, float x, float y, float width, float height, Color color) {
        BuiltRectangle built = Builder.rectangle()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(0)) 
                .color(new QuadColorState(color))
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }

    
    public void drawGradientRect(MatrixStack stack, float x, float y, float width, float height, Color start, Color end, boolean horizontal) {
        QuadColorState quad;
        if (horizontal) {
            
            quad = new QuadColorState(start, end, end, start);
        } else {
            
            quad = new QuadColorState(start, start, end, end);
        }

        BuiltRectangle built = Builder.rectangle()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(0))
                .color(quad)
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }



    public void drawFont(MatrixStack stack, Instance instance, String text, float x, float y, Color color) {
        BuiltText built = Builder.text()
                .size(instance.size())
                .font(instance.font())
                .text(text)
                .thickness(0.05f)
                .color(color)
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }

    public void drawTexture(MatrixStack stack, float x, float y, float width, float height, float radius, Identifier texture, Color color) {
        drawTexture(stack, x, y, width, height, radius, mc.getTextureManager().getTexture(texture), color);
    }

    public void drawTexture(MatrixStack stack, float x, float y, float width, float height, float radius, AbstractTexture texture, Color color) {
        BuiltTexture built = Builder.texture()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius))
                .texture(1f, 1f, 1f, 1f, texture)
                .color(new QuadColorState(color))
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }

    public void drawTexture(MatrixStack stack, float x, float y, float width, float height, float radius, float u, float v, float textWidth, float texHeight, Identifier texture, Color color) {
        BuiltTexture built = Builder.texture()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius))
                .texture(u, v, textWidth, texHeight, mc.getTextureManager().getTexture(texture))
                .color(new QuadColorState(color))
                .build();
        built.render(stack.peek().getPositionMatrix(), x, y);
    }
    
    public void startScissor(DrawContext context, float x, float y, float width, float height) {
    	context.enableScissor((int) x, (int) y, (int) (x + width), (int) (y + height));
    }
    
    public void stopScissor(DrawContext context) {
    	context.disableScissor();
    }

    public AbstractTexture convert(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage img = new NativeImage(width, height, false);
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) img.setColorArgb(x, y, image.getRGB(x, y));

        return new NativeImageBackedTexture(img);
    }
    public void drawLine(MatrixStack stack,
                         float x1, float y1,
                         float x2, float y2,
                         float width,
                         Color color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length <= 0.5f) return; 

        float angle = (float) Math.atan2(dy, dx);

        BuiltRectangle built = Builder.rectangle()
                .size(new SizeState(length, width))
                .radius(new QuadRadiusState(0))
                .color(new QuadColorState(color))
                .build();

        stack.push();
        stack.translate(x1, y1, 0);
        stack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotation(angle));
        built.render(stack.peek().getPositionMatrix(), 0, -width / 2f);
        stack.pop();
    }

    public void drawGlow(MatrixStack stack, float x, float y, float width, float height, float radius, Color color, float intensity, int layers) {
        /*
         * stack     - матрица для рендера
         * x, y      - позиция
         * width     - ширина прямоугольника
         * height    - высота
         * radius    - радиус скругления
         * color     - цвет свечения
         * intensity - максимальная прозрачность верхнего слоя (0-255)
         * layers    - количество слоёв размытия
         */

        float step = intensity / layers; 
        float blurStep = radius / layers; 

        for (int i = 0; i < layers; i++) {
            float alpha = step * (i + 1);
            float layerRadius = radius + blurStep * i;
            float offset = i * 2; 

            drawBlurredRect(
                    stack,
                    x - offset,
                    y - offset,
                    width + offset * 2,
                    height + offset * 2,
                    layerRadius,
                    layerRadius,
                    new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) alpha)
            );
        }
    }

    public void drawTextGlow(MatrixStack stack, float x, float y, float width, float height, float radius, Color color, int glowAlpha, int layers) {
        /*
         * stack     - матрица рендера
         * x, y      - позиция текста/логотипа
         * width     - ширина объекта
         * height    - высота объекта
         * radius    - скругление углов
         * color     - цвет свечения
         * glowAlpha - максимальная непрозрачность свечения (0-255)
         * layers    - количество слоёв свечения
         */

        for (int i = layers; i > 0; i--) {
            float factor = i / (float) layers;
            float offset = i * 1.5f; 
            int alpha = (int) (glowAlpha * factor);
            drawBlurredRect(
                    stack,
                    x - offset,
                    y - offset,
                    width + offset * 2,
                    height + offset * 2,
                    radius + offset / 2f,
                    radius + offset / 2f,
                    new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha)
            );
        }
    }

    public void drawGlowOutline(MatrixStack stack, float x, float y, float width, float height, float radius, Color glowColor, int intensity, float glowRadius) {
        /*
         * stack      - матрица
         * x, y       - позиция
         * width      - ширина
         * height     - высота
         * radius     - скругление углов
         * glowColor  - цвет свечения
         * intensity  - максимальная непрозрачность (0-255)
         * glowRadius - радиус свечения
         */

        drawBorder(
                stack,
                x - glowRadius,
                y - glowRadius,
                width + glowRadius * 2,
                height + glowRadius * 2,
                radius + glowRadius,
                1f,          
                glowRadius,  
                new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), intensity)
        );
}

    /**
     * Рисует обводку только по углам (corners) вместо полной обводки
     */
    public void drawCornerOutline(MatrixStack stack, float x, float y, float width, float height, float cornerSize, Color color) {
        /*
         * stack       - матрица
         * x, y        - позиция
         * width       - ширина
         * height      - высота
         * cornerSize  - размер углов
         * color       - цвет обводки
         */
        
        float thickness = 2f;
        
        
        drawRect(stack, x, y, cornerSize, thickness, color);
        drawRect(stack, x, y, thickness, cornerSize, color);
        
        
        drawRect(stack, x + width - cornerSize, y, cornerSize, thickness, color);
        drawRect(stack, x + width - thickness, y, thickness, cornerSize, color);
        
        
        drawRect(stack, x, y + height - thickness, cornerSize, thickness, color);
        drawRect(stack, x, y + height - cornerSize, thickness, cornerSize, color);
        
        
        drawRect(stack, x + width - cornerSize, y + height - thickness, cornerSize, thickness, color);
        drawRect(stack, x + width - thickness, y + height - cornerSize, thickness, cornerSize, color);
    }

    /**
     * Рисует закругленные углы используя простой подход
     * @param stack - матрица
     * @param x - позиция X
     * @param y - позиция Y  
     * @param width - ширина
     * @param height - высота
     * @param cornerSize - размер углов
     * @param cornerColor - цвет углов
     * @param thickness - толщина линий
     */
    public void drawRoundedCorner(MatrixStack stack, float x, float y, float width, float height, 
                                 float cornerSize, Color cornerColor, float thickness) {
        
        if (cornerColor.getAlpha() <= 0) return;
        
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(cornerColor.getRed() / 255f, cornerColor.getGreen() / 255f, 
                                   cornerColor.getBlue() / 255f, cornerColor.getAlpha() / 255f);
        
        
        org.joml.Matrix4f matrix = stack.peek().getPositionMatrix();
        
        
        drawCornerElement(matrix, x, y, cornerSize, thickness, true, true); 
        drawCornerElement(matrix, x + width - cornerSize, y, cornerSize, thickness, false, true); 
        drawCornerElement(matrix, x, y + height - cornerSize, cornerSize, thickness, true, false); 
        drawCornerElement(matrix, x + width - cornerSize, y + height - cornerSize, cornerSize, thickness, false, false); 
        
        
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }
    
    /**
     * Рисует один угол как L-образный элемент
     */
    private void drawCornerElement(org.joml.Matrix4f matrix, float x, float y, float size, float thickness, 
                                  boolean left, boolean top) {
        
        
        float hX = left ? x : x + size - thickness;
        float hY = top ? y : y + size - thickness;
        float hWidth = left ? size : thickness;
        float hHeight = thickness;
        
        drawQuad(matrix, hX, hY, hWidth, hHeight);
        
        
        float vX = left ? x : x + size - thickness;
        float vY = top ? y : y + size - thickness;
        float vWidth = thickness;
        float vHeight = left ? thickness : size;
        
        drawQuad(matrix, vX, vY, vWidth, vHeight);
    }
    
    /**
     * Рисует простой прямоугольник через OpenGL
     */
    private void drawQuad(org.joml.Matrix4f matrix, float x, float y, float width, float height) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        bufferBuilder.vertex(matrix, x, y, 0).color(1f, 1f, 1f, 1f);
        bufferBuilder.vertex(matrix, x + width, y, 0).color(1f, 1f, 1f, 1f);
        bufferBuilder.vertex(matrix, x + width, y + height, 0).color(1f, 1f, 1f, 1f);
        bufferBuilder.vertex(matrix, x, y + height, 0).color(1f, 1f, 1f, 1f);
        
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }
    
    /**
     * Упрощенная версия drawRoundedCorner с настройками по умолчанию
     */
    public void drawRoundedCorner(MatrixStack stack, float x, float y, float width, float height, 
                                 float cornerSize, Color cornerColor) {
        drawRoundedCorner(stack, x, y, width, height, cornerSize, cornerColor, 2f);
    }
}
