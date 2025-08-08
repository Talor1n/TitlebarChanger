package net.talor1n.titlebarchanger.utils.fabric;

import static net.fabricmc.loader.api.FabricLoader.getInstance;

public class PlatformHelperImpl {
    public static boolean isModLoaded(String modId) {
        return getInstance().isModLoaded(modId);
    }
}
