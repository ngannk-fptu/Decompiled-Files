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
public final class ObjectJsonNode
implements JsonNode {
    private final Map<String, JsonNode> value;

    public ObjectJsonNode(Map<String, JsonNode> value) {
        this.value = value;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public String asNumber() {
        throw new UnsupportedOperationException("A JSON object cannot be converted to a number.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A JSON object cannot be converted to a string.");
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A JSON object cannot be converted to a boolean.");
    }

    @Override
    public List<JsonNode> asArray() {
        throw new UnsupportedOperationException("A JSON object cannot be converted to an array.");
    }

    @Override
    public Map<String, JsonNode> asObject() {
        return this.value;
    }

    @Override
    public <T> T visit(JsonNodeVisitor<T> visitor) {
        return visitor.visitObject(this.asObject());
    }

    @Override
    public Object asEmbeddedObject() {
        throw new UnsupportedOperationException("A JSON object cannot be converted to an embedded object.");
    }

    @Override
    public String text() {
        return null;
    }

    @Override
    public Optional<JsonNode> field(String child) {
        return Optional.ofNullable(this.value.get(child));
    }

    public String toString() {
        if (this.value.isEmpty()) {
            return "{}";
        }
        StringBuilder output = new StringBuilder();
        output.append("{");
        this.value.forEach((k, v) -> output.append("\"").append((String)k).append("\": ").append(v.toString()).append(","));
        output.setCharAt(output.length() - 1, '}');
        return output.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ObjectJsonNode that = (ObjectJsonNode)o;
        return this.value.equals(that.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }
}

