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
        this.bgColor = new Color(15, 15, 15, 160);
    }

    @Override
    public void onThemeChanged(ThemeManager.Theme theme) {
        applyTheme(theme);
    }

    @Override
    public void onRender2D(EventRender2D e) {
        if (fullNullCheck() || closed()) return;
        if (mc.player == null) return;

        Perf.tryBeginFrame();
        try (var __ = Perf.scopeCpu("Watermark.onRender2D")) {
            var matrices = e.getContext().getMatrices();
            Font fontBold = Fonts.BOLD;

            int fps = mc.getCurrentFps();

            int ping = 0;
            if (mc.getNetworkHandler() != null && mc.player != null) {
                PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                if (playerListEntry != null) {
                    ping = playerListEntry.getLatency();
                }
            }

            String text = String.format("DtxVisual | %d fps | %d ms", fps, ping);

            float fontSize = 9f;
            float paddingX = 8f;
            float paddingY = 10f;

            float textWidth = fontBold.getWidth(text, fontSize);
            float totalWidth = paddingX * 2 + textWidth;
            float totalHeight = paddingY * 2 + fontSize;

            setBounds(getX(), getY(), totalWidth, totalHeight);

            Render2D.drawRoundedRect(
                    matrices,
                    getX(), getY(),
                    totalWidth, totalHeight,
                    5f,
                    bgColor
            );

            float textX = getX() + paddingX;
            float textY = getY() + (totalHeight - fontSize) / 8 + fontSize * 0.75f;

            Render2D.drawFont(
                    matrices,
                    fontBold.getFont(fontSize),
                    text,
                    textX,
                    textY,
                    Color.WHITE
            );

            super.onRender2D(e);
        }
    }
}