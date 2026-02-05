package dev.simplevisuals.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen {
    // Кастомное главное меню отключено; миксин оставлен пустым, чтобы не влиять на TitleScreen.
}