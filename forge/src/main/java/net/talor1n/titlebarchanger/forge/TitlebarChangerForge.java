package net.talor1n.titlebarchanger.forge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.talor1n.titlebarchanger.TitlebarChanger;

@Mod(TitlebarChanger.MOD_ID)
public final class TitlebarChangerForge {
    public TitlebarChangerForge() {
        TitlebarChanger.init(FMLPaths.CONFIGDIR.get().resolve("titlebar_settings.json"));
    }
}
