package pixikdev.ru.dtxvisual.client.ui.hud;

import pixikdev.ru.dtxvisual.DtxVisual;
import pixikdev.ru.dtxvisual.client.events.impl.EventMouse;
import pixikdev.ru.dtxvisual.client.events.impl.EventRender2D;
import pixikdev.ru.dtxvisual.client.managers.AutoSaveManager;
import pixikdev.ru.dtxvisual.client.util.animations.Animation;
import pixikdev.ru.dtxvisual.client.util.animations.Easing;
import pixikdev.ru.dtxvisual.client.util.math.MathUtils;
import pixikdev.ru.dtxvisual.client.util.renderer.Render2D;
import pixikdev.ru.dtxvisual.client.util.renderer.fonts.Fonts;
import pixikdev.ru.dtxvisual.modules.settings.Setting;
import pixikdev.ru.dtxvisual.modules.settings.api.Position;
import pixikdev.ru.dtxvisual.modules.settings.impl.BooleanSetting;
import pixikdev.ru.dtxvisual.modules.settings.impl.PositionSetting;
import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.client.ui.hud.windows.Window;
import lombok.Getter;
import lombok.Setter;

import meteordevelopment.orbit.EventHandler;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resource.language.I18n;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Getter @Setter
public abstract class HudElement extends Module {

    private final PositionSetting position = new PositionSetting("setting.position", new Position(0, 0));
    private final Animation hoverAnimation = new Animation(300, 1f, false, Easing.SMOOTH_STEP);
    private final Animation cornerAnimation = new Animation(200, 1f, false, Easing.OUT_QUART);
    protected final Animation toggledAnimation = new Animation(300, 1f, false, Easing.BOTH_SINE);
    private float dragX, dragY, width, height;
    private boolean dragging, button;
    private final List<Setting<?>> settings = new ArrayList<>();
    private Window window;

    public HudElement(String name) {
        super(name, Category.Utility);
        settings.add(position);
        setToggled(true);
    }

