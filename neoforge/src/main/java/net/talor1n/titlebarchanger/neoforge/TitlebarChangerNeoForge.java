package net.talor1n.titlebarchanger.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.talor1n.titlebarchanger.TitlebarChanger;

@Mod(TitlebarChanger.MOD_ID)
public final class TitlebarChangerNeoForge {
    public TitlebarChangerNeoForge() {
        TitlebarChanger.init(FMLPaths.CONFIGDIR.get().resolve("titlebar_settings.json"));
    }
}
