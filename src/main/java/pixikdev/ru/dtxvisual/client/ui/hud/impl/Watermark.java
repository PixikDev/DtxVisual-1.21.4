package pixikdev.ru.dtxvisual.client.ui.hud.impl;

import pixikdev.ru.dtxvisual.client.events.impl.EventRender2D;
import pixikdev.ru.dtxvisual.client.ui.hud.HudElement;
import pixikdev.ru.dtxvisual.client.managers.ThemeManager;
import pixikdev.ru.dtxvisual.client.util.renderer.Render2D;
import pixikdev.ru.dtxvisual.client.util.renderer.fonts.Fonts;
import pixikdev.ru.dtxvisual.client.util.renderer.fonts.Font;
import pixikdev.ru.dtxvisual.client.util.perf.Perf;
import java.awt.Color;

public class Watermark extends HudElement implements ThemeManager.ThemeChangeListener {
    private final ThemeManager themeManager;
    private Color bgColor;
    private Color accentColor;
    private float totalWidth, totalHeight;

    public Watermark() {
        super("Watermark");
        this.themeManager = ThemeManager.getInstance();
        if (themeManager != null) {
            applyTheme(themeManager.getCurrentTheme());
            themeManager.addThemeChangeListener(this);
        }
    }

    private void applyTheme(ThemeManager.Theme theme) {
        this.bgColor = new Color(20, 20, 20, 220);
        this.accentColor = theme != null ? theme.getAccentColor() : new Color(131, 60, 205);
    }

    @Override
    public void onThemeChanged(ThemeManager.Theme theme) {
        applyTheme(theme);
    }

    @Override
    public void onRender2D(EventRender2D e) {
        if (fullNullCheck() || closed()) return;
        Perf.tryBeginFrame();
        try (var __ = Perf.scopeCpu("Watermark.onRender2D")) {
            var matrices = e.getContext().getMatrices();
            Font fontBold = Fonts.BOLD;
            Font fontRegular = Fonts.REGULAR;
            Font fontIcons = Fonts.ICONS;

            String title = "dtxvisual";
            String version = "v0.3";
            String link = "t.me/dtxvisual";

            float fontSizeTitle = 10f;
            float fontSizeVersion = 7f;
            float fontSizeLink = 7f;

            float paddingX = 13f;
            float paddingY = 8f;
            float logoSize = 19f;

            float titleWidth = fontBold.getWidth(title, fontSizeTitle);
            float versionWidth = fontRegular.getWidth(version, fontSizeVersion);
            float linkWidth = fontRegular.getWidth(link, fontSizeLink);
            float textWidth = Math.max(titleWidth + 4 + versionWidth, linkWidth);

            totalWidth = paddingX * 2 + logoSize + 9 + textWidth;
            totalHeight = paddingY * 2 + fontSizeTitle + fontSizeLink + 3f;

            setBounds(getX(), getY(), totalWidth, totalHeight);

            Color liveAccent = themeManager != null ? themeManager.getCurrentTheme().getAccentColor() : accentColor;

            // Основной фон
            Render2D.drawRoundedRect(
                    matrices,
                    getX(), getY(),
                    totalWidth, totalHeight,
                    7f,
                    bgColor
            );

            // Логотип с эффектом
            float logoX = getX() + paddingX;
            float logoY = getY() + (totalHeight - logoSize) / 2f;

            // Эффект свечения
            Render2D.drawRoundedRect(
                    matrices,
                    logoX - 0.5f, logoY - 0.5f,
                    logoSize + 1f, logoSize + 1f,
                    5f,
                    new Color(liveAccent.getRed(), liveAccent.getGreen(), liveAccent.getBlue(), 80)
            );

            // Основной логотип
            Render2D.drawRoundedRect(
                    matrices,
                    logoX, logoY,
                    logoSize, logoSize,
                    5f,
                    liveAccent
            );

            // Буква D в логотипе
            Render2D.drawFont(
                    matrices,
                    fontIcons.getFont(logoSize * 0.65f),
                    "D",
                    logoX + logoSize * 0.175f,
                    logoY + logoSize * 0.175f,
                    Color.WHITE
            );

            // Текст
            float textX = getX() + paddingX + logoSize + 9;
            float textY = getY() + paddingY;

            // Заголовок
            Render2D.drawFont(
                    matrices,
                    fontBold.getFont(fontSizeTitle),
                    title,
                    textX,
                    textY,
                    Color.WHITE
            );

            // Версия
            Render2D.drawFont(
                    matrices,
                    fontRegular.getFont(fontSizeVersion),
                    version,
                    textX + titleWidth + 4,
                    textY + 0.5f,
                    new Color(170, 170, 170)
            );

            // Ссылка
            Render2D.drawFont(
                    matrices,
                    fontRegular.getFont(fontSizeLink),
                    link,
                    textX,
                    textY + fontSizeTitle + 2.5f,
                    new Color(200, 200, 200)
            );

            super.onRender2D(e);
        }
    }
}