    @EventHandler
    public void onRender2D(EventRender2D e) {
        if (fullNullCheck()) return;

        hoverAnimation.update(MathUtils.isHovered(getX(), getY(), getWidth(), getHeight(), mouseX(), mouseY()) && window == null || button || dragging);
        cornerAnimation.update(hoverAnimation.getValue() > 0);

        if (button) {
            
            HudElement current = DtxVisual.getInstance().getHudManager().getCurrentDragging();
            if (current != null && current != this) {
                dragging = false;
                return;
            }

            
            if (!(mc.currentScreen instanceof ChatScreen)) {
                dragging = false;
                button = false;
                DtxVisual.getInstance().getHudManager().setCurrentDragging(null);
                return;
            }

            if (!dragging && MathUtils.isHovered(getX(), getY(), getWidth(), getHeight(), mouseX(), mouseY())) {
                dragX = mouseX() - getX();
                dragY = mouseY() - getY();
                dragging = true;
                DtxVisual.getInstance().getHudManager().setCurrentDragging(this);
            }

            if (dragging) {
                
                float sw = mc.getWindow().getScaledWidth();
                float sh = mc.getWindow().getScaledHeight();
                float finalX = Math.min(Math.max(mouseX() - dragX, 0), sw - width);
                float finalY = Math.min(Math.max(mouseY() - dragY, 0), sh - height);

                // Snap-to guide lines
                float threshold = 6f; // px threshold to snap
                float edgePad = 4f;   // side guide line padding

                // Snap X to vertical center
                float centerX = sw / 2f - width / 2f; // align element center to screen center
                if (Math.abs((finalX + width / 2f) - sw / 2f) <= threshold) {
                    finalX = centerX;
                } else {
                    // Snap X to nearest side (left or right)
                    float leftX = edgePad;
                    float rightX = sw - edgePad - width;
                    if (Math.abs(finalX - leftX) <= threshold) finalX = leftX;
                    else if (Math.abs(finalX - rightX) <= threshold) finalX = rightX;
                }

                // Snap Y to horizontal center
                float centerY = sh / 2f - height / 2f;
                if (Math.abs((finalY + height / 2f) - sh / 2f) <= threshold) {
                    finalY = centerY;
                } else {
                    // Snap Y to nearest side (top or bottom)
                    float topY = edgePad;
                    float bottomY = sh - edgePad - height;
                    if (Math.abs(finalY - topY) <= threshold) finalY = topY;
                    else if (Math.abs(finalY - bottomY) <= threshold) finalY = bottomY;
                }

                
                float newX = finalX / sw;
                float newY = finalY / sh;

                if (Math.abs(position.getValue().getX() - newX) > 0.001f ||
                    Math.abs(position.getValue().getY() - newY) > 0.001f) {
                    position.getValue().setX(newX);
                    position.getValue().setY(newY);
                    
                    try {
                        AutoSaveManager asm = DtxVisual.getInstance().getAutoSaveManager();
                        if (asm != null) asm.scheduleAutoSave();
                    } catch (Throwable ignored) {}
                }
            }
        } else {
            dragging = false;
        }

        
        if (mc.currentScreen instanceof ChatScreen && cornerAnimation.getValue() > 0) {
            float animationValue = cornerAnimation.getValue();
            float animatedCornerSize = 16f * animationValue; 
            int alpha = (int) (255 * animationValue); 
            
            
            float padding = 4f; 
            float cornerX = getX() - padding;
            float cornerY = getY() - padding;
            float cornerWidth = getWidth() + (padding * 2);
            float cornerHeight = getHeight() + (padding * 2);
            
            
            
            
            Render2D.drawRoundedCorner(
                    e.getContext().getMatrices(),
                    cornerX, cornerY,
                    cornerWidth, cornerHeight,
                    animatedCornerSize,
                    new Color(255, 255, 255, alpha) 
            );
        }

        
        if (mc.currentScreen instanceof ChatScreen && dragging) {
            float sw = mc.getWindow().getScaledWidth();
            float sh = mc.getWindow().getScaledHeight();
            float edgePad = 4f;

            // Center lines
            Color guide = new Color(255, 255, 255, 110);
            // Vertical center
            Render2D.drawRoundedRect(e.getContext().getMatrices(), sw / 2f - 0.5f, 0, 1f, sh, 0f, guide);
            // Horizontal center
            Render2D.drawRoundedRect(e.getContext().getMatrices(), 0, sh / 2f - 0.5f, sw, 1f, 0f, guide);

            // Nearest side lines (vertical and horizontal)
            // Choose nearest X side to current element
            float leftX = edgePad;
            float rightX = sw - edgePad;
            float elemCenterX = getX() + getWidth() / 2f;
            float sideX = (Math.abs(elemCenterX - leftX) < Math.abs(elemCenterX - rightX)) ? leftX : rightX;
            Render2D.drawRoundedRect(e.getContext().getMatrices(), sideX - 0.5f, 0, 1f, sh, 0f, new Color(255, 255, 255, 80));

            float topY = edgePad;
            float bottomY = sh - edgePad;
            float elemCenterY = getY() + getHeight() / 2f;
            float sideY = (Math.abs(elemCenterY - topY) < Math.abs(elemCenterY - bottomY)) ? topY : bottomY;
            Render2D.drawRoundedRect(e.getContext().getMatrices(), 0, sideY - 0.5f, sw, 1f, 0f, new Color(255, 255, 255, 80));
        }

        if (window != null) {
            if (!(mc.currentScreen instanceof ChatScreen)) window.reset();

            if (window.closed()) {
                window = null;
                return;
            }

            window.render(e.getContext(), mouseX(), mouseY());
        }


        if (mc.currentScreen instanceof ChatScreen) {
            String text = I18n.translate("RMB.setting");
            int textWidth = mc.textRenderer.getWidth(text);
            int x = 10;
            int y = mc.getWindow().getScaledHeight() - 30;
            Render2D.drawFont(e.getContext().getMatrices(), Fonts.REGULAR.getFont(9f), text, x, y, new Color(255, 255, 255));
//            e.getContext().drawTextWithShadow(mc.textRenderer, text, x, y, 0xFFFFFF);
        }
    }

