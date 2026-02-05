package pixikdev.ru.dtxvisual.client.util.macro;

import pixikdev.ru.dtxvisual.modules.settings.api.Bind;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class Macro {
    private String name, command;
    private Bind bind;
}