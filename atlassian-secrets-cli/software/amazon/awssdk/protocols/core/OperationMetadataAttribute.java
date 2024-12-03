/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.core;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.AttributeMap;

@SdkProtectedApi
public final class OperationMetadataAttribute<T>
extends AttributeMap.Key<T> {
    public OperationMetadataAttribute(Class<T> valueType) {
        super(valueType);
    }
}

