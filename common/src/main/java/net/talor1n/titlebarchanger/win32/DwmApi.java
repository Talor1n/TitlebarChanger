package net.talor1n.titlebarchanger.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowAttribute;

/**
 * JNA interface for Windows Desktop Window Manager (DWM) API.
 * <p>
 * This interface provides access to DWM functions for customizing window appearance,
 * including title bar colors, window effects, and other visual attributes in Windows 10/11.
 *
 * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/dwmapi/">DWM API Documentation</a>
 */
public interface DwmApi extends StdCallLibrary {
    DwmApi INSTANCE = createInstance();

    /**
     * Sets a window attribute using the DWM API.
     * <p>
     * This is the core function for modifying DWM window attributes such as
     * title bar color, corner preferences, and visual effects.
     *
     * @param hwnd        window handle to modify
     * @param dwAttribute attribute identifier (see {@link DwmWindowAttribute})
     * @param pvAttribute pointer to attribute value data
     * @param cbAttribute size of attribute data in bytes
     * @return HRESULT indicating success (S_OK) or failure
     */
    WinNT.HRESULT DwmSetWindowAttribute(
            WinDef.HWND hwnd,
            WinDef.DWORD dwAttribute,
            WinDef.LPVOID pvAttribute,
            WinDef.DWORD cbAttribute
    );

    /**
     * Safe instance creation with error handling.
     *
     * @return DwmApi instance or null if loading fails
     */
    private static DwmApi createInstance() {
        try {
            DwmApi instance = Native.load("dwmapi", DwmApi.class, W32APIOptions.DEFAULT_OPTIONS);
            TitlebarChanger.LOGGER.info("DWM API loaded successfully");
            return instance;
        } catch (UnsatisfiedLinkError e) {
            TitlebarChanger.LOGGER.error("Failed to load DWM API: {}. DWM features will be unavailable.", e.getMessage());
            return null;
        } catch (Exception e) {
            TitlebarChanger.LOGGER.error("Unexpected error loading DWM API: {}", e.getMessage(), e);
            return null;
        }
    }
}