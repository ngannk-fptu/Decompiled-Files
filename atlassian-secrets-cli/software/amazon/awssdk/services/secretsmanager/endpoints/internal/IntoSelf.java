/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Into;

@SdkInternalApi
public interface IntoSelf<T extends IntoSelf<T>>
extends Into<T> {
    @Override
    default public T into() {
        return (T)this;
    }
}