    @EventHandler
    public void onRender2DX2(EventRender2D e) {
        if (fullNullCheck()) return;

        Setting<?> setting = DtxVisual.getInstance().getHudManager().getElements().getName(getName());
        if (setting != null && setting instanceof BooleanSetting) {
            toggledAnimation.update(((BooleanSetting) setting).getValue());
        } else {
            
            
            toggledAnimation.update(isToggled());
        }
    }

    @EventHandler
    public void onMouse(EventMouse e) {
        if (!(mc.currentScreen instanceof ChatScreen) || fullNullCheck()) return;

        if (e.getAction() == 0) {
            button = false;
            dragging = false;
            DtxVisual.getInstance().getHudManager().setCurrentDragging(null);
            
            try {
                AutoSaveManager asm = DtxVisual.getInstance().getAutoSaveManager();
                if (asm != null) asm.scheduleAutoSave();
            } catch (Throwable ignored) {}
        } else if (e.getAction() == 1) {
            
            if (DtxVisual.getInstance().getHudManager().getCurrentDragging() == null ||
                DtxVisual.getInstance().getHudManager().getCurrentDragging() == this) {
                button = true;
            }

            if (window != null) {
                if (MathUtils.isHovered(window.getX(), window.getY(), window.getWidth(), window.getFinalHeight(), mouseX(), mouseY())) {
                    window.mouseClicked(mouseX(), mouseY(), e.getButton());
                    return;
                } else window.reset();
            }

            if (e.getButton() == 1) {
                if (MathUtils.isHovered(getX(), getY(), getWidth(), getHeight(), mouseX(), mouseY())) {

                    if (settings.size() == 1) {
                    } else {
                        if (DtxVisual.getInstance().getHudManager().getWindow() != null)
                            DtxVisual.getInstance().getHudManager().getWindow().reset();
                        for (HudElement element : DtxVisual.getInstance().getHudManager().getHudElements()) {
                            if (element.getWindow() == null) continue;
                            element.getWindow().reset();
                        }
                        window = new Window(mouseX() + 3, mouseY() + 3, 100, 12.5f, settings);
                    }
                }
            }
        }
    }

    public float getX() {
        return mc.getWindow().getScaledWidth() * position.getValue().getX();
    }

    public float getY() {
        return mc.getWindow().getScaledHeight() * position.getValue().getY();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int mouseX() {
        return (int) (mc.mouse.getX() / mc.getWindow().getScaleFactor());
    }

    public int mouseY() {
        return (int) (mc.mouse.getY() / mc.getWindow().getScaleFactor());
    }

    public void setBounds(float x, float y, float width, float height) {
        this.width = width;
        this.height = height;
        position.getValue().setX(x / mc.getWindow().getScaledWidth());
        position.getValue().setY(y / mc.getWindow().getScaledHeight());
        
        try {
            AutoSaveManager asm = DtxVisual.getInstance().getAutoSaveManager();
            if (asm != null) asm.scheduleAutoSave();
        } catch (Throwable ignored) {}
    }

    protected boolean closed() {
        return toggledAnimation.getValue() <= 0f;
    }

    /**
     * Вызывается при перетаскивании элемента для рисования углов
     */
    protected void onDragging(EventRender2D e) {
        if (dragging) {
            
            float padding = 4f;
            float cornerX = getX() - padding;
            float cornerY = getY() - padding;
            float cornerWidth = getWidth() + (padding * 2);
            float cornerHeight = getHeight() + (padding * 2);
            
            
            Render2D.drawRoundedCorner(
                    e.getContext().getMatrices(),
                    cornerX, cornerY,
                    cornerWidth, cornerHeight,
                    16f, 
                    new Color(255, 255, 255, (int) (255 * hoverAnimation.getValue())), 
                    2f 
            );
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
