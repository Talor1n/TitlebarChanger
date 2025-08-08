package net.talor1n.titlebarchanger.api;

import net.talor1n.titlebarchanger.utils.color.RGB;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowAttribute;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowCornerPreference;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowThemeAttribute;
import net.talor1n.titlebarchanger.utils.win32.SystemStatus;

import static net.talor1n.titlebarchanger.utils.win32.DwmWindowThemeAttribute.*;
import static net.talor1n.titlebarchanger.utils.win32.SystemStatus.*;

/**
 * Main API interface for customizing Windows titlebar appearance and behavior.
 *
 * <p>This interface provides methods to modify various aspects of window titlebars
 * on Windows platforms, including corner rounding, dark mode, and color customization.
 * The API leverages Windows DWM (Desktop Window Manager) capabilities to achieve
 * these customizations.</p>
 *
 * <p><strong>Platform Support:</strong></p>
 * <ul>
 *   <li>Windows 11: Full support for all features</li>
 *   <li>Windows 10: Limited support (some features may not be available)</li>
 *   <li>Other platforms: Not supported</li>
 * </ul>
 *
 * @author talor1n
 * @since 1.0
 */
public interface TitlebarChangerApi {
    // ========================= PLATFORM CHECKS =========================

    /**
     * Performs a runtime check of the host operating system version to determine
     * feature availability and compatibility.
     *
     * @return {@link SystemStatus#SUITABLE} if running on Windows 11+ with full support,
     * {@link SystemStatus#LIMITED_SUITABILITY} if running on Windows 10 with partial support,
     * {@link SystemStatus#NOT_SUITABLE} for unsupported platforms
     */
    SystemStatus checkSystem();

    /**
     * Checks if the current platform is NOT Windows 11 or newer.
     *
     * <p>This method returns {@code true} for Windows 10, unsupported platforms,
     * or any system that doesn't have full Windows 11+ API support.</p>
     *
     * @return {@code true} if NOT running on Windows 11+ with full support,
     * {@code false} if running on Windows 11+ with complete feature set
     */
    default boolean isNotWindows11() {
        return !isWindows11();
    }

    /**
     * Checks if the current platform is Windows 11 or newer with full API support.
     *
     * @return {@code true} if running on Windows 11+ with complete feature set available,
     * {@code false} otherwise
     */
    default boolean isWindows11() {
        return checkSystem() == SUITABLE;
    }

    /**
     * Checks if the current platform is Windows 10 with limited API support.
     *
     * <p>On Windows 10, some features may not be available or may behave differently
     * compared to Windows 11.</p>
     *
     * @return {@code true} if running on Windows 10 with partial feature support,
     * {@code false} otherwise
     */
    default boolean isWindows10() {
        return checkSystem() == LIMITED_SUITABILITY;
    }

    /**
     * Checks if the current platform is unsupported (not Windows 10/11).
     *
     * @return {@code true} if running on an unsupported platform,
     * {@code false} if running on Windows 10 or 11
     */
    default boolean isOtherPlatform() {
        return checkSystem() == NOT_SUITABLE;
    }

    // ========================= WINDOW ATTRIBUTES =========================

    /**
     * Sets a specific DWM window attribute with the provided value.
     *
     * <p>This method provides low-level access to DWM window attributes,
     * allowing fine-grained control over window appearance and behavior.</p>
     *
     * @param attribute the DWM window attribute to modify
     * @param value     the integer value to set for the specified attribute
     * @return {@code true} if the attribute was successfully set,
     * {@code false} if the operation failed
     * @throws IllegalArgumentException if the attribute is null
     */
    boolean setWindowAttribute(DwmWindowAttribute attribute, int value);

    // ========================= CORNER PREFERENCES =========================

    /**
     * Sets the window corner rounding preference for the titlebar.
     *
     * @param cornerPreference the desired corner rounding style
     * @return {@code true} if the corner preference was successfully applied,
     * {@code false} if the operation failed
     * @throws IllegalArgumentException if cornerPreference is null
     */
    boolean setWindowCornerPreference(DwmWindowCornerPreference cornerPreference);

