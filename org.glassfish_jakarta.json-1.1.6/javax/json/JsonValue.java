/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import javax.json.EmptyArray;
import javax.json.EmptyObject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValueImpl;

public interface JsonValue {
    public static final JsonObject EMPTY_JSON_OBJECT = new EmptyObject();
    public static final JsonArray EMPTY_JSON_ARRAY = new EmptyArray();
    public static final JsonValue NULL = new JsonValueImpl(ValueType.NULL);
    public static final JsonValue TRUE = new JsonValueImpl(ValueType.TRUE);
    public static final JsonValue FALSE = new JsonValueImpl(ValueType.FALSE);

    public ValueType getValueType();

    default public JsonObject asJsonObject() {
        return (JsonObject)JsonObject.class.cast(this);
    }

    default public JsonArray asJsonArray() {
        return (JsonArray)JsonArray.class.cast(this);
    }

    public String toString();

    public static enum ValueType {
        ARRAY,
        OBJECT,
        STRING,
        NUMBER,
        TRUE,
        FALSE,
        NULL;

    }
}

