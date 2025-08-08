package net.talor1n.titlebarchanger.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import net.talor1n.titlebarchanger.TitlebarChanger;

/**
 * JNA interface for Windows Desktop Window Manager (DWM) API.
 * Provides access to DWM functions for customizing window appearance.
 */
public interface DwmApi extends StdCallLibrary {
    DwmApi INSTANCE = createInstance();

    WinNT.HRESULT DwmSetWindowAttribute(
            WinDef.HWND hwnd,
            WinDef.DWORD dwAttribute,
            WinDef.LPVOID pvAttribute,
            WinDef.DWORD cbAttribute
    );

    /** Flushes the DWN composition queue. */
    WinNT.HRESULT DwmFlush();

    /**
     * Minimal refresh for non-client area (titlebar/borders) — useful on Windows 10
     * after calling DwmSetWindowAttribute, when changes don't repaint immediately.
     *
     * Does:
     * 1) DwmFlush()
     * 2) SetWindowPos(..., SWP_FRAMECHANGED | SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER)
     *
     * @param hwnd target window handle
     */
    static void refreshNonClient(WinDef.HWND hwnd) {
        if (hwnd == null || Pointer.nativeValue(hwnd.getPointer()) == 0) return;
        try {
            if (INSTANCE != null) {
                INSTANCE.DwmFlush(); // sync with compositor
            }
        } catch (Throwable t) {
            TitlebarChanger.LOGGER.debug("DwmFlush failed: {}", t.toString());
        }

        final int SWP_NOMOVE       = 0x0002;
        final int SWP_NOSIZE       = 0x0001;
        final int SWP_NOZORDER     = 0x0004;
        final int SWP_FRAMECHANGED = 0x0020;

        try {
            User32.INSTANCE.SetWindowPos(
                    hwnd,
                    null,
                    0, 0, 0, 0,
                    SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER | SWP_FRAMECHANGED
            );
        } catch (Throwable t) {
            TitlebarChanger.LOGGER.debug("SetWindowPos(SWP_FRAMECHANGED) failed: {}", t.toString());
        }
    }

    /** Safe instance creation with error handling. */
    private static DwmApi createInstance() {
        try {
            DwmApi instance = Native.load("dwmapi", DwmApi.class, W32APIOptions.DEFAULT_OPTIONS);
            TitlebarChanger.LOGGER.debug("DWM API loaded successfully");
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
