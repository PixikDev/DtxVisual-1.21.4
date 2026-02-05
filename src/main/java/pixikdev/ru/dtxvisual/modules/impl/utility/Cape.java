package pixikdev.ru.dtxvisual.modules.impl.utility;

import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import net.minecraft.client.resource.language.I18n;

public class Cape extends Module {

    public Cape() {
        super("Cape", Category.Utility, I18n.translate("module.cape.description"));
    }

}
