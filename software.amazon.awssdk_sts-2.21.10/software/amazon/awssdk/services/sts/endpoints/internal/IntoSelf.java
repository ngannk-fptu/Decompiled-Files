/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.Into;

@SdkInternalApi
public interface IntoSelf<T extends IntoSelf<T>>
extends Into<T> {
    @Override
    default public T into() {
        return (T)this;
    }
}

