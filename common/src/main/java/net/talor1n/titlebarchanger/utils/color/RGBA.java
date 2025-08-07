package net.talor1n.titlebarchanger.utils.color;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * Simplified color structure with validation support and multiple format compatibility (HEX, RGB, RGBA).
 * <p>
 * This class provides a comprehensive color representation with automatic validation
 * and conversion between different color formats commonly used in UI applications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class RGBA {

    /**
     * Red component [0-255]
     */
    @Setter(AccessLevel.NONE)
    private int r;

    /**
     * Green component [0-255]
     */
    @Setter(AccessLevel.NONE)
    private int g;

    /**
     * Blue component [0-255]
     */
    @Setter(AccessLevel.NONE)
    private int b;

    /**
     * Alpha component [0-255], defaults to 255 (fully opaque)
     */
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private int a = 255;

    /**
     * Factory method to create a color without alpha channel (alpha = 255).
     *
     * @param r red component [0-255]
     * @param g green component [0-255]
     * @param b blue component [0-255]
     * @return new RGBA instance with full opacity
     * @throws IllegalArgumentException if any component is out of [0-255] range
     */
    public static RGBA of(int r, int g, int b) {
        return of(r, g, b, 255);
    }

    /**
     * Factory method to create a color with specified alpha channel.
     *
     * @param r red component [0-255]
     * @param g green component [0-255]
     * @param b blue component [0-255]
     * @param a alpha component [0-255]
     * @return new RGBA instance
     * @throws IllegalArgumentException if any component is out of [0-255] range
     */
    public static RGBA of(int r, int g, int b, int a) {
        return new RGBA().setR(r).setG(g).setB(b).setA(a);
    }

    /**
     * Parses a color string in one of the supported formats:
     * <ul>
     *   <li><strong>HEX:</strong> "#RRGGBB", "RRGGBB", "#RRGGBBAA", "RRGGBBAA"</li>
     *   <li><strong>RGB:</strong> "r,g,b", "rgb(r,g,b)"</li>
     *   <li><strong>RGBA:</strong> "r,g,b,a", "rgba(r,g,b,a)"</li>
     * </ul>
     *
     * <p><strong>Examples:</strong></p>
     * <pre>
     * RGBA.parse("#FF0000")        // Red in HEX
     * RGBA.parse("255,0,0")        // Red in RGB
     * RGBA.parse("rgb(255,0,0)")   // Red in CSS RGB format
     * RGBA.parse("rgba(255,0,0,128)") // Red with 50% opacity
     * </pre>
     *
     * @param color the color string to parse
     * @return RGBA object representing the parsed color
     * @throws IllegalArgumentException if format is not supported or values are out of [0-255] range
     */
    public static RGBA parse(String color) {
        String trimmed = color.trim().toLowerCase();

        // HEX format detection
        if (trimmed.startsWith("#") || trimmed.matches("^[0-9a-f]{6}([0-9a-f]{2})?$")) {
            return fromHex(trimmed);
        }

        // RGB/RGBA format detection
        if (trimmed.contains("rgb")) {
            String nums = trimmed.replaceAll("rgba?\\(|\\)", "");
            String[] parts = nums.split(",");
            int[] vals = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                vals[i] = Integer.parseInt(parts[i].trim());
            }
            if (vals.length == 3) return of(vals[0], vals[1], vals[2]);
            if (vals.length == 4) return of(vals[0], vals[1], vals[2], vals[3]);
        }

        throw new IllegalArgumentException("Unsupported color format: " + color);
    }

    /**
     * Parses a HEX color string into RGBA object.
     *
     * @param hex HEX color string ("#RRGGBB", "RRGGBB", "#RRGGBBAA", "RRGGBBAA")
     * @return RGBA object
     * @throws IllegalArgumentException if HEX format is invalid
     */
    private static RGBA fromHex(String hex) {
        String s = hex.startsWith("#") ? hex.substring(1) : hex;
        if (s.length() != 6 && s.length() != 8) {
            throw new IllegalArgumentException("Invalid HEX color format: " + hex);
        }

        int r = Integer.parseInt(s.substring(0, 2), 16);
        int g = Integer.parseInt(s.substring(2, 4), 16);
        int b = Integer.parseInt(s.substring(4, 6), 16);
        int a = (s.length() == 8) ? Integer.parseInt(s.substring(6, 8), 16) : 255;

        return of(r, g, b, a);
    }

    /**
     * Converts color to HEX string format (alpha channel ignored).
     *
     * @return HEX color string in format "#RRGGBB"
     */
    public String toHex() {
        return String.format("#%02X%02X%02X", r, g, b);
    }

    /**
     * Converts color to HEX string format with alpha channel.
     *
     * @return HEX color string in format "#RRGGBBAA"
     */
    public String toHexWithAlpha() {
        return String.format("#%02X%02X%02X%02X", r, g, b, a);
    }

    /**
     * Converts color to CSS RGB string format.
     *
     * @return RGB color string in format "rgb(r, g, b)"
     */
    public String toRgb() {
        return String.format("rgb(%d, %d, %d)", r, g, b);
    }

    /**
     * Converts color to CSS RGBA string format.
     *
     * @return RGBA color string in format "rgba(r, g, b, a)"
     */
    public String toRgba() {
        return String.format("rgba(%d, %d, %d, %d)", r, g, b, a);
    }

    /**
     * Converts current RGB components to integer HEX format (0xRRGGBB).
     * <p>
     * This method is useful for APIs that expect integer color values,
     * such as many UI frameworks and graphics libraries.
     *
     * @return integer representation of the color (alpha ignored)
     */
    public int toHexInt() {
        return (r << 16) | (g << 8) | b;
    }

    /**
     * Static utility method to convert RGB values to integer HEX format (0xRRGGBB).
     * <p>
     * This is a convenience method for quick conversions without creating RGBA objects.
     *
     * @param r red component [0-255]
     * @param g green component [0-255]
     * @param b blue component [0-255]
     * @return integer representation of the color
     * @throws IllegalArgumentException if any value is out of [0-255] range
     */
    public static int rgbToHex(int r, int g, int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("RGB values must be between 0 and 255");
        }
        return (r << 16) | (g << 8) | b;
    }

    /**
     * Sets the red component with validation.
     *
     * @param r red component [0-255]
     * @return this instance for method chaining
     * @throws IllegalArgumentException if value is out of [0-255] range
     */
    public RGBA setR(int r) {
        this.r = validate(r, "red");
        return this;
    }

    /**
     * Sets the green component with validation.
     *
     * @param g green component [0-255]
     * @return this instance for method chaining
     * @throws IllegalArgumentException if value is out of [0-255] range
     */
    public RGBA setG(int g) {
        this.g = validate(g, "green");
        return this;
    }

    /**
     * Sets the blue component with validation.
     *
     * @param b blue component [0-255]
     * @return this instance for method chaining
     * @throws IllegalArgumentException if value is out of [0-255] range
     */
    public RGBA setB(int b) {
        this.b = validate(b, "blue");
        return this;
    }

    /**
     * Sets the alpha component with validation.
     *
     * @param a alpha component [0-255] where 0 is transparent and 255 is opaque
     * @return this instance for method chaining
     * @throws IllegalArgumentException if value is out of [0-255] range
     */
    public RGBA setA(int a) {
        this.a = validate(a, "alpha");
        return this;
    }

    /**
     * Validates that a color component value is within the valid range [0-255].
     *
     * @param value         the value to validate
     * @param componentName name of the component for error messages
     * @return the validated value
     * @throws IllegalArgumentException if value is out of [0-255] range
     */
    private static int validate(int value, String componentName) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(
                    String.format("%s component out of range [0-255]: %d", componentName, value)
            );
        }
        return value;
    }
}