    /**
     * Sets the window corners to the system default rounding behavior.
     *
     * <p>This allows the system to determine the appropriate corner style
     * based on current system settings and theme.</p>
     *
     * @return {@code true} if the default corner style was successfully applied,
     * {@code false} if the operation failed
     */
    default boolean setSharpDefault() {
        return setWindowCornerPreference(DwmWindowCornerPreference.SYSTEM_DEFAULT);
    }

    /**
     * Disables corner rounding, creating sharp rectangular corners.
     *
     * @return {@code true} if sharp corners were successfully applied,
     * {@code false} if the operation failed
     */
    default boolean setSharpCorners() {
        return setWindowCornerPreference(DwmWindowCornerPreference.NO_ROUNDING);
    }

    /**
     * Enables standard rounded corners for the window.
     *
     * @return {@code true} if rounded corners were successfully applied,
     * {@code false} if the operation failed
     */
    default boolean setRoundedCorners() {
        return setWindowCornerPreference(DwmWindowCornerPreference.ROUNDED);
    }

    /**
     * Enables small rounded corners for a subtle rounded appearance.
     *
     * @return {@code true} if small rounded corners were successfully applied,
     * {@code false} if the operation failed
     */
    default boolean setSmallRoundedCorners() {
        return setWindowCornerPreference(DwmWindowCornerPreference.ROUNDED_SMALL);
    }

    // ========================= DARK MODE & THEME =========================

    /**
     * Enables or disables dark mode for the window titlebar.
     *
     * <p>Dark mode affects the titlebar's background color and controls,
     * switching between light and dark themes to match system preferences
     * or application-specific styling.</p>
     *
     * @param enabled {@code true} to enable dark mode, {@code false} to disable it
     * @return {@code true} if the dark mode setting was successfully applied,
     * {@code false} if the operation failed
     */
    boolean setDarkMode(DwmWindowThemeAttribute dwmWindowThemeAttribute);

    /**
     * Enables dark mode for the window titlebar.
     *
     * <p>This is a convenience method equivalent to calling {@code setDarkMode(true)}.</p>
     *
     * @return {@code true} if dark mode was successfully enabled,
     * {@code false} if the operation failed
     */
    default boolean enableDarkMode() {
        return setDarkMode(DARK);
    }

    /**
     * Enables light mode for the window titlebar.
     *
     * <p>This is a convenience method equivalent to calling {@code setDarkMode(false)}.</p>
     *
     * @return {@code true} if light mode was successfully enabled,
     * {@code false} if the operation failed
     */
    default boolean enableLightMode() {
        return setDarkMode(LIGHT);
    }

    // ========================= COLOR CUSTOMIZATION =========================

    /**
     * Sets a custom color for the window titlebar caption area.
     *
     * <p>The caption area includes the titlebar background where the window
     * title text is displayed. This method allows full RGB color customization
     * including transparency effects.</p>
     *
     * @param color the RGB color to apply to the caption area
     * @return {@code true} if the caption color was successfully set,
     * {@code false} if the operation failed
     * @throws IllegalArgumentException if color is null
     */
    boolean setCaptionColor(RGB color);

    /**
     * Sets a custom color for the window border.
     *
     * <p>This affects the thin border line that surrounds the entire window,
     * providing visual separation from other windows and the desktop background.</p>
     *
     * @param color the RGB color to apply to the window border
     * @return {@code true} if the border color was successfully set,
     * {@code false} if the operation failed
     * @throws IllegalArgumentException if color is null
     */
    boolean setBorderColor(RGB color);

    /**
     * Sets a custom color for the titlebar text.
     *
     * <p>This affects the window title text and potentially other text elements
     * within the titlebar area, such as control button labels if applicable.</p>
     *
     * @param color the RGB color to apply to the titlebar text
     * @return {@code true} if the text color was successfully set,
     * {@code false} if the operation failed
     * @throws IllegalArgumentException if color is null
     */
    boolean setTextColor(RGB color);

    boolean loadStyle();
}