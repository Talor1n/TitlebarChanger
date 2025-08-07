package net.talor1n.titlebarchanger.config;

import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.talor1n.titlebarchanger.utils.color.RGBA;
import net.talor1n.titlebarchanger.utils.color.RGBAAdapter;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowCornerPreference;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowThemeAttribute;

/**
 * Primary configuration model for TitlebarChanger.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Config {
    /**
     * UI theme (e.g., 1 = dark, 2 = light, etc.).
     */
    @Builder.Default
    private DwmWindowThemeAttribute theme = DwmWindowThemeAttribute.DARK;

    /**
     * Window corner rounding radius in pixels.
     */
    @Builder.Default
    private DwmWindowCornerPreference corner = DwmWindowCornerPreference.DWMWCP_DEFAULT;

    /**
     * Title bar background color (HEX string or RGBA object).
     */
    @JsonAdapter(RGBAAdapter.class)
    @Builder.Default
    private RGBA captionColor = RGBA.of(5, 5, 5);

    /**
     * Title bar text color (HEX string or RGBA object).
     */
    @JsonAdapter(RGBAAdapter.class)
    @Builder.Default
    private RGBA borderColor = RGBA.of(0, 255, 0);

    /**
     * Title bar border (stroke) color (HEX string or RGBA object).
     */
    @JsonAdapter(RGBAAdapter.class)
    @Builder.Default
    private RGBA textColor = RGBA.of(0, 255, 0);

    /**
     * Whether to display the menu.
     */
    @Builder.Default
    private boolean showTheMenu = true;

    /**
     * Whether to display the warning screen.
     */
    @Builder.Default
    private boolean showWarnScreen = true;
}