package pixikdev.ru.dtxvisual.mixin;

import pixikdev.ru.dtxvisual.DtxVisual;
import pixikdev.ru.dtxvisual.client.events.impl.EventTick;
import pixikdev.ru.dtxvisual.client.events.impl.EventGameShutdown;
import pixikdev.ru.dtxvisual.client.util.math.Counter;
import pixikdev.ru.dtxvisual.client.managers.HitDetectionManager;
import pixikdev.ru.dtxvisual.client.util.Wrapper;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements Wrapper {
    
    private static int tickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        EventTick event = new EventTick();
        DtxVisual.getInstance().getEventHandler().post(event);
        Counter.updateFPS();

        tickCounter++;
        if (tickCounter >= 20) {
            HitDetectionManager.getInstance().cleanup();
            tickCounter = 0;
        }
    }

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    public void updateWindowTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("DtxVisual 1.1 - 1.21.4");
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void onStop(CallbackInfo ci) {
        DtxVisual.getInstance().getEventHandler().post(new EventGameShutdown());
    }
}