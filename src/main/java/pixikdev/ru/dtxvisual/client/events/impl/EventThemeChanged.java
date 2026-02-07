package pixikdev.ru.dtxvisual.client.events.impl;

import pixikdev.ru.dtxvisual.client.events.Event;
import pixikdev.ru.dtxvisual.client.managers.ThemeManager;

public class EventThemeChanged extends Event {
    private final ThemeManager.Theme theme;
    
    public EventThemeChanged(ThemeManager.Theme theme) {
        this.theme = theme;
    }
    
    public ThemeManager.Theme getTheme() {
        return theme;
    }
} 
