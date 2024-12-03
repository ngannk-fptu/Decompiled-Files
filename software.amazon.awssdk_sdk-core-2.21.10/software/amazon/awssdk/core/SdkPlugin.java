/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPreviewApi
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.SdkAutoCloseable
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

    default public void close() {
    }
}

