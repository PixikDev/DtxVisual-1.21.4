package pixikdev.ru.dtxvisual.mixin.accessors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mutable;

@Mixin(MinecraftClient.class)
public interface IMinecraftClientSessionAccessor {
    @Accessor("session")
    @Mutable
    void dtxvisual$setSession(Session session);
}



