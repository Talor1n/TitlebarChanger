package net.talor1n.titlebarchanger.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.talor1n.titlebarchanger.compat.TitlebarConfigScreen;

public class ClothConfigModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            try {
                return TitlebarConfigScreen.createConfigScreen(parent);
            } catch (Throwable t) {
                return parent;
            }
        };
    }
}
