/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.protocols.core.Marshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshallerContext;

@FunctionalInterface
@SdkInternalApi
public interface JsonMarshaller<T>
extends Marshaller<T> {
    public static final JsonMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {};

    public void marshall(T var1, JsonMarshallerContext var2, String var3, SdkField<T> var4);
}

