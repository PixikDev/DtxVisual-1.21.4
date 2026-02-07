package pixikdev.ru.dtxvisual.modules.impl.render;

import pixikdev.ru.dtxvisual.DtxVisual;
import pixikdev.ru.dtxvisual.client.ChatUtils;
import pixikdev.ru.dtxvisual.client.events.impl.EventTick;
import pixikdev.ru.dtxvisual.client.ui.clickgui.ClickGui;
import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.modules.settings.api.Bind;
import meteordevelopment.orbit.EventHandler;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.resource.language.I18n;

public class UI extends Module {


    public UI() {
        super("ClickGui", Category.Render, I18n.translate("module.ui.description"));
        setBind(new Bind(GLFW.GLFW_KEY_RIGHT_SHIFT, false));

    }

    @EventHandler
    public void onTick(EventTick e) {
        if (!(mc.currentScreen instanceof ClickGui) && !(mc.currentScreen instanceof ClickGui)) {
            setToggled(false);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

		// Allow opening only when in a world
		if (mc.player == null || mc.world == null) {
			ChatUtils.sendMessage(I18n.translate("dtxvisual.ui.onlyInWorld"));
			setToggled(false);
			return;
		}

		mc.setScreen(DtxVisual.getInstance().getClickGui());


    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.currentScreen instanceof ClickGui) {
            ((ClickGui) mc.currentScreen).close();
        }
    }
}
