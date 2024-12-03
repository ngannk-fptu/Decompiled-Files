/*
 * Decompiled with CFR 0.152.
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

