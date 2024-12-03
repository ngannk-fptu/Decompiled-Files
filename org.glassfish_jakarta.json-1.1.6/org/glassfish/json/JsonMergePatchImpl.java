/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public final class JsonMergePatchImpl
implements JsonMergePatch {
    private JsonValue patch;

    public JsonMergePatchImpl(JsonValue patch) {
        this.patch = patch;
    }

    @Override
    public JsonValue apply(JsonValue target) {
        return JsonMergePatchImpl.mergePatch(target, this.patch);
    }

    @Override
    public JsonValue toJsonValue() {
        return this.patch;
    }

    private static JsonValue mergePatch(JsonValue target, JsonValue patch) {
        if (patch.getValueType() != JsonValue.ValueType.OBJECT) {
            return patch;
        }
        if (target.getValueType() != JsonValue.ValueType.OBJECT) {
            target = JsonValue.EMPTY_JSON_OBJECT;
        }
        JsonObject targetJsonObject = target.asJsonObject();
        JsonObjectBuilder builder = Json.createObjectBuilder(targetJsonObject);
        patch.asJsonObject().forEach((key, value) -> {
            if (value == JsonValue.NULL) {
                if (targetJsonObject.containsKey(key)) {
                    builder.remove((String)key);
                }
            } else if (targetJsonObject.containsKey(key)) {
                builder.add((String)key, JsonMergePatchImpl.mergePatch((JsonValue)targetJsonObject.get(key), value));
            } else {
                builder.add((String)key, JsonMergePatchImpl.mergePatch(JsonValue.EMPTY_JSON_OBJECT, value));
            }
        });
        return builder.build();
    }

    static JsonValue diff(JsonValue source, JsonValue target) {
        if (source.getValueType() != JsonValue.ValueType.OBJECT || target.getValueType() != JsonValue.ValueType.OBJECT) {
            return target;
        }
        JsonObject s = (JsonObject)source;
        JsonObject t = (JsonObject)target;
        JsonObjectBuilder builder = Json.createObjectBuilder();
        s.forEach((key, value) -> {
            if (t.containsKey(key)) {
                if (!value.equals(t.get(key))) {
                    builder.add((String)key, JsonMergePatchImpl.diff(value, (JsonValue)t.get(key)));
                }
            } else {
                builder.addNull((String)key);
            }
        });
        t.forEach((key, value) -> {
            if (!s.containsKey(key)) {
                builder.add((String)key, (JsonValue)value);
            }
        });
        return builder.build();
    }
}

