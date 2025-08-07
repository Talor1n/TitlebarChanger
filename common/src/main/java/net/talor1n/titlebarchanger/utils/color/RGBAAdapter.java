package net.talor1n.titlebarchanger.utils.color;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * JSON adapter for serialization and deserialization of {@link RGBA} objects.
 * <p>
 * This adapter provides flexible JSON handling for RGBA color objects, supporting
 * multiple input formats while maintaining a consistent, compact output format.
 *
 * <h3>Serialization</h3>
 * Colors are always serialized to compact HEX format: {@code "#RRGGBB"}
 * (alpha channel is ignored in output for compatibility).
 *
 * <h3>Deserialization</h3>
 * The following input formats are supported:
 * <ul>
 *   <li><strong>HEX strings:</strong> {@code "#RRGGBB"}, {@code "RRGGBB"},
 *       {@code "#RRGGBBAA"}, {@code "RRGGBBAA"}</li>
 *   <li><strong>CSS RGB/RGBA strings:</strong> {@code "rgb(r,g,b)"}, {@code "rgba(r,g,b,a)"}</li>
 *   <li><strong>Comma-separated values:</strong> {@code "r,g,b"}, {@code "r,g,b,a"}</li>
 *   <li><strong>JSON objects:</strong> with {@code r}, {@code g}, {@code b} fields
 *       and optional {@code a} field</li>
 * </ul>
 *
 * <h3>Usage Example</h3>
 * <pre>
 * Gson gson = new GsonBuilder()
 *     .registerTypeAdapter(RGBA.class, new RGBAAdapter())
 *     .create();
 *
 * // All these JSON inputs will be parsed correctly:
 * gson.fromJson("\"#FF0000\"", RGBA.class);           // HEX string
 * gson.fromJson("\"rgb(255,0,0)\"", RGBA.class);      // CSS RGB
 * gson.fromJson("{\"r\":255,\"g\":0,\"b\":0}", RGBA.class); // JSON object
 *
 * // Output is always compact HEX:
 * gson.toJson(RGBA.of(255, 0, 0)); // Returns "#FF0000"
 * </pre>
 *
 * @author Talor1n
 * @version 1.0
 * @see RGBA
 * @since 1.0
 */
public class RGBAAdapter implements JsonSerializer<RGBA>, JsonDeserializer<RGBA> {

    /**
     * Deserializes a JSON element into an RGBA object.
     * <p>
     * This method handles multiple input formats automatically by delegating
     * to the appropriate parsing method based on the JSON element type.
     *
     * @param json    the JSON element to deserialize
     * @param typeOfT the type of the target object (RGBA.class)
     * @param context the deserialization context
     * @return RGBA object parsed from the JSON input
     * @throws JsonParseException if the JSON format is unsupported or contains invalid values
     */
    @Override
    public RGBA deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        // Handle string formats (HEX, RGB, RGBA)
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            try {
                return RGBA.parse(json.getAsString());
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Failed to parse color string: " + json.getAsString(), e);
            }
        }

        // Handle JSON object format
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();

            // Validate required fields
            if (!obj.has("r") || !obj.has("g") || !obj.has("b")) {
                throw new JsonParseException("JSON object must contain 'r', 'g', and 'b' fields: " + json);
            }

            try {
                int r = obj.get("r").getAsInt();
                int g = obj.get("g").getAsInt();
                int b = obj.get("b").getAsInt();
                int a = obj.has("a") ? obj.get("a").getAsInt() : 255;

                return RGBA.of(r, g, b, a);
            } catch (NumberFormatException e) {
                throw new JsonParseException("Invalid numeric values in color object: " + json, e);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Color component values out of range [0-255]: " + json, e);
            }
        }

        throw new JsonParseException("Unsupported JSON format for RGBA color: " + json);
    }

    /**
     * Serializes an RGBA object to a JSON element.
     * <p>
     * The output is always a compact HEX string in the format {@code "#RRGGBB"}.
     * The alpha channel is ignored for compatibility with most color systems
     * that expect RGB values.
     *
     * @param src       the RGBA object to serialize
     * @param typeOfSrc the type of the source object
     * @param context   the serialization context
     * @return JSON primitive containing the HEX color string
     */
    @Override
    public JsonElement serialize(RGBA src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toHex());
    }
}
