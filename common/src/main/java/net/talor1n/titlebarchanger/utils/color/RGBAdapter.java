package net.talor1n.titlebarchanger.utils.color;

import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * JSON adapter для RGB класса
 */
public class RGBAdapter implements JsonSerializer<RGB>, JsonDeserializer<RGB> {

    @Override
    public RGB deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            try {
                return RGB.parse(json.getAsString());
            } catch (Exception e) {
                throw new JsonParseException("Failed to parse RGB: " + json.getAsString(), e);
            }
        }

        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (!obj.has("r") || !obj.has("g") || !obj.has("b")) {
                throw new JsonParseException("RGB object must have r, g, b fields");
            }

            int r = obj.get("r").getAsInt();
            int g = obj.get("g").getAsInt();
            int b = obj.get("b").getAsInt();

            return RGB.of(r, g, b);
        }

        throw new JsonParseException("Unsupported RGB format: " + json);
    }

    @Override
    public JsonElement serialize(RGB src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toHex());
    }
}