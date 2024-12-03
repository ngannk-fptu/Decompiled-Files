/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkPublicApi
@ThreadSafe
public interface SdkClient
extends SdkAutoCloseable {
    public String serviceName();

    default public SdkServiceClientConfiguration serviceClientConfiguration() {
        throw new UnsupportedOperationException();
    }
}

