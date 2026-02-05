package pixikdev.ru.dtxvisual.client.events.impl;

import pixikdev.ru.dtxvisual.client.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class EventKey extends Event {
    private int key, action, modifiers;
}