package pixikdev.ru.dtxvisual.client.events.impl;

import pixikdev.ru.dtxvisual.client.events.Event;
import pixikdev.ru.dtxvisual.modules.settings.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class EventSettingChange extends Event {
    private final Setting<?> setting;
}