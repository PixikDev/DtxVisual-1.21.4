package pixikdev.ru.dtxvisual.client.events.impl;

import pixikdev.ru.dtxvisual.client.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class EventMouse extends Event {
    private int button, action;
}