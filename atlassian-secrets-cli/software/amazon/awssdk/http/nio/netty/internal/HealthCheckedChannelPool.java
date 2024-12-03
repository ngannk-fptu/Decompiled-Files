/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.NettyConfiguration;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public class HealthCheckedChannelPool
implements SdkChannelPool {
    private final EventLoopGroup eventLoopGroup;
    private final int acquireTimeoutMillis;
    private final SdkChannelPool delegate;

    public HealthCheckedChannelPool(EventLoopGroup eventLoopGroup, NettyConfiguration configuration, SdkChannelPool delegate) {
        this.eventLoopGroup = eventLoopGroup;
        this.acquireTimeoutMillis = configuration.connectionAcquireTimeoutMillis();
        this.delegate = delegate;
    }

    @Override
    public Future<Channel> acquire() {
        return this.acquire(this.eventLoopGroup.next().newPromise());
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> resultFuture) {
        ScheduledFuture<?> timeoutFuture = this.eventLoopGroup.schedule(() -> this.timeoutAcquire(resultFuture), (long)this.acquireTimeoutMillis, TimeUnit.MILLISECONDS);
        this.tryAcquire(resultFuture, timeoutFuture);
        return resultFuture;
    }

    private void timeoutAcquire(Promise<Channel> resultFuture) {
        resultFuture.tryFailure(new TimeoutException("Acquire operation took longer than " + this.acquireTimeoutMillis + " milliseconds."));
    }

    private void tryAcquire(Promise<Channel> resultFuture, ScheduledFuture<?> timeoutFuture) {
        if (resultFuture.isDone()) {
            return;
        }
        Promise<Channel> delegateFuture = this.eventLoopGroup.next().newPromise();
        this.delegate.acquire(delegateFuture);
        delegateFuture.addListener(f -> this.ensureAcquiredChannelIsHealthy(delegateFuture, resultFuture, timeoutFuture));
    }

    private void ensureAcquiredChannelIsHealthy(Promise<Channel> delegateFuture, Promise<Channel> resultFuture, ScheduledFuture<?> timeoutFuture) {
        if (!delegateFuture.isSuccess()) {
            timeoutFuture.cancel(false);
            resultFuture.tryFailure(delegateFuture.cause());
            return;
        }
        Channel channel = (Channel)delegateFuture.getNow();
        if (!this.isHealthy(channel)) {
            channel.close();
            this.delegate.release(channel);
            this.tryAcquire(resultFuture, timeoutFuture);
            return;
        }
        timeoutFuture.cancel(false);
        if (!resultFuture.trySuccess(channel)) {
            this.release(channel);
        }
    }

    @Override
    public Future<Void> release(Channel channel) {
        this.closeIfUnhealthy(channel);
        return this.delegate.release(channel);
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        this.closeIfUnhealthy(channel);
        return this.delegate.release(channel, promise);
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    private void closeIfUnhealthy(Channel channel) {
        if (!this.isHealthy(channel)) {
            channel.close();
        }
    }

    private boolean isHealthy(Channel channel) {
        if (channel.attr(ChannelAttributeKey.KEEP_ALIVE).get() != null && !channel.attr(ChannelAttributeKey.KEEP_ALIVE).get().booleanValue()) {
            return false;
        }
        return channel.isActive();
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        return this.delegate.collectChannelPoolMetrics(metrics);
    }
}

