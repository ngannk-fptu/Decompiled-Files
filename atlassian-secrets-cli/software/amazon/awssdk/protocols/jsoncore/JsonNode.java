/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.jsoncore;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeVisitor;
import software.amazon.awssdk.protocols.jsoncore.internal.ObjectJsonNode;

@SdkProtectedApi
public interface JsonNode {
    public static JsonNodeParser parser() {
        return JsonNodeParser.create();
    }

    public static JsonNodeParser.Builder parserBuilder() {
        return JsonNodeParser.builder();
    }

    public static JsonNode emptyObjectNode() {
        return new ObjectJsonNode(Collections.emptyMap());
    }

    default public boolean isNumber() {
        return false;
    }

    default public boolean isString() {
        return false;
    }

    default public boolean isBoolean() {
        return false;
    }

    default public boolean isNull() {
        return false;
    }

    default public boolean isArray() {
        return false;
    }

    default public boolean isObject() {
        return false;
    }

    default public boolean isEmbeddedObject() {
        return false;
    }

    public String asNumber();

    public String asString();

    public boolean asBoolean();

    public List<JsonNode> asArray();

    public Map<String, JsonNode> asObject();

    public Object asEmbeddedObject();

    public <T> T visit(JsonNodeVisitor<T> var1);

    public String text();

    default public Optional<JsonNode> field(String child) {
        return Optional.empty();
    }

    default public Optional<JsonNode> index(int child) {
        return Optional.empty();
    }
}

