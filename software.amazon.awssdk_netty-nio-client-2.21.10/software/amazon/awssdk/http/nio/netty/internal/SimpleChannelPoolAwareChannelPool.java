/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.Promise
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.metrics.MetricCollector
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.BetterSimpleChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
final class SimpleChannelPoolAwareChannelPool
implements SdkChannelPool {
    private final SdkChannelPool delegate;
    private final BetterSimpleChannelPool simpleChannelPool;

    SimpleChannelPoolAwareChannelPool(SdkChannelPool delegate, BetterSimpleChannelPool simpleChannelPool) {
        this.delegate = delegate;
        this.simpleChannelPool = simpleChannelPool;
    }

    public Future<Channel> acquire() {
        return this.delegate.acquire();
    }

    public Future<Channel> acquire(Promise<Channel> promise) {
        return this.delegate.acquire(promise);
    }

    public Future<Void> release(Channel channel) {
        return this.delegate.release(channel);
    }

    public Future<Void> release(Channel channel, Promise<Void> promise) {
        return this.delegate.release(channel, promise);
    }

    public void close() {
        this.delegate.close();
    }

    public BetterSimpleChannelPool underlyingSimpleChannelPool() {
        return this.simpleChannelPool;
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        return this.delegate.collectChannelPoolMetrics(metrics);
    }
}

