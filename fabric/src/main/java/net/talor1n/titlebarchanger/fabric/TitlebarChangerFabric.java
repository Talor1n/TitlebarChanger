package net.talor1n.titlebarchanger.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.talor1n.titlebarchanger.TitlebarChanger;

import static net.fabricmc.loader.api.FabricLoader.getInstance;

public final class TitlebarChangerFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TitlebarChanger.init(FabricLoader.getInstance().getConfigDir().resolve("titlebar_settings.json"));
    }

    public static boolean isModLoaded(String modId) {
        return getInstance().isModLoaded(modId);
    }
}
