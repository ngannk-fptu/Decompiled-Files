/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.pool.ChannelPool
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.metrics.MetricCollector
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.pool.ChannelPool;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public interface SdkChannelPool
extends ChannelPool {
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector var1);
}

