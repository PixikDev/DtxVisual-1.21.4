package pixikdev.ru.dtxvisual.modules.impl.render;

import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import net.minecraft.client.resource.language.I18n;

public class Fullbright extends Module {

    public Fullbright() {
        super("Fullbright", Category.Render, I18n.translate("module.fullbright.description"));
    }
}
