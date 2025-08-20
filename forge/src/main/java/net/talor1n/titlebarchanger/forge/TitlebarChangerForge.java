package net.talor1n.titlebarchanger.forge;

import me.shedaniel.clothconfig2.ClothConfigInitializer;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.compat.TitlebarConfigScreen;

import static net.minecraftforge.fml.ModList.get;

@Mod(TitlebarChanger.MOD_ID)
public final class TitlebarChangerForge {
    public TitlebarChangerForge() {
        IEventBus modEventBus = MinecraftForge.EVENT_BUS;
        modEventBus.addListener(this::onClientSetup);

        if (isModLoaded(ClothConfigInitializer.MOD_ID)) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(((minecraft, parentScreen) -> TitlebarConfigScreen.createConfigScreen(parentScreen))));
        }
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        TitlebarChanger.init(FMLPaths.CONFIGDIR.get().resolve("titlebar_settings.json"));
    }

    public static boolean isModLoaded(String modId) {
        return get().isLoaded(modId);
    }
}
