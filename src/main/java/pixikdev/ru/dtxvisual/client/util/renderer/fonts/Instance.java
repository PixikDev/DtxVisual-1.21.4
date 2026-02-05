package pixikdev.ru.dtxvisual.client.util.renderer.fonts;

import pixikdev.ru.dtxvisual.client.render.msdf.MsdfFont;

public record Instance(MsdfFont font, float size) {
    public float getWidth(String text) {
        return font.getWidth(text, size);
    }

    public float getHeight() {
        return font.getHeight(size);
    }

}