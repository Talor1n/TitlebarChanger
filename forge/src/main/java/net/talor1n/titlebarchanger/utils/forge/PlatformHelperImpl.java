package net.talor1n.titlebarchanger.utils.forge;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;

import static net.minecraftforge.fml.ModList.*;

public class PlatformHelperImpl {
    public static boolean isModLoaded(String modId) {
        return get().isLoaded(modId);
    }
}
