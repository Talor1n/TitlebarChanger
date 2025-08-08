package net.talor1n.titlebarchanger.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.talor1n.titlebarchanger.compat.ClothConfigCompat;

public class ClothConfigModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClothConfigCompat::createConfigScreen;
    }
}
