package net.talor1n.titlebarchanger.neoforge;

import me.shedaniel.clothconfig2.ClothConfigInitializer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.compat.TitlebarConfigScreen;

import static net.neoforged.fml.ModList.get;

@Mod(TitlebarChanger.MOD_ID)
public final class TitlebarChangerNeoForge {
    public TitlebarChangerNeoForge() {
        TitlebarChanger.init(FMLPaths.CONFIGDIR.get().resolve("titlebar_settings.json"));

        if (isModLoaded(ClothConfigInitializer.MOD_ID)) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, ()->
                    (modContainer, parentScreen)-> TitlebarConfigScreen.createConfigScreen());
        }
    }

    public static boolean isModLoaded(String modId) {
        return get().isLoaded(modId);
    }
}
