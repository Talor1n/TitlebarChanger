package net.talor1n.titlebarchanger.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.config.ConfigManager;
import net.talor1n.titlebarchanger.ui.WarnScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class AddWarnScreen {
    @Inject(at = {@At("TAIL")}, method = {"init"})
    protected void init(CallbackInfo ci) {
        if (TitlebarChanger.api.isNotWindows11() && ConfigManager.INSTANCE.getConfig().isShowWarnScreen()) {
            Minecraft.getInstance().setScreen(new WarnScreen());
        }
    }
}
