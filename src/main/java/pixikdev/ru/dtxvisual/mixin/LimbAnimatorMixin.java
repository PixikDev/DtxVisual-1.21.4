package pixikdev.ru.dtxvisual.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import pixikdev.ru.dtxvisual.DtxVisual;
import pixikdev.ru.dtxvisual.modules.impl.render.NoRender;
import net.minecraft.entity.LimbAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LimbAnimator.class)
public abstract class LimbAnimatorMixin {

    @ModifyReturnValue(method = "getPos()F", at = @At("RETURN"))
    public float getPos(float original) {
        if (DtxVisual.getInstance().getModuleManager().getModule(NoRender.class).isToggled() && DtxVisual.getInstance().getModuleManager().getModule(NoRender.class).limbs.getValue()) return 0;
        else return original;
    }

    @ModifyReturnValue(method = "getPos(F)F", at = @At("RETURN"))
    public float getPos2(float original) {
        if (DtxVisual.getInstance().getModuleManager().getModule(NoRender.class).isToggled() && DtxVisual.getInstance().getModuleManager().getModule(NoRender.class).limbs.getValue()) return 0;
        else return original;
    }

    @ModifyReturnValue(method = "getSpeed()F", at = @At("RETURN"))
    public float getSpeed(float original) {
        if (DtxVisual.getInstance().getModuleManager().getModule(NoRender.class).isToggled() && DtxVisual.getInstance().getModuleManager().getModule(NoRender.class).limbs.getValue()) return 0;
        else return original;
    }

    @ModifyReturnValue(method = "getSpeed(F)F", at = @At("RETURN"))
    public float getSpeed2(float original) {
        if (DtxVisual.getInstance().getModuleManager().getModule(NoRender.class).isToggled() && DtxVisual.getInstance().getModuleManager().getModule(NoRender.class).limbs.getValue()) return 0;
        else return original;
    }
}
