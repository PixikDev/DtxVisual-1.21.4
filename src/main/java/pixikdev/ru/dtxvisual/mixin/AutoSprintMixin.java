package pixikdev.ru.dtxvisual.mixin;

import pixikdev.ru.dtxvisual.client.events.impl.EventTick;
import pixikdev.ru.dtxvisual.modules.impl.utility.AutoSprint;
import pixikdev.ru.dtxvisual.DtxVisual;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class AutoSprintMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        AutoSprint module = DtxVisual.getInstance().getModuleManager().getModule(AutoSprint.class);
        if (module != null && module.isToggled()) {
            module.onTick(new EventTick());
        }
    }
}
