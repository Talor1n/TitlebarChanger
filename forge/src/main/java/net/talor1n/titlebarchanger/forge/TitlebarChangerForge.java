package net.talor1n.titlebarchanger.forge;

import me.shedaniel.clothconfig2.ClothConfigInitializer;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.compat.ClothConfigCompat;
import net.talor1n.titlebarchanger.utils.PlatformHelper;

@Mod(TitlebarChanger.MOD_ID)
public final class TitlebarChangerForge {
    public TitlebarChangerForge() {
        TitlebarChanger.init(FMLPaths.CONFIGDIR.get().resolve("titlebar_settings.json"));

        if (PlatformHelper.isModLoaded(ClothConfigInitializer.MOD_ID)) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(((minecraft, screen) -> ClothConfigCompat.createConfigScreen(screen))));
        }
    }
}
