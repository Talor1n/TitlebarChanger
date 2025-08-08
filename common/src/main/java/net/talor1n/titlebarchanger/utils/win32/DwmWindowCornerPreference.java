package net.talor1n.titlebarchanger.utils.win32;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.network.chat.Component;

/**
 * Window corner preferences for Windows 11+.
 */
@Getter
public enum DwmWindowCornerPreference {
    /**
     * Let the system decide
     */
    @SerializedName("0")
    SYSTEM_DEFAULT(0, "titlebarchanger.corner.system_default"),

    /**
     * Don't round corners
     */
    @SerializedName("1")
    NO_ROUNDING(1, "titlebarchanger.corner.no_rounding"),

    /**
     * Round the corners
     */
    @SerializedName("2")
    ROUNDED(2, "titlebarchanger.corner.rounded"),

    /**
     * Round the corners with small radius
     */
    @SerializedName("3")
    ROUNDED_SMALL(3, "titlebarchanger.corner.rounded_small");

    private final int value;
    private final String translationKey;

    DwmWindowCornerPreference(int value, String translationKey) {
        this.value = value;
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
