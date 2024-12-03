/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
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

    @Override
    public Future<Channel> acquire() {
        return this.delegate.acquire().addListener(this.onAcquire());
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        return this.delegate.acquire(promise).addListener(this.onAcquire());
    }

    private GenericFutureListener<Future<Channel>> onAcquire() {
        return future -> {
            if (future.isSuccess()) {
                ((Channel)future.getNow()).attr(IS_RELEASED).set(new AtomicBoolean(false));
            }
        };
    }

    @Override
    public Future<Void> release(Channel channel) {
        if (this.shouldRelease(channel)) {
            return this.delegate.release(channel);
        }
        return new SucceededFuture<Object>(channel.eventLoop(), null);
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        if (this.shouldRelease(channel)) {
            return this.delegate.release(channel, promise);
        }
        return promise.setSuccess(null);
    }

    private boolean shouldRelease(Channel channel) {
        return channel.attr(IS_RELEASED).get() == null || channel.attr(IS_RELEASED).get().compareAndSet(false, true);
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        return this.delegate.collectChannelPoolMetrics(metrics);
    }
}

