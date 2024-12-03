/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.protocols.core.Marshaller
 */
package software.amazon.awssdk.protocols.xml.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.protocols.core.Marshaller;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshallerContext;

@FunctionalInterface
@SdkInternalApi
public interface XmlMarshaller<T>
extends Marshaller<T> {
    public static final XmlMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {};

    public void marshall(T var1, XmlMarshallerContext var2, String var3, SdkField<T> var4);
}

