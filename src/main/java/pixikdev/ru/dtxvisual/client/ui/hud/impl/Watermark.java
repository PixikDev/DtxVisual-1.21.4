package pixikdev.ru.dtxvisual.client.ui.hud.impl;

import pixikdev.ru.dtxvisual.client.events.impl.EventRender2D;
import pixikdev.ru.dtxvisual.client.ui.hud.HudElement;
import pixikdev.ru.dtxvisual.client.managers.ThemeManager;
import pixikdev.ru.dtxvisual.client.util.renderer.Render2D;
import pixikdev.ru.dtxvisual.client.util.renderer.fonts.Fonts;
import pixikdev.ru.dtxvisual.client.util.renderer.fonts.Font;
import pixikdev.ru.dtxvisual.client.util.perf.Perf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.awt.Color;

public class Watermark extends HudElement implements ThemeManager.ThemeChangeListener {
    private final ThemeManager themeManager;
    private final MinecraftClient mc;
    private Color bgColor;
    private Color accentColor;
    private Color textColor;

    public Watermark() {
        super("Watermark");
        this.themeManager = ThemeManager.getInstance();
        this.mc = MinecraftClient.getInstance();
        if (themeManager != null) {
            applyTheme(themeManager.getCurrentTheme());
            themeManager.addThemeChangeListener(this);
        }
    }

    private void applyTheme(ThemeManager.Theme theme) {
        // Base background slightly darkened for readability, but still follows theme
        Color baseBg = theme.getBackgroundColor();
        this.bgColor = new Color(
                baseBg.getRed(),
                baseBg.getGreen(),
                baseBg.getBlue(),
                180
        );
        this.accentColor = theme.getAccentColor();
        this.textColor = theme.getTextColor();
    }

    @Override
    public void onThemeChanged(ThemeManager.Theme theme) {
        applyTheme(theme);
    }

    @Override
    public void onRender2D(EventRender2D e) {
        if (fullNullCheck() || closed()) return;
        if (mc.player == null) return;

        // Fallback colors if theme system is not ready for some reason
        if (bgColor == null) {
            bgColor = new Color(15, 15, 15, 180);
        }
        if (accentColor == null) {
            accentColor = new Color(120, 120, 255, 220);
        }
        if (textColor == null) {
            textColor = Color.WHITE;
        }

        Perf.tryBeginFrame();
        try (var __ = Perf.scopeCpu("Watermark.onRender2D")) {
            var matrices = e.getContext().getMatrices();
            Font fontBold = Fonts.BOLD;
            Font fontRegular = Fonts.REGULAR;

            // Data
            int fps = mc.getCurrentFps();
            int ping = 0;
            if (mc.getNetworkHandler() != null) {
                PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                if (playerListEntry != null) {
                    ping = playerListEntry.getLatency();
                }
            }

            String title = "DtxVisual";
            String meta = String.format("%d fps  •  %d ms", fps, ping);

            float titleSize = 9.5f;
            float metaSize = 8.0f;
            float paddingX = 10f;
            float paddingY = 7f;
            float lineGap = 2.5f;

            float titleWidth = fontBold.getWidth(title, titleSize);
            float metaWidth = fontRegular.getWidth(meta, metaSize);
            float contentWidth = Math.max(titleWidth, metaWidth);

            float titleHeight = fontBold.getHeight(titleSize);
            float metaHeight = fontRegular.getHeight(metaSize);

            float totalWidth = paddingX * 2 + contentWidth;
            float totalHeight = paddingY * 2 + titleHeight + lineGap + metaHeight;

            setBounds(getX(), getY(), totalWidth, totalHeight);

            float x = getX();
            float y = getY();

            // Soft blur behind the card for readability
            Render2D.drawShaderBlurRect(
                    matrices,
                    x,
                    y,
                    totalWidth,
                    totalHeight,
                    7f,
                    18f,
                    new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 180)
            );

            // Main background card
            Render2D.drawRoundedRect(
                    matrices,
                    x,
                    y,
                    totalWidth,
                    totalHeight,
                    7f,
                    bgColor
            );

            // Accent strip on the left following current theme
            Color accent = new Color(
                    accentColor.getRed(),
                    accentColor.getGreen(),
                    accentColor.getBlue(),
                    220
            );
            Render2D.drawGradientRect(
                    matrices,
                    x,
                    y,
                    3f,
                    totalHeight,
                    accent,
                    accent.darker(),
                    false
            );

            // Slight glow around title text
            float titleX = x + paddingX;
            float titleY = y + paddingY;
            float titleBoxW = titleWidth;
            float titleBoxH = titleHeight;
            Render2D.drawTextGlow(
                    matrices,
                    titleX,
                    titleY,
                    titleBoxW,
                    titleBoxH,
                    6f,
                    accent,
                    70,
                    3
            );

            // Draw title
            Render2D.drawFont(
                    matrices,
                    fontBold.getFont(titleSize),
                    title,
                    titleX,
                    titleY,
                    textColor
            );

            // Meta text line (FPS / Ping) aligned under title
            float metaX = x + paddingX;
            float metaY = titleY + titleHeight + lineGap;
            Render2D.drawFont(
                    matrices,
                    fontRegular.getFont(metaSize),
                    meta,
                    metaX,
                    metaY,
                    new Color(220, 220, 220, 220)
            );

            super.onRender2D(e);
        }
    }
}