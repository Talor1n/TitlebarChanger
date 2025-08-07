package net.talor1n.titlebarchanger.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.config.ConfigManager;
import net.talor1n.titlebarchanger.ui.TitleBarSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class AddTitleBarSettingsButton {
    @Shadow
    @Final
    private Screen lastScreen;

    @Shadow
    protected abstract Button openScreenButton(Component var1, Supplier<Screen> var2);

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/options/OptionsScreen;openScreenButton(Lnet/minecraft/network/chat/Component;Ljava/util/function/Supplier;)Lnet/minecraft/client/gui/components/Button;",
                    ordinal = 4
            )
    )
    protected void init(CallbackInfo ci, @Local GridLayout.RowHelper rowHelper) {
        if (TitlebarChanger.api.isWindows11() && ConfigManager.INSTANCE.getConfig().isShowTheMenu()) {
            rowHelper.addChild(this.openScreenButton(Component.translatable("gui.titlebar_settings"), () -> new TitleBarSettings(this.lastScreen)));
        }
    }
}