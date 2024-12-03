/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.util.AttributeKey
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.concurrent.SucceededFuture
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.metrics.MetricCollector
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.SucceededFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public class ReleaseOnceChannelPool
implements SdkChannelPool {
    private static final AttributeKey<AtomicBoolean> IS_RELEASED = NettyUtils.getOrCreateAttributeKey("software.amazon.awssdk.http.nio.netty.internal.http2.ReleaseOnceChannelPool.isReleased");
    private final SdkChannelPool delegate;

    public ReleaseOnceChannelPool(SdkChannelPool delegate) {
        this.delegate = delegate;
    }

    public Future<Channel> acquire() {
        return this.delegate.acquire().addListener(this.onAcquire());
    }

    public Future<Channel> acquire(Promise<Channel> promise) {
        return this.delegate.acquire(promise).addListener(this.onAcquire());
    }

    private GenericFutureListener<Future<Channel>> onAcquire() {
        return future -> {
            if (future.isSuccess()) {
                ((Channel)future.getNow()).attr(IS_RELEASED).set((Object)new AtomicBoolean(false));
            }
        };
    }

    public Future<Void> release(Channel channel) {
        if (this.shouldRelease(channel)) {
            return this.delegate.release(channel);
        }
        return new SucceededFuture((EventExecutor)channel.eventLoop(), null);
    }

    public Future<Void> release(Channel channel, Promise<Void> promise) {
        if (this.shouldRelease(channel)) {
            return this.delegate.release(channel, promise);
        }
        return promise.setSuccess(null);
    }

    private boolean shouldRelease(Channel channel) {
        return channel.attr(IS_RELEASED).get() == null || ((AtomicBoolean)channel.attr(IS_RELEASED).get()).compareAndSet(false, true);
    }

    public void close() {
        this.delegate.close();
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        return this.delegate.collectChannelPoolMetrics(metrics);
    }
}

