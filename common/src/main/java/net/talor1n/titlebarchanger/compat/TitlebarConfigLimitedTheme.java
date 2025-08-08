package net.talor1n.titlebarchanger.compat;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowThemeAttribute;

@Getter
enum TitlebarConfigLimitedTheme {
    LIGHT("titlebarchanger.theme.light"),
    DARK("titlebarchanger.theme.dark");

    private final String translationKey;

    TitlebarConfigLimitedTheme(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    static TitlebarConfigLimitedTheme toLimited(DwmWindowThemeAttribute t) {
        return t == DwmWindowThemeAttribute.DARK ? TitlebarConfigLimitedTheme.DARK : TitlebarConfigLimitedTheme.LIGHT;
    }

    static DwmWindowThemeAttribute fromLimited(TitlebarConfigLimitedTheme lt) {
        return lt == TitlebarConfigLimitedTheme.LIGHT ? DwmWindowThemeAttribute.LIGHT : DwmWindowThemeAttribute.DARK;
    }

    @Override
    public String toString() {
        return getDisplayName().getString();
    }
}
