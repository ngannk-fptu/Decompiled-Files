/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.AttributeMap$Key
 *  software.amazon.awssdk.utils.AttributeMap$Key$UnsafeValueType
 */
package software.amazon.awssdk.core.client.config;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.AttributeMap;

@SdkProtectedApi
public abstract class ClientOption<T>
extends AttributeMap.Key<T> {
    protected ClientOption(Class<T> valueClass) {
        super(valueClass);
    }

    protected ClientOption(AttributeMap.Key.UnsafeValueType unsafeValueType) {
        super(unsafeValueType);
    }
}

