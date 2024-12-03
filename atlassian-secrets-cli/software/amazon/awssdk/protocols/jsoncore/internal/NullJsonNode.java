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
public final class NullJsonNode
implements JsonNode {
    private static final NullJsonNode INSTANCE = new NullJsonNode();

    private NullJsonNode() {
    }

    public static NullJsonNode instance() {
        return INSTANCE;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String asNumber() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to a number.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to a string.");
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to a boolean.");
    }

    @Override
    public List<JsonNode> asArray() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to an array.");
    }

    @Override
    public Map<String, JsonNode> asObject() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to an object.");
    }

    @Override
    public Object asEmbeddedObject() {
        throw new UnsupportedOperationException("A JSON null cannot be converted to an embedded object.");
    }

    @Override
    public <T> T visit(JsonNodeVisitor<T> visitor) {
        return visitor.visitNull();
    }

    @Override
    public String text() {
        return null;
    }

    public String toString() {
        return "null";
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return 0;
    }
}

