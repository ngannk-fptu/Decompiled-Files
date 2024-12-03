/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.io.Serializable;
import javax.json.JsonValue;

final class JsonValueImpl
implements JsonValue,
Serializable {
    private final JsonValue.ValueType valueType;

    JsonValueImpl(JsonValue.ValueType valueType) {
        this.valueType = valueType;
    }

    @Override
    public JsonValue.ValueType getValueType() {
        return this.valueType;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JsonValue) {
            return this.getValueType().equals((Object)((JsonValue)obj).getValueType());
        }
        return false;
    }

    public int hashCode() {
        return this.valueType.hashCode();
    }

    @Override
    public String toString() {
        return this.valueType.name().toLowerCase();
    }
}

