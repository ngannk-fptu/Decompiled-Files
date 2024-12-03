/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.Promise
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.metrics.MetricCollector
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public final class CancellableAcquireChannelPool
implements SdkChannelPool {
    private final EventExecutor executor;
    private final SdkChannelPool delegatePool;

    public CancellableAcquireChannelPool(EventExecutor executor, SdkChannelPool delegatePool) {
        this.executor = executor;
        this.delegatePool = delegatePool;
    }

    public Future<Channel> acquire() {
        return this.acquire((Promise<Channel>)this.executor.newPromise());
    }

    public Future<Channel> acquire(Promise<Channel> acquirePromise) {
        Future channelFuture = this.delegatePool.acquire(this.executor.newPromise());
        channelFuture.addListener(f -> {
            if (f.isSuccess()) {
                Channel ch = (Channel)f.getNow();
                if (!acquirePromise.trySuccess((Object)ch)) {
                    ch.close().addListener(closeFuture -> this.delegatePool.release(ch));
                }
            } else {
                acquirePromise.tryFailure(f.cause());
            }
        });
        return acquirePromise;
    }

    public Future<Void> release(Channel channel) {
        return this.delegatePool.release(channel);
    }

    public Future<Void> release(Channel channel, Promise<Void> promise) {
        return this.delegatePool.release(channel, promise);
    }

    public void close() {
        this.delegatePool.close();
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        return this.delegatePool.collectChannelPoolMetrics(metrics);
    }
}

