package net.talor1n.titlebarchanger.config;

import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
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
@Config(name = "titlebarchanger")
public class TitlebarChangerConfig implements ConfigData {

    /**
     * UI theme (e.g., 1 = dark, 2 = light, etc.).
     */
    @Builder.Default
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    private DwmWindowThemeAttribute theme = DwmWindowThemeAttribute.DARK;

    /**
     * Window corner rounding radius in pixels.
     */
    @Builder.Default
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    private DwmWindowCornerPreference corner = DwmWindowCornerPreference.DWMWCP_DEFAULT;

    /**
     * Title bar background color (HEX string or RGBA object).
     */
    @JsonAdapter(RGBAAdapter.class)
    @Builder.Default
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("colors")
    private RGBA captionColor = RGBA.of(5, 5, 5);

    /**
     * Title bar border color (HEX string or RGBA object).
     */
    @JsonAdapter(RGBAAdapter.class)
    @Builder.Default
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("colors")
    private RGBA borderColor = RGBA.of(0, 255, 0);

    /**
     * Title bar text color (HEX string or RGBA object).
     */
    @JsonAdapter(RGBAAdapter.class)
    @Builder.Default
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("colors")
    private RGBA textColor = RGBA.of(0, 255, 0);
}