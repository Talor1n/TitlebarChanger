package net.talor1n.titlebarchanger.win32;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.utils.win32.SystemStatus;

/**
 * Provides access to the native RtlGetVersion function in ntdll.dll
 * and a convenience method to check Windows version compatibility.
 */
public interface Ntdll extends StdCallLibrary {
    Ntdll INSTANCE = createInstance();

    // Минимальные требования для работы мода
    int MIN_WIN10_BUILD = 17763; // Windows 10 October 2018 Update (1809) - DWMWA_USE_IMMERSIVE_DARK_MODE
    int WIN11_BUILD_THRESHOLD = 22000; // Windows 11

    /**
     * Safe instance creation with error handling.
     *
     * @return Ntdll instance or null if loading fails
     */
    private static Ntdll createInstance() {
        try {
            Ntdll instance = Native.load("ntdll", Ntdll.class, W32APIOptions.DEFAULT_OPTIONS);
            TitlebarChanger.LOGGER.debug("Ntdll API loaded successfully");
            return instance;
        } catch (UnsatisfiedLinkError e) {
            TitlebarChanger.LOGGER.error("Failed to load Ntdll API: {}. Version checking will be unavailable.", e.getMessage());
            return null;
        } catch (Exception e) {
            TitlebarChanger.LOGGER.error("Unexpected error loading Ntdll API: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Data structure matching OSVERSIONINFOEXW from Windows API.
     * Holds version and build information for the operating system.
     */
    @Structure.FieldOrder({
            "dwOSVersionInfoSize",
            "dwMajorVersion",
            "dwMinorVersion",
            "dwBuildNumber",
            "dwPlatformId",
            "szCSDVersion",
            "wServicePackMajor",
            "wServicePackMinor",
            "wSuiteMask",
            "wProductType",
            "wReserved"
    })
    class OSVERSIONINFOEX extends Structure {
        /**
         * Size of this structure, in bytes.
         * ВАЖНО: Не вызывайте size() в конструкторе!
         */
        public WinDef.DWORD dwOSVersionInfoSize;
        /**
         * Major version number (e.g., 10).
         */
        public WinDef.DWORD dwMajorVersion;
        /**
         * Minor version number (e.g., 0).
         */
        public WinDef.DWORD dwMinorVersion;
        /**
         * Build number (e.g., 22000).
         */
        public WinDef.DWORD dwBuildNumber;
        /**
         * Platform ID (should be VER_PLATFORM_WIN32_NT).
         */
        public WinDef.DWORD dwPlatformId;
        /**
         * Service pack string (wide char, max 128 bytes).
         */
        public byte[] szCSDVersion = new byte[128];
        /**
         * Major version of installed service pack.
         */
        public WinDef.WORD wServicePackMajor;
        /**
         * Minor version of installed service pack.
         */
        public WinDef.WORD wServicePackMinor;
        /**
         * Bitmask identifying product suites.
         */
        public WinDef.WORD wSuiteMask;
        /**
         * Additional product information (e.g., server vs. workstation).
         */
        public byte wProductType;
        /**
         * Reserved for future use.
         */
        public byte wReserved;

        /**
         * Конструктор, который правильно инициализирует размер ПОСЛЕ создания структуры
         */
        public OSVERSIONINFOEX() {
            super();
            write();
            dwOSVersionInfoSize = new WinDef.DWORD(size());
        }
    }

    /**
     * Calls the native RtlGetVersion function to fill in the provided structure.
     *
     * @param osVersionInfo A pre-allocated OSVERSIONINFOEX instance.
     * @return Zero on success; non-zero error code on failure.
     */
    int RtlGetVersion(OSVERSIONINFOEX osVersionInfo);

    /**
     * Checks the current Windows version and returns a compatibility status.
     *
     * @return {@link SystemStatus#SUITABLE} if Windows 11 or later (build ≥ 22000),
     * {@link SystemStatus#LIMITED_SUITABILITY} if Windows 10 with build ≥ 17763,
     * {@link SystemStatus#NOT_SUITABLE} for older versions or non-Windows systems.
     */
    static SystemStatus checkWindowsVer() {
        if (INSTANCE == null) {
            TitlebarChanger.LOGGER.error("Ntdll API not available");
            return SystemStatus.NOT_SUITABLE;
        }

        try {
            OSVERSIONINFOEX info = new OSVERSIONINFOEX();
            int result = INSTANCE.RtlGetVersion(info);
            if (result != 0) {
                TitlebarChanger.LOGGER.error("Failed to retrieve OS version info (error code {}).", result);
                return SystemStatus.NOT_SUITABLE;
            }

            info.read();

            int major = info.dwMajorVersion.intValue();
            int minor = info.dwMinorVersion.intValue();
            int build = info.dwBuildNumber.intValue();

            if (major == 10 && build >= WIN11_BUILD_THRESHOLD) {
                TitlebarChanger.LOGGER.debug("Detected Windows 11 (build {}). Full feature support available.", build);
                return SystemStatus.SUITABLE;
            }

            if (major == 10 && build >= MIN_WIN10_BUILD) {
                TitlebarChanger.LOGGER.warn("Detected Windows 10 (build {}). Limited feature support - custom colors and advanced corner settings unavailable.", build);
                return SystemStatus.LIMITED_SUITABILITY;
            }

            if (major == 10) {
                TitlebarChanger.LOGGER.error("Detected Windows 10 (build {}) - too old. Requires Windows 10 build {} or later for titlebar theming support.", build, MIN_WIN10_BUILD);
                return SystemStatus.NOT_SUITABLE;
            }

            TitlebarChanger.LOGGER.error("Unsupported Windows version: {}.{} (build {}). Requires Windows 10 build {} or later.", major, minor, build, MIN_WIN10_BUILD);
            return SystemStatus.NOT_SUITABLE;
        } catch (Exception e) {
            TitlebarChanger.LOGGER.error("Exception while checking Windows version: {}", e.getMessage(), e);
            return SystemStatus.NOT_SUITABLE;
        }
    }
}