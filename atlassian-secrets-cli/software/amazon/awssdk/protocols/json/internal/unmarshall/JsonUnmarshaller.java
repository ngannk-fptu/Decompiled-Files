/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonUnmarshallerContext;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkInternalApi
public interface JsonUnmarshaller<T> {
    public T unmarshall(JsonUnmarshallerContext var1, JsonNode var2, SdkField<T> var3);
}

