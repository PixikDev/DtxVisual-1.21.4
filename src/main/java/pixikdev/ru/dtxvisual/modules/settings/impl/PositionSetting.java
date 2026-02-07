package pixikdev.ru.dtxvisual.modules.settings.impl;

import pixikdev.ru.dtxvisual.modules.settings.Setting;
import pixikdev.ru.dtxvisual.modules.settings.api.Position;

import java.util.function.Supplier;

public class PositionSetting extends Setting<Position> {

    public PositionSetting(String name, Position defaultValue) {
        super(name, defaultValue);
    }

    public PositionSetting(String name, Position defaultValue, Supplier<Boolean> visible) {
        super(name, defaultValue, visible);
    }
}
