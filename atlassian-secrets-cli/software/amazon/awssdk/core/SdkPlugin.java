/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.SdkPreviewApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@FunctionalInterface
@SdkPreviewApi
@SdkPublicApi
@ThreadSafe
public interface SdkPlugin
extends SdkAutoCloseable {
    public void configureClient(SdkServiceClientConfiguration.Builder var1);

    @Override
    default public void close() {
    }
}

