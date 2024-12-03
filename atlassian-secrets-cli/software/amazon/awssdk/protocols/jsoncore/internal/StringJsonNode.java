/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeVisitor;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class StringJsonNode
implements JsonNode {
    private final String value;

    public StringJsonNode(String value) {
        Validate.paramNotNull(value, "value");
        this.value = value;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String asNumber() {
        throw new UnsupportedOperationException("A JSON string cannot be converted to a number.");
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A JSON string cannot be converted to a boolean.");
    }

    @Override
    public List<JsonNode> asArray() {
        throw new UnsupportedOperationException("A JSON string cannot be converted to an array.");
    }

    @Override
    public Map<String, JsonNode> asObject() {
        throw new UnsupportedOperationException("A JSON string cannot be converted to an object.");
    }

    @Override
    public Object asEmbeddedObject() {
        throw new UnsupportedOperationException("A JSON string cannot be converted to an embedded object.");
    }

    @Override
    public <T> T visit(JsonNodeVisitor<T> visitor) {
        return visitor.visitString(this.asString());
    }

    @Override
    public String text() {
        return this.value;
    }

    public String toString() {
        return "\"" + this.value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        StringJsonNode that = (StringJsonNode)o;
        return this.value.equals(that.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }
}

