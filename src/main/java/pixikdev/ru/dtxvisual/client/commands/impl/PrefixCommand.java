package pixikdev.ru.dtxvisual.client.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import pixikdev.ru.dtxvisual.DtxVisual;
import pixikdev.ru.dtxvisual.client.commands.Command;
import pixikdev.ru.dtxvisual.client.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.client.resource.language.I18n;

public class PrefixCommand extends Command {
    public PrefixCommand() {
        super("prefix");
    }

    @Override
    public void execute(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> {
            String current = DtxVisual.getInstance().getCommandManager().getPrefix();
            ChatUtils.sendMessage(String.format(I18n.translate("cmd.prefix.current"), current));
            ChatUtils.sendMessage(I18n.translate("cmd.prefix.help"));
            return 1;
        });

        builder.then(literal("set")
                .then(arg("value", StringArgumentType.string())
                        .executes(context -> {
                            String newPrefix = StringArgumentType.getString(context, "value");
                            if (newPrefix == null || newPrefix.isEmpty()) {
                                ChatUtils.sendMessage(I18n.translate("cmd.prefix.error.empty"));
                                return 1;
                            }
                            DtxVisual.getInstance().getCommandManager().setPrefix(newPrefix);
                            ChatUtils.sendMessage(String.format(I18n.translate("cmd.prefix.changed"), newPrefix));
                            // persist via autosave
                            try {
                                DtxVisual.getInstance().getAutoSaveManager().scheduleAutoSave();
                            } catch (Throwable ignored) {}
                            return 1;
                        })));
    }
}
