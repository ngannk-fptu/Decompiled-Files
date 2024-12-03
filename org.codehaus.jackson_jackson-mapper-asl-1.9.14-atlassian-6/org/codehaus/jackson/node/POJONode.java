/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 */
package org.codehaus.jackson.node;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.node.ValueNode;

public final class POJONode
extends ValueNode {
    protected final Object _value;

    public POJONode(Object v) {
        this._value = v;
    }

    public JsonToken asToken() {
        return JsonToken.VALUE_EMBEDDED_OBJECT;
    }

    public boolean isPojo() {
        return true;
    }

    public byte[] getBinaryValue() throws IOException {
        if (this._value instanceof byte[]) {
            return (byte[])this._value;
        }
        return super.getBinaryValue();
    }

    public String asText() {
        return this._value == null ? "null" : this._value.toString();
    }

    public boolean asBoolean(boolean defaultValue) {
        if (this._value != null && this._value instanceof Boolean) {
            return (Boolean)this._value;
        }
        return defaultValue;
    }

    public int asInt(int defaultValue) {
        if (this._value instanceof Number) {
            return ((Number)this._value).intValue();
        }
        return defaultValue;
    }

    public long asLong(long defaultValue) {
        if (this._value instanceof Number) {
            return ((Number)this._value).longValue();
        }
        return defaultValue;
    }

    public double asDouble(double defaultValue) {
        if (this._value instanceof Number) {
            return ((Number)this._value).doubleValue();
        }
        return defaultValue;
    }

    public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (this._value == null) {
            jg.writeNull();
        } else {
            jg.writeObject(this._value);
        }
    }

    public Object getPojo() {
        return this._value;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        POJONode other = (POJONode)o;
        if (this._value == null) {
            return other._value == null;
        }
        return this._value.equals(other._value);
    }

    public int hashCode() {
        return this._value.hashCode();
    }

    public String toString() {
        return String.valueOf(this._value);
    }
}

