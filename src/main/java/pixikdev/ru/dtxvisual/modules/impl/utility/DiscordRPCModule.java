package pixikdev.ru.dtxvisual.modules.impl.utility;

import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.util.DiscordRichPresenceUtil;
import net.minecraft.client.resource.language.I18n;

public class DiscordRPCModule extends Module {
    public DiscordRPCModule() {
        super("DiscordRPC", Category.Utility, I18n.translate("module.discordrpc.description"));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        DiscordRichPresenceUtil.discordrpc();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        DiscordRichPresenceUtil.shutdownDiscord();
    }
}
