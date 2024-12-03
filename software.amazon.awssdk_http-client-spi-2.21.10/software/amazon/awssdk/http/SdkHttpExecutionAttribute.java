/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.AttributeMap$Key
 *  software.amazon.awssdk.utils.AttributeMap$Key$UnsafeValueType
 */
package software.amazon.awssdk.http;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.AttributeMap;

@SdkPublicApi
public abstract class SdkHttpExecutionAttribute<T>
extends AttributeMap.Key<T> {
    protected SdkHttpExecutionAttribute(Class<T> valueType) {
        super(valueType);
    }

    protected SdkHttpExecutionAttribute(AttributeMap.Key.UnsafeValueType unsafeValueType) {
        super(unsafeValueType);
    }
}

