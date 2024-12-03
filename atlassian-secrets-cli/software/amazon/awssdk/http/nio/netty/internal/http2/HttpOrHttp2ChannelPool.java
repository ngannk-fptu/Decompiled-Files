/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.IdleConnectionCountingChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.NettyConfiguration;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2MultiplexedChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.BetterFixedChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public class HttpOrHttp2ChannelPool
implements SdkChannelPool {
    private final ChannelPool delegatePool;
    private final int maxConcurrency;
    private final EventLoopGroup eventLoopGroup;
    private final EventLoop eventLoop;
    private final NettyConfiguration configuration;
    private boolean protocolImplPromiseInitializationStarted = false;
    private Promise<ChannelPool> protocolImplPromise;
    private BetterFixedChannelPool protocolImpl;
    private boolean closed;

    public HttpOrHttp2ChannelPool(ChannelPool delegatePool, EventLoopGroup group, int maxConcurrency, NettyConfiguration configuration) {
        this.delegatePool = delegatePool;
        this.maxConcurrency = maxConcurrency;
        this.eventLoopGroup = group;
        this.eventLoop = group.next();
        this.configuration = configuration;
        this.protocolImplPromise = this.eventLoop.newPromise();
    }

    @Override
    public Future<Channel> acquire() {
        return this.acquire(this.eventLoop.newPromise());
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        NettyUtils.doInEventLoop(this.eventLoop, () -> this.acquire0(promise), promise);
        return promise;
    }

    private void acquire0(Promise<Channel> promise) {
        if (this.closed) {
            promise.setFailure(new IllegalStateException("Channel pool is closed!"));
            return;
        }
        if (this.protocolImpl != null) {
            this.protocolImpl.acquire(promise);
            return;
        }
        if (!this.protocolImplPromiseInitializationStarted) {
            this.initializeProtocol();
        }
        this.protocolImplPromise.addListener(future -> {
            if (future.isSuccess()) {
                ((ChannelPool)future.getNow()).acquire(promise);
            } else {
                promise.setFailure(future.cause());
            }
        });
    }

    private void initializeProtocol() {
        this.protocolImplPromiseInitializationStarted = true;
        this.delegatePool.acquire().addListener(future -> {
            if (future.isSuccess()) {
                Channel newChannel = (Channel)future.getNow();
                newChannel.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get().whenComplete((protocol, e) -> {
                    if (e != null) {
                        this.failProtocolImplPromise((Throwable)e);
                    } else {
                        this.completeProtocolConfiguration(newChannel, (Protocol)((Object)((Object)protocol)));
                    }
                });
            } else {
                this.failProtocolImplPromise(future.cause());
            }
        });
    }

    private void failProtocolImplPromise(Throwable e) {
        NettyUtils.doInEventLoop(this.eventLoop, () -> {
            this.protocolImplPromise.setFailure(e);
            this.protocolImplPromise = this.eventLoop.newPromise();
            this.protocolImplPromiseInitializationStarted = false;
        });
    }

    private void completeProtocolConfiguration(Channel newChannel, Protocol protocol) {
        NettyUtils.doInEventLoop(this.eventLoop, () -> {
            if (this.closed) {
                this.closeAndRelease(newChannel, new IllegalStateException("Pool closed"));
            } else {
                try {
                    this.configureProtocol(newChannel, protocol);
                }
                catch (Throwable e) {
                    this.closeAndRelease(newChannel, e);
                }
            }
        });
    }

    private void closeAndRelease(Channel newChannel, Throwable e) {
        newChannel.close();
        this.delegatePool.release(newChannel);
        this.protocolImplPromise.setFailure(e);
    }

    private void configureProtocol(Channel newChannel, Protocol protocol) {
        if (Protocol.HTTP1_1 == protocol) {
            IdleConnectionCountingChannelPool idleConnectionMetricChannelPool = new IdleConnectionCountingChannelPool(this.eventLoop, this.delegatePool);
            this.protocolImpl = BetterFixedChannelPool.builder().channelPool(idleConnectionMetricChannelPool).executor(this.eventLoop).acquireTimeoutAction(BetterFixedChannelPool.AcquireTimeoutAction.FAIL).acquireTimeoutMillis(this.configuration.connectionAcquireTimeoutMillis()).maxConnections(this.maxConcurrency).maxPendingAcquires(this.configuration.maxPendingConnectionAcquires()).build();
        } else {
            Duration idleConnectionTimeout = this.configuration.reapIdleConnections() ? Duration.ofMillis(this.configuration.idleTimeoutMillis()) : null;
            Http2MultiplexedChannelPool h2Pool = new Http2MultiplexedChannelPool(this.delegatePool, this.eventLoopGroup, idleConnectionTimeout);
            this.protocolImpl = BetterFixedChannelPool.builder().channelPool(h2Pool).executor(this.eventLoop).acquireTimeoutAction(BetterFixedChannelPool.AcquireTimeoutAction.FAIL).acquireTimeoutMillis(this.configuration.connectionAcquireTimeoutMillis()).maxConnections(this.maxConcurrency).maxPendingAcquires(this.configuration.maxPendingConnectionAcquires()).build();
        }
        this.delegatePool.release(newChannel).addListener(NettyUtils.runOrPropagate(this.protocolImplPromise, () -> this.protocolImplPromise.trySuccess(this.protocolImpl)));
    }

    @Override
    public Future<Void> release(Channel channel) {
        return this.release(channel, this.eventLoop.newPromise());
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        NettyUtils.doInEventLoop(this.eventLoop, () -> this.release0(channel, promise), promise);
        return promise;
    }

    private void release0(Channel channel, Promise<Void> promise) {
        if (this.protocolImpl == null) {
            this.delegatePool.release(channel, promise);
        } else {
            this.protocolImpl.release(channel, promise);
        }
    }

    @Override
    public void close() {
        NettyUtils.doInEventLoop(this.eventLoop, this::close0);
    }

    private void close0() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.protocolImpl != null) {
            this.protocolImpl.close();
        } else if (this.protocolImplPromiseInitializationStarted) {
            this.protocolImplPromise.addListener(f -> {
                if (f.isSuccess()) {
                    ((ChannelPool)f.getNow()).close();
                } else {
                    this.delegatePool.close();
                }
            });
        } else {
            this.delegatePool.close();
        }
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        CompletableFuture<Void> result = new CompletableFuture<Void>();
        this.protocolImplPromise.addListener(f -> {
            if (!f.isSuccess()) {
                result.completeExceptionally(f.cause());
            } else {
                this.protocolImpl.collectChannelPoolMetrics(metrics).whenComplete((m, t) -> {
                    if (t != null) {
                        result.completeExceptionally((Throwable)t);
                    } else {
                        result.complete((Void)m);
                    }
                });
            }
        });
        return result;
    }
}

