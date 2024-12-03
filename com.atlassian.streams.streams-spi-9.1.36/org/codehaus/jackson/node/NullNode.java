/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.node;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.node.ValueNode;

public final class NullNode
extends ValueNode {
    public static final NullNode instance = new NullNode();

    private NullNode() {
    }

    public static NullNode getInstance() {
        return instance;
    }

    public JsonToken asToken() {
        return JsonToken.VALUE_NULL;
    }

    public boolean isNull() {
        return true;
    }

    public String asText() {
        return "null";
    }

    public int asInt(int defaultValue) {
        return 0;
    }

    public long asLong(long defaultValue) {
        return 0L;
    }

    public double asDouble(double defaultValue) {
        return 0.0;
    }

    public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeNull();
    }

    public boolean equals(Object o) {
        return o == this;
    }
}

