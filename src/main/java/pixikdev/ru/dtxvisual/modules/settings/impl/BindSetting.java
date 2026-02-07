package pixikdev.ru.dtxvisual.modules.settings.impl;

import pixikdev.ru.dtxvisual.modules.settings.Setting;
import pixikdev.ru.dtxvisual.modules.settings.api.Bind;

import java.util.function.Supplier;

public class BindSetting extends Setting<Bind> {

    public BindSetting(String name, Bind defaultValue) {
        super(name, defaultValue);
    }

    public BindSetting(String name, Bind defaultValue, Supplier<Boolean> visible) {
        super(name, defaultValue, visible);
    }
}
