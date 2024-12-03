/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.jsoncore;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkProtectedApi
public interface JsonNodeVisitor<T> {
    public T visitNull();

    public T visitBoolean(boolean var1);

    public T visitNumber(String var1);

    public T visitString(String var1);

    public T visitArray(List<JsonNode> var1);

    public T visitObject(Map<String, JsonNode> var1);

    public T visitEmbeddedObject(Object var1);
}

