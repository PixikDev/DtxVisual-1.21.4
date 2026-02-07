package pixikdev.ru.dtxvisual.mixin;

import pixikdev.ru.dtxvisual.DtxVisual;
import pixikdev.ru.dtxvisual.client.events.impl.EventHandledScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
	@Shadow protected int backgroundWidth;
	@Shadow protected int backgroundHeight;
	@Shadow @Nullable protected Slot focusedSlot;

	@Inject(method = "render", at = @At("RETURN"))
	private void dtxvisual$afterRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		DtxVisual.getInstance().getEventHandler().post(new EventHandledScreen(context, focusedSlot, backgroundWidth, backgroundHeight));
	}
} 
