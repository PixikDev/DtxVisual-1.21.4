package pixikdev.ru.dtxvisual.client.events.impl;

import pixikdev.ru.dtxvisual.client.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@AllArgsConstructor
@Getter
@Setter
public class TotemPopEvent extends Event {
    Entity entity;
}
