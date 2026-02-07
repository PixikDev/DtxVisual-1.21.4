package pixikdev.ru.dtxvisual.modules.impl.render;

import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.modules.settings.impl.NumberSetting;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import net.minecraft.client.resource.language.I18n;

@Getter
public class AspectRatio extends Module {

    private final @NotNull NumberSetting aspectRatio = new NumberSetting(
            "setting.aspectRatio",
            1.777f,   
            0.5f,     
            3.0f,     
            0.01f     
    );

    public AspectRatio() {
        super("Aspect Ratio", Category.Render, I18n.translate("module.aspectratio.description"));
    }
}
