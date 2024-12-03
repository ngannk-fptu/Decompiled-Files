/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeVisitor;

@SdkInternalApi
public final class BooleanJsonNode
implements JsonNode {
    private final boolean value;

    public BooleanJsonNode(boolean value) {
        this.value = value;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public String asNumber() {
        throw new UnsupportedOperationException("A JSON boolean cannot be converted to a number.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A JSON boolean cannot be converted to a string.");
    }

    @Override
    public boolean asBoolean() {
        return this.value;
    }

    @Override
    public List<JsonNode> asArray() {
        throw new UnsupportedOperationException("A JSON boolean cannot be converted to an array.");
    }

    @Override
    public Map<String, JsonNode> asObject() {
        throw new UnsupportedOperationException("A JSON boolean cannot be converted to an object.");
    }

    @Override
    public Object asEmbeddedObject() {
        throw new UnsupportedOperationException("A JSON boolean cannot be converted to an embedded object.");
    }

    @Override
    public <T> T visit(JsonNodeVisitor<T> visitor) {
        return visitor.visitBoolean(this.asBoolean());
    }

    @Override
    public String text() {
        return Boolean.toString(this.value);
    }

    public String toString() {
        return Boolean.toString(this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BooleanJsonNode that = (BooleanJsonNode)o;
        return this.value == that.value;
    }

    public int hashCode() {
        return this.value ? 1 : 0;
    }
}

