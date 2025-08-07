package net.talor1n.titlebarchanger.utils.win32;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Window corner preferences for Windows 11+.
 */
@Getter
public
enum DwmWindowCornerPreference {
    /**
     * Let the system decide
     */
    @SerializedName("0")
    DWMWCP_DEFAULT(0),

    /**
     * Don't round corners
     */
    @SerializedName("1")
    DWMWCP_DONOTROUND(1),

    /**
     * Round the corners
     */
    @SerializedName("2")
    DWMWCP_ROUND(2),

    /**
     * Round the corners with small radius
     */
    @SerializedName("3")
    DWMWCP_ROUNDSMALL(3);

    private final int value;

    DwmWindowCornerPreference(int value) {
        this.value = value;
    }
}
