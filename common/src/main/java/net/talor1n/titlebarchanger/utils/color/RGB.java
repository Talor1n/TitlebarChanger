package net.talor1n.titlebarchanger.utils.color;

import com.google.gson.annotations.JsonAdapter;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Simple RGB color class without alpha channel.
 * <p>
 * Stores only red, green, and blue components (0–255).
 * Provides parsing from HEX strings and conversion to/from Windows BGR integer format.
 * <p>
 * Supported formats:
 * <ul>
 *   <li>HEX: {@code "#RRGGBB"} or {@code "RRGGBB"}</li>
 *   <li>Windows BGR (COLORREF): {@code 0x00BBGGRR}</li>
 * </ul>
 * Alpha channel is not supported and never stored.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
@JsonAdapter(RGBAdapter.class)
public class RGB {
    /** Red component [0–255]. */
    private int r;

    /** Green component [0–255]. */
    private int g;

    /** Blue component [0–255]. */
    private int b;

    /**
     * Creates a new RGB instance with values clamped to [0–255].
     *
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return RGB instance
     */
    public static RGB of(int r, int g, int b) {
        return new RGB(clamp(r), clamp(g), clamp(b));
    }
    /**
     * Creates a new RGB instance from a HEX integer {@code 0xRRGGBB}.
     * Alpha channel is not supported and will be ignored if present.
     *
     * @param hexInt RGB color as integer in format {@code 0xRRGGBB}
     * @return RGB instance
     */
    public static RGB of(int hexInt) {
        int r = (hexInt >> 16) & 0xFF;
        int g = (hexInt >> 8) & 0xFF;
        int b = hexInt & 0xFF;
        return of(r, g, b);
    }
    /**
     * Parses a HEX string in the format {@code "#RRGGBB"} or {@code "RRGGBB"}.
     *
     * @param hex HEX color string
     * @return RGB instance
     * @throws IllegalArgumentException if string is null, empty, wrong length, or invalid hex
     */
    public static RGB parse(String hex) {
        if (hex == null || hex.isEmpty()) {
            throw new IllegalArgumentException("HEX string cannot be null or empty");
        }
        String s = hex.startsWith("#") ? hex.substring(1) : hex;
        if (s.length() != 6) {
            throw new IllegalArgumentException("Invalid HEX color: " + hex);
        }
        try {
            int r = Integer.parseInt(s.substring(0, 2), 16);
            int g = Integer.parseInt(s.substring(2, 4), 16);
            int b = Integer.parseInt(s.substring(4, 6), 16);
            return of(r, g, b);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid HEX color format: " + hex, e);
        }
    }

    /**
     * Converts this RGB color to a HEX string in the format {@code "#RRGGBB"}.
     *
     * @return HEX color string
     */
    public String toHex() {
        if (isEmpty()) {
            return "-1";
        }
        return String.format("#%02X%02X%02X", clamp(r), clamp(g), clamp(b));
    }
    /**
     * Converts this RGB color to a HEX integer in the format {@code 0xRRGGBB}.
     * Alpha channel is not included.
     *
     * @return RGB color as an integer
     */
    public int toHexInt() {
        if (isEmpty()) {
            return -1; // 0xFFFFFFFF
        }
        return (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
    }
    /**
     * Converts this RGB color to Windows BGR (COLORREF) format {@code 0x00BBGGRR}.
     *
     * @return Windows BGR integer
     */
    public int toWindowsInt() {
        if (isEmpty()) {
            return -1; // DWM COLOR_DEFAULT
        }
        int red = clamp(r);
        int green = clamp(g);
        int blue = clamp(b);
        return (blue << 16) | (green << 8) | red;
    }

    /**
     * Creates RGB from Windows BGR (COLORREF) format {@code 0x00BBGGRR}.
     *
     * @param bgr Windows BGR integer
     * @return RGB instance
     */
    public static RGB fromWindowsInt(int bgr) {
        int r = bgr & 0xFF;
        int g = (bgr >> 8) & 0xFF;
        int b = (bgr >> 16) & 0xFF;
        return of(r, g, b);
    }

    /**
     * Checks whether all components are within the valid range [0–255].
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return r >= 0 && r <= 255 &&
                g >= 0 && g <= 255 &&
                b >= 0 && b <= 255;
    }

    /**
     * Returns a new RGB instance with all values clamped to [0–255].
     *
     * @return normalized RGB
     */
    public RGB normalize() {
        return RGB.of(clamp(r), clamp(g), clamp(b));
    }

    /**
     * Ensures a value is within the range [0–255].
     */
    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public boolean isEmpty() {
        return r == -1 && g == -1 && b == -1;
    }

    public static final RGB BLACK = RGB.of(0,0,0);
    public static final RGB EMPTY = new RGB(-1, -1, -1);
}
