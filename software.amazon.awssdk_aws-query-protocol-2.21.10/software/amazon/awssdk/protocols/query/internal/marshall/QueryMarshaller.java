/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.protocols.core.Marshaller
 */
package software.amazon.awssdk.protocols.query.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.protocols.core.Marshaller;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshallerContext;

@FunctionalInterface
@SdkInternalApi
public interface QueryMarshaller<T>
extends Marshaller<T> {
    public void marshall(QueryMarshallerContext var1, String var2, T var3, SdkField<T> var4);
}

