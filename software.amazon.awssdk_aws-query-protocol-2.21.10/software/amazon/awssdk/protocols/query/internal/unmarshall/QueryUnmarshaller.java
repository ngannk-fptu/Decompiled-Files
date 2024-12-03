/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 */
package software.amazon.awssdk.protocols.query.internal.unmarshall;

import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshallerContext;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;

@SdkInternalApi
public interface QueryUnmarshaller<T> {
    public T unmarshall(QueryUnmarshallerContext var1, List<XmlElement> var2, SdkField<T> var3);
}

