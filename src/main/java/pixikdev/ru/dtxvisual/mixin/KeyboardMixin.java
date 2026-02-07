package pixikdev.ru.dtxvisual.mixin;

import pixikdev.ru.dtxvisual.DtxVisual;
import pixikdev.ru.dtxvisual.client.events.impl.EventKey;
import pixikdev.ru.dtxvisual.modules.impl.render.BetterMinecraft;
import pixikdev.ru.dtxvisual.client.ui.hud.impl.PerfHUD;
import pixikdev.ru.dtxvisual.client.ui.hud.HudElement;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

	@Inject(method = "onKey", at = @At("HEAD"))
	public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		EventKey event = new EventKey(key, action, modifiers);
		DtxVisual.getInstance().getEventHandler().post(event);

		// Ctrl + Shift + Q: toggle Perf HUD overlay (spawn on demand, not listed in elements)
		if (key == GLFW.GLFW_KEY_Q && action == GLFW.GLFW_PRESS
				&& (modifiers & GLFW.GLFW_MOD_CONTROL) != 0
				&& (modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
			try {
				var hudManager = DtxVisual.getInstance().getHudManager();
				if (hudManager != null) {
					PerfHUD perfHud = null;
					for (HudElement he : hudManager.getHudElements()) {
						if (he instanceof PerfHUD p) { perfHud = p; break; }
					}
					if (perfHud == null) {
						perfHud = new PerfHUD();
						// Set a default position near top-left if needed
						perfHud.setBounds(10, 10, 180, 120);
						hudManager.getHudElements().add(perfHud);
						DtxVisual.getInstance().getEventHandler().subscribe(perfHud);
					}
					perfHud.setToggled(!perfHud.isToggled());
				}
			} catch (Throwable ignored) {}
		}

		BetterMinecraft module = DtxVisual.getInstance().getModuleManager().getModule(BetterMinecraft.class);
		if (module == null || !module.isToggled()) return;

		
		if (key == GLFW.GLFW_KEY_F5 && action == GLFW.GLFW_PRESS && module.smoothThirdPersonZoom.getValue()) {
			module.getThirdPersonAnimation().reset();
		}

		
		if (key == GLFW.GLFW_KEY_TAB) {
			if (action == GLFW.GLFW_PRESS && module.smoothTab.getValue()) {
				module.setTabPressed(true);
				module.getTabOpenAnimation().reset();
				module.getTabOpenAnimation().update(true);
			} else if (action == GLFW.GLFW_RELEASE && module.smoothTab.getValue()) {
				module.setTabPressed(false);
				module.getTabOpenAnimation().reset();
				module.getTabOpenAnimation().update(false);
			}
		}
	}
}
