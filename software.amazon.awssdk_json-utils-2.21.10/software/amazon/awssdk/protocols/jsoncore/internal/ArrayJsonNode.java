/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeVisitor;

@SdkInternalApi
public final class ArrayJsonNode
implements JsonNode {
    private final List<JsonNode> value;

    public ArrayJsonNode(List<JsonNode> value) {
        this.value = value;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public String asNumber() {
        throw new UnsupportedOperationException("A JSON array cannot be converted to a number.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A JSON array cannot be converted to a string.");
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A JSON array cannot be converted to a boolean.");
    }

    @Override
    public List<JsonNode> asArray() {
        return this.value;
    }

    @Override
    public Map<String, JsonNode> asObject() {
        throw new UnsupportedOperationException("A JSON array cannot be converted to an object.");
    }

    @Override
    public Object asEmbeddedObject() {
        throw new UnsupportedOperationException("A JSON array cannot be converted to an embedded object.");
    }

    @Override
    public <T> T visit(JsonNodeVisitor<T> visitor) {
        return visitor.visitArray(this.asArray());
    }

    @Override
    public String text() {
        return null;
    }

    @Override
    public Optional<JsonNode> index(int child) {
        if (child < 0 || child >= this.value.size()) {
            return Optional.empty();
        }
        return Optional.of(this.value.get(child));
    }

    public String toString() {
        return this.value.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ArrayJsonNode that = (ArrayJsonNode)o;
        return this.value.equals(that.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }
}

