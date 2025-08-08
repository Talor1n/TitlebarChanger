package net.talor1n.titlebarchanger.config;

import lombok.*;
import lombok.experimental.Accessors;
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

    @Builder.Default
    private DwmWindowThemeAttribute theme = DwmWindowThemeAttribute.DARK;

    @Builder.Default
    private DwmWindowCornerPreference corner = DwmWindowCornerPreference.SYSTEM_DEFAULT;

    @Builder.Default
    private RGB captionColor = RGB.of(5, 5, 5);

    @Builder.Default
    private RGB borderColor = RGB.of(0, 255, 0);

    @Builder.Default
    private RGB textColor = RGB.of(0, 255, 0);
}