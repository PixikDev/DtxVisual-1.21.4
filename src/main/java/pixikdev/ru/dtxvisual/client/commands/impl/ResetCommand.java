package pixikdev.ru.dtxvisual.client.commands.impl;

import pixikdev.ru.dtxvisual.DtxVisual;
import pixikdev.ru.dtxvisual.client.commands.Command;
import pixikdev.ru.dtxvisual.client.ChatUtils;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.modules.settings.Setting;
import pixikdev.ru.dtxvisual.client.ui.hud.HudElement;
import net.minecraft.command.CommandSource;
import net.minecraft.client.resource.language.I18n;
import pixikdev.ru.dtxvisual.client.managers.ThemeManager;

public class ResetCommand extends Command {
    private static long lastRequestMs = 0L;
    private static final long CONFIRM_WINDOW_MS = 10_000L;
    public ResetCommand() {
        super("reset");
    }

    @Override
    public void execute(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> {
            long now = System.currentTimeMillis();
            if (now - lastRequestMs > CONFIRM_WINDOW_MS) {
                lastRequestMs = now;
                ChatUtils.sendMessage(I18n.translate("cmd.reset.confirm"));
                return 1;
            }

            
            DtxVisual.getInstance().getCommandManager().setPrefix(".");

            
            for (Module module : DtxVisual.getInstance().getModuleManager().getModules()) {
                if (module.isToggled()) module.setToggled(false);
                for (Setting<?> setting : module.getSettings()) {
                    setting.reset();
                }
            }

            
            DtxVisual.getInstance().getModuleManager().resetBindsToDefaults();

            
            for (HudElement hud : DtxVisual.getInstance().getHudManager().getHudElements()) {
                for (Setting<?> setting : hud.getSettings()) {
                    setting.reset();
                }
                if (!hud.isToggled()) hud.setToggled(true);
            }

            
            try {
                ThemeManager.getInstance().setTheme(new ThemeManager.LightTheme());
            } catch (Throwable ignored) {}

            ChatUtils.sendMessage(I18n.translate("cmd.reset.done"));

            try {
                DtxVisual.getInstance().getAutoSaveManager().forceSave();
            } catch (Throwable ignored) {}
            lastRequestMs = 0L;
            return 1;
        });
    }
}
