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
            // Устанавливаем размер ПОСЛЕ того, как структура создана
            write();  // Убеждаемся что память выделена
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
     * {@link SystemStatus#LIMITED_SUITABILITY} if Windows 10,
     * {@link SystemStatus#NOT_SUITABLE} otherwise or on error.
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

            // Читаем данные из нативной памяти
            info.read();

            int major = info.dwMajorVersion.intValue();
            int build = info.dwBuildNumber.intValue();

            if (major == 10 && build >= 22000) {
                TitlebarChanger.LOGGER.debug("Detected Windows 11 (build {}).", build);
                return SystemStatus.SUITABLE;
            }
            if (major == 10) {
                TitlebarChanger.LOGGER.warn("Detected Windows 10 (build {}). Some features may be limited.", build);
                return SystemStatus.LIMITED_SUITABILITY;
            }

            TitlebarChanger.LOGGER.error("Unsupported Windows version: {}.{} (build {}). Requires Windows 10 or later.", major, info.dwMinorVersion.intValue(), build);
            return SystemStatus.NOT_SUITABLE;
        } catch (Exception e) {
            TitlebarChanger.LOGGER.error("Exception while checking Windows version: {}", e.getMessage(), e);
            return SystemStatus.NOT_SUITABLE;
        }
    }
}