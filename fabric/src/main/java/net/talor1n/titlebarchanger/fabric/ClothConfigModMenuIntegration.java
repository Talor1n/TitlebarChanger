package net.talor1n.titlebarchanger.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import net.talor1n.titlebarchanger.compat.TitlebarConfigScreen;

public class ClothConfigModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (TitlebarChangerFabric.isModLoaded(ClothConfigInitializer.MOD_ID)) {
            return parentScreen -> TitlebarConfigScreen.createConfigScreen();
        }
        return null;
    }
}
