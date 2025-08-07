package net.talor1n.titlebarchanger.utils.win32;

/**
 * Compatibility statuses for the operating system.
 */
public enum SystemStatus {
    /**
     * Fully supported (Windows 11 or later).
     */
    SUITABLE,

    /**
     * Partially supported (Windows 10).
     */
    LIMITED_SUITABILITY,

    /**
     * Not supported (earlier than Windows 10 or non-Windows platforms).
     */
    NOT_SUITABLE
}
