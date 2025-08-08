package net.talor1n.titlebarchanger.compat;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionResult;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.config.ConfigManager;
import net.talor1n.titlebarchanger.config.TitlebarChangerConfig;

public class ClothConfigCompat {
    private static boolean isRegistered = false;

    private ClothConfigCompat(){}

    public static Screen createConfigScreen(Screen screen) {
        register();
        return AutoConfig.getConfigScreen(TitlebarChangerConfig.class, screen).get();
    }

    public static void register() {
        if (isRegistered) return;
        var config = AutoConfig.register(TitlebarChangerConfig.class, GsonConfigSerializer::new);
        config.setConfig(ConfigManager.INSTANCE.getTitlebarChangerConfig());
        config.registerSaveListener((configHolder, config1) -> {
            TitlebarChanger.api.loadStyle();
            return InteractionResult.SUCCESS;
        });
        isRegistered = true;
    }
}
