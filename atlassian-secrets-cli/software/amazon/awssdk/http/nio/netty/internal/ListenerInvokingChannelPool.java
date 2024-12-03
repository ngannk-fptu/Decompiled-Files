/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public final class ListenerInvokingChannelPool
implements SdkChannelPool {
    private final SdkChannelPool delegatePool;
    private final Supplier<Promise<Channel>> promiseFactory;
    private final List<ChannelPoolListener> listeners;

    public ListenerInvokingChannelPool(EventLoopGroup eventLoopGroup, SdkChannelPool delegatePool, List<ChannelPoolListener> listeners) {
        this(() -> eventLoopGroup.next().newPromise(), delegatePool, listeners);
    }

    public ListenerInvokingChannelPool(Supplier<Promise<Channel>> promiseFactory, SdkChannelPool delegatePool, List<ChannelPoolListener> listeners) {
        this.delegatePool = delegatePool;
        this.promiseFactory = promiseFactory;
        this.listeners = listeners;
    }

    @Override
    public Future<Channel> acquire() {
        return this.acquire(this.promiseFactory.get());
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> returnFuture) {
        this.delegatePool.acquire(this.promiseFactory.get()).addListener(NettyUtils.consumeOrPropagate(returnFuture, channel -> NettyUtils.doInEventLoop(channel.eventLoop(), () -> {
            this.invokeChannelAcquired((Channel)channel);
            returnFuture.trySuccess((Channel)channel);
        }, returnFuture)));
        return returnFuture;
    }

    private void invokeChannelAcquired(Channel channel) {
        this.listeners.forEach(listener -> listener.channelAcquired(channel));
    }

    @Override
    public Future<Void> release(Channel channel) {
        return this.release(channel, channel.eventLoop().newPromise());
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> returnFuture) {
        NettyUtils.doInEventLoop(channel.eventLoop(), () -> {
            this.invokeChannelReleased(channel);
            this.delegatePool.release(channel, returnFuture);
        }, returnFuture);
        return returnFuture;
    }

    private void invokeChannelReleased(Channel channel) {
        this.listeners.forEach(listener -> listener.channelReleased(channel));
    }

    @Override
    public void close() {
        this.delegatePool.close();
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        return this.delegatePool.collectChannelPoolMetrics(metrics);
    }

    @SdkInternalApi
    public static interface ChannelPoolListener {
        default public void channelAcquired(Channel channel) {
        }

        default public void channelReleased(Channel channel) {
        }
    }
}

