package pixikdev.ru.dtxvisual.modules.impl.render;

import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.modules.settings.impl.NumberSetting;

public class HitColor extends Module {

    public final NumberSetting alpha = new NumberSetting("setting.alpha", 0.8f, 0.1f, 1.0f, 0.05f);

    public HitColor() {
        super("HitColor", Category.Render, "module.hitcolor.description");
    }
}
