/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.protocols.jsoncore.internal;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeVisitor;

@SdkInternalApi
public final class EmbeddedObjectJsonNode
implements JsonNode {
    private final Object embeddedObject;

    public EmbeddedObjectJsonNode(Object embeddedObject) {
        this.embeddedObject = embeddedObject;
    }

    @Override
    public boolean isEmbeddedObject() {
        return true;
    }

    @Override
    public String asNumber() {
        throw new UnsupportedOperationException("A JSON embedded object cannot be converted to a number.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A JSON embedded object cannot be converted to a string.");
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A JSON embedded object cannot be converted to a boolean.");
    }

    @Override
    public List<JsonNode> asArray() {
        throw new UnsupportedOperationException("A JSON embedded object cannot be converted to an array.");
    }

    @Override
    public Map<String, JsonNode> asObject() {
        throw new UnsupportedOperationException("A JSON embedded object cannot be converted to an object.");
    }

    @Override
    public Object asEmbeddedObject() {
        return this.embeddedObject;
    }

    @Override
    public <T> T visit(JsonNodeVisitor<T> visitor) {
        return visitor.visitEmbeddedObject(this.asEmbeddedObject());
    }

    @Override
    public String text() {
        return null;
    }

    public String toString() {
        return "<<Embedded Object (" + this.embeddedObject.getClass().getSimpleName() + ")>>";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EmbeddedObjectJsonNode that = (EmbeddedObjectJsonNode)o;
        return this.embeddedObject.equals(that.embeddedObject);
    }

    public int hashCode() {
        return this.embeddedObject.hashCode();
    }
}

