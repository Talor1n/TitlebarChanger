package net.talor1n.titlebarchanger.utils.win32;

import lombok.Getter;

/**
 * Windows DWM window attributes enumeration.
 * <p>
 * These constants represent various window attributes that can be modified
 * through the DWM API to customize window appearance and behavior.
 */
@Getter
public enum DwmWindowAttribute {
    /**
     * Use immersive dark mode (Windows 10 1903+)
     */
    DWMWA_USE_IMMERSIVE_DARK_MODE(20),

    /**
     * Window corner preference (Windows 11+)
     */
    DWMWA_WINDOW_CORNER_PREFERENCE(33),

    /**
     * Border color (Windows 11+)
     */
    DWMWA_BORDER_COLOR(34),

    /**
     * Caption color (Windows 11+)
     */
    DWMWA_CAPTION_COLOR(35),

    /**
     * Text color (Windows 11+)
     */
    DWMWA_TEXT_COLOR(36);

    private final int value;

    DwmWindowAttribute(int value) {
        this.value = value;
    }
}
