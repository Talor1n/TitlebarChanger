package net.talor1n.titlebarchanger.utils.win32;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.network.chat.Component;

@Getter
public enum DwmWindowThemeAttribute {
    @SerializedName("0")
    LIGHT("titlebarchanger.theme.light"),
    @SerializedName("1")
    DARK("titlebarchanger.theme.dark"),
    @SerializedName("2")
    CUSTOM("titlebarchanger.theme.custom");

    private final String translationKey;

    DwmWindowThemeAttribute(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    @Override
    public String toString() {
        return getDisplayName().getString();
    }
}
