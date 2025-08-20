package net.talor1n.titlebarchanger.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.talor1n.titlebarchanger.utils.ConfigComment;
import net.talor1n.titlebarchanger.utils.color.RGB;
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
public class TitlebarConfig {

    @ConfigComment(value = {
            "Window Theme: LIGHT (0), DARK (1), CUSTOM (2)",
            "Controls the overall appearance of the titlebar"
    }, restriction = "Custom only works on Windows 11+")
    @Builder.Default
    private DwmWindowThemeAttribute theme = DwmWindowThemeAttribute.DARK;

    @ConfigComment(value = {
            "Window Corners: SYSTEM_DEFAULT (0), NO_ROUNDING (1), ROUNDED (2), ROUNDED_SMALL (3)",
            "Controls how window corners are rendered"
    }, restriction = "Only works on Windows 11+")
    @Builder.Default
    private DwmWindowCornerPreference corner = DwmWindowCornerPreference.SYSTEM_DEFAULT;

    @ConfigComment(value = {
            "Caption (titlebar) background color",
            "Format: #RRGGBB"
    }, restriction = "Only works when theme is CUSTOM (2)")
    @Builder.Default
    private RGB captionColor = RGB.of(5, 5, 5);

    @ConfigComment(value = {
            "Window border color",
            "Format: #RRGGBB"
    }, restriction = "Only works when theme is CUSTOM (2)")
    @Builder.Default
    private RGB borderColor = RGB.of(0, 255, 0);

    @ConfigComment(value = {
            "Title text and button color",
            "Format: #RRGGBB"
    }, restriction = "Only works when theme is CUSTOM (2)")
    @Builder.Default
    private RGB textColor = RGB.of(0, 255, 0);

}