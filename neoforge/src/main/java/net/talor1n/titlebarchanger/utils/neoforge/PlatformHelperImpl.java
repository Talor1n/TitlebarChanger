package net.talor1n.titlebarchanger.utils.neoforge;

import static net.neoforged.fml.ModList.get;

public class PlatformHelperImpl {
    public static boolean isModLoaded(String modId) {
        return get().isLoaded(modId);
    }
}
