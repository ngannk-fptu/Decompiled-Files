/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.AttributeMap$Key
 */
package software.amazon.awssdk.core.internal.waiters;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.AttributeMap;

@SdkInternalApi
public class WaiterAttribute<T>
extends AttributeMap.Key<T> {
    public WaiterAttribute(Class<T> valueType) {
        super(valueType);
    }
}

