/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.HttpMetric;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public class IdleConnectionCountingChannelPool
implements SdkChannelPool {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(IdleConnectionCountingChannelPool.class);
    private static final AttributeKey<ChannelIdleState> CHANNEL_STATE = NettyUtils.getOrCreateAttributeKey("IdleConnectionCountingChannelPool.CHANNEL_STATE");
    private final EventExecutor executor;
    private final ChannelPool delegatePool;
    private int idleConnections = 0;

    public IdleConnectionCountingChannelPool(EventExecutor executor, ChannelPool delegatePool) {
        this.executor = executor;
        this.delegatePool = delegatePool;
    }

    @Override
    public Future<Channel> acquire() {
        return this.acquire(this.executor.newPromise());
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        Future<Channel> acquirePromise = this.delegatePool.acquire(this.executor.newPromise());
        acquirePromise.addListener(f -> {
            Throwable failure = acquirePromise.cause();
            if (failure != null) {
                promise.setFailure(failure);
            } else {
                Channel channel = (Channel)acquirePromise.getNow();
                this.channelAcquired(channel);
                promise.setSuccess(channel);
            }
        });
        return promise;
    }

    @Override
    public Future<Void> release(Channel channel) {
        return this.release(channel, new DefaultPromise<Void>(this.executor));
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        this.channelReleased(channel).addListener(f -> this.delegatePool.release(channel, promise));
        return promise;
    }

    @Override
    public void close() {
        this.delegatePool.close();
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        CompletableFuture<Void> result = new CompletableFuture<Void>();
        NettyUtils.doInEventLoop(this.executor, () -> {
            metrics.reportMetric(HttpMetric.AVAILABLE_CONCURRENCY, this.idleConnections);
            result.complete(null);
        }).addListener(f -> {
            if (!f.isSuccess()) {
                result.completeExceptionally(f.cause());
            }
        });
        return result;
    }

    private void addUpdateIdleCountOnCloseListener(Channel channel) {
        channel.closeFuture().addListener((GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener<Future>)f -> this.channelClosed(channel)));
    }

    private void channelAcquired(Channel channel) {
        NettyUtils.doInEventLoop(this.executor, () -> {
            ChannelIdleState channelIdleState = this.getChannelIdleState(channel);
            if (channelIdleState == null) {
                this.addUpdateIdleCountOnCloseListener(channel);
                this.setChannelIdleState(channel, ChannelIdleState.NOT_IDLE);
            } else {
                switch (channelIdleState) {
                    case IDLE: {
                        this.decrementIdleConnections();
                        this.setChannelIdleState(channel, ChannelIdleState.NOT_IDLE);
                        break;
                    }
                    case CLOSED: {
                        break;
                    }
                    default: {
                        log.warn(channel, () -> "Failed to update idle connection count metric on acquire, because the channel (" + channel + ") was in an unexpected state: " + (Object)((Object)channelIdleState));
                    }
                }
            }
        });
    }

    private Future<?> channelReleased(Channel channel) {
        return NettyUtils.doInEventLoop(this.executor, () -> {
            ChannelIdleState channelIdleState = this.getChannelIdleState(channel);
            if (channelIdleState == null) {
                log.warn(channel, () -> "Failed to update idle connection count metric on release, because the channel (" + channel + ") was in an unexpected state: null");
            } else {
                switch (channelIdleState) {
                    case NOT_IDLE: {
                        this.incrementIdleConnections();
                        this.setChannelIdleState(channel, ChannelIdleState.IDLE);
                        break;
                    }
                    case CLOSED: {
                        break;
                    }
                    default: {
                        log.warn(channel, () -> "Failed to update idle connection count metric on release, because the channel (" + channel + ") was in an unexpected state: " + (Object)((Object)channelIdleState));
                    }
                }
            }
        });
    }

    private void channelClosed(Channel channel) {
        NettyUtils.doInEventLoop(this.executor, () -> {
            ChannelIdleState channelIdleState = this.getChannelIdleState(channel);
            this.setChannelIdleState(channel, ChannelIdleState.CLOSED);
            if (channelIdleState != null) {
                switch (channelIdleState) {
                    case IDLE: {
                        this.decrementIdleConnections();
                        break;
                    }
                    case NOT_IDLE: {
                        break;
                    }
                    default: {
                        log.warn(channel, () -> "Failed to update idle connection count metric on close, because the channel (" + channel + ") was in an unexpected state: " + (Object)((Object)channelIdleState));
                    }
                }
            }
        });
    }

    private ChannelIdleState getChannelIdleState(Channel channel) {
        return channel.attr(CHANNEL_STATE).get();
    }

    private void setChannelIdleState(Channel channel, ChannelIdleState newState) {
        channel.attr(CHANNEL_STATE).set(newState);
    }

    private void decrementIdleConnections() {
        --this.idleConnections;
        log.trace(null, () -> "Idle connection count decremented, now " + this.idleConnections);
    }

    private void incrementIdleConnections() {
        ++this.idleConnections;
        log.trace(null, () -> "Idle connection count incremented, now " + this.idleConnections);
    }

    private static enum ChannelIdleState {
        IDLE,
        NOT_IDLE,
        CLOSED;

    }
}

