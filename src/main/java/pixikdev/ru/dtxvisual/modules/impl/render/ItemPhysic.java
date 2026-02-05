package pixikdev.ru.dtxvisual.modules.impl.render;

import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import net.minecraft.client.resource.language.I18n;

public class ItemPhysic extends Module {
    public ItemPhysic() {
        super("ItemPhysic", Category.Render, I18n.translate("module.itemphysic.description"));
    }
}
