package net.talor1n.titlebarchanger.neoforge;

import me.shedaniel.clothconfig2.ClothConfigDemo;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.compat.ClothConfigCompat;
import net.talor1n.titlebarchanger.utils.PlatformHelper;

@Mod(TitlebarChanger.MOD_ID)
public final class TitlebarChangerNeoForge {
    public TitlebarChangerNeoForge() {
        TitlebarChanger.init(FMLPaths.CONFIGDIR.get().resolve("titlebar_settings.json"));

        if (PlatformHelper.isModLoaded(ClothConfigInitializer.MOD_ID)) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, ()->
                    (modContainer, screen)-> ClothConfigCompat.createConfigScreen(screen));
        }
    }
}
