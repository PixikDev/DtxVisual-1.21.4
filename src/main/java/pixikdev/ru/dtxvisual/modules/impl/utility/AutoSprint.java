package pixikdev.ru.dtxvisual.modules.impl.utility;

import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.client.events.impl.EventTick;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class AutoSprint extends Module {

    public AutoSprint() {
        super("AutoSprint", Category.Utility, I18n.translate("module.autosprint.description"));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.options != null) {
            client.options.sprintKey.setPressed(true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.options != null) {
            client.options.sprintKey.setPressed(false);
        }
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (!isToggled()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;
        
        client.options.sprintKey.setPressed(true);
    }
}
