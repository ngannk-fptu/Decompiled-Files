/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.metrics;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@ThreadSafe
@SdkPublicApi
public interface MetricPublisher
extends SdkAutoCloseable {
    public void publish(MetricCollection var1);

    public void close();
}

