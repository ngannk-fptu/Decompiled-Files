/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.EventLoop
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.pool.ChannelPool
 *  io.netty.handler.codec.http2.Http2Connection
 *  io.netty.handler.codec.http2.Http2Exception
 *  io.netty.handler.codec.http2.Http2LocalFlowController
 *  io.netty.handler.codec.http2.Http2Stream
 *  io.netty.util.AttributeKey
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.concurrent.PromiseCombiner
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.http.HttpMetric
 *  software.amazon.awssdk.http.Protocol
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.HttpMetric;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.http2.GoAwayException;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2ConnectionTerminatingException;
import software.amazon.awssdk.http.nio.netty.internal.http2.MultiplexedChannelRecord;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class Http2MultiplexedChannelPool
implements SdkChannelPool {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(Http2MultiplexedChannelPool.class);
    private static final AttributeKey<MultiplexedChannelRecord> MULTIPLEXED_CHANNEL = NettyUtils.getOrCreateAttributeKey("software.amazon.awssdk.http.nio.netty.internal.http2.Http2MultiplexedChannelPool.MULTIPLEXED_CHANNEL");
    private static final AttributeKey<Boolean> RELEASED = NettyUtils.getOrCreateAttributeKey("software.amazon.awssdk.http.nio.netty.internal.http2.Http2MultiplexedChannelPool.RELEASED");
    private final ChannelPool connectionPool;
    private final EventLoopGroup eventLoopGroup;
    private final Set<MultiplexedChannelRecord> connections;
    private final Duration idleConnectionTimeout;
    private AtomicBoolean closed = new AtomicBoolean(false);

    Http2MultiplexedChannelPool(ChannelPool connectionPool, EventLoopGroup eventLoopGroup, Duration idleConnectionTimeout) {
        this.connectionPool = connectionPool;
        this.eventLoopGroup = eventLoopGroup;
        this.connections = ConcurrentHashMap.newKeySet();
        this.idleConnectionTimeout = idleConnectionTimeout;
    }

    @SdkTestInternalApi
    Http2MultiplexedChannelPool(ChannelPool connectionPool, EventLoopGroup eventLoopGroup, Set<MultiplexedChannelRecord> connections, Duration idleConnectionTimeout) {
        this(connectionPool, eventLoopGroup, idleConnectionTimeout);
        this.connections.addAll(connections);
    }

    public Future<Channel> acquire() {
        return this.acquire((Promise<Channel>)this.eventLoopGroup.next().newPromise());
    }

    public Future<Channel> acquire(Promise<Channel> promise) {
        if (this.closed.get()) {
            return promise.setFailure((Throwable)new IOException("Channel pool is closed!"));
        }
        for (MultiplexedChannelRecord multiplexedChannel : this.connections) {
            if (!this.acquireStreamOnInitializedConnection(multiplexedChannel, promise)) continue;
            return promise;
        }
        this.acquireStreamOnNewConnection(promise);
        return promise;
    }

    private void acquireStreamOnNewConnection(Promise<Channel> promise) {
        Future newConnectionAcquire = this.connectionPool.acquire();
        newConnectionAcquire.addListener(f -> {
            if (!newConnectionAcquire.isSuccess()) {
                promise.setFailure(newConnectionAcquire.cause());
                return;
            }
            Channel parentChannel = (Channel)newConnectionAcquire.getNow();
            try {
                parentChannel.attr(ChannelAttributeKey.HTTP2_MULTIPLEXED_CHANNEL_POOL).set((Object)this);
                ((CompletableFuture)((CompletableFuture)parentChannel.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get()).thenAccept(protocol -> this.acquireStreamOnFreshConnection(promise, parentChannel, (Protocol)protocol))).exceptionally(throwable -> this.failAndCloseParent(promise, parentChannel, (Throwable)throwable));
            }
            catch (Throwable e) {
                this.failAndCloseParent(promise, parentChannel, e);
            }
        });
    }

    private void acquireStreamOnFreshConnection(Promise<Channel> promise, Channel parentChannel, Protocol protocol) {
        try {
            Long maxStreams = (Long)parentChannel.attr(ChannelAttributeKey.MAX_CONCURRENT_STREAMS).get();
            Validate.isTrue((protocol == Protocol.HTTP2 ? 1 : 0) != 0, (String)"Protocol negotiated on connection (%s) was expected to be HTTP/2, but it was %s.", (Object[])new Object[]{parentChannel, Protocol.HTTP1_1});
            Validate.isTrue((maxStreams != null ? 1 : 0) != 0, (String)"HTTP/2 was negotiated on the connection (%s), but the maximum number of streams was not initialized.", (Object[])new Object[]{parentChannel});
            Validate.isTrue((maxStreams > 0L ? 1 : 0) != 0, (String)"Maximum streams were not positive on channel (%s).", (Object[])new Object[]{parentChannel});
            MultiplexedChannelRecord multiplexedChannel = new MultiplexedChannelRecord(parentChannel, maxStreams, this.idleConnectionTimeout);
            parentChannel.attr(MULTIPLEXED_CHANNEL).set((Object)multiplexedChannel);
            Promise streamPromise = parentChannel.eventLoop().newPromise();
            if (!this.acquireStreamOnInitializedConnection(multiplexedChannel, (Promise<Channel>)streamPromise)) {
                this.failAndCloseParent(promise, parentChannel, new IOException("Connection was closed while creating a new stream."));
                return;
            }
            streamPromise.addListener(f -> {
                if (!streamPromise.isSuccess()) {
                    promise.setFailure(streamPromise.cause());
                    return;
                }
                Channel stream = (Channel)streamPromise.getNow();
                this.cacheConnectionForFutureStreams(stream, multiplexedChannel, promise);
            });
        }
        catch (Throwable e) {
            this.failAndCloseParent(promise, parentChannel, e);
        }
    }

    private void cacheConnectionForFutureStreams(Channel stream, MultiplexedChannelRecord multiplexedChannel, Promise<Channel> promise) {
        Channel parentChannel = stream.parent();
        parentChannel.pipeline().addLast(new ChannelHandler[]{ReleaseOnExceptionHandler.INSTANCE});
        this.connections.add(multiplexedChannel);
        if (this.closed.get()) {
            this.failAndCloseParent(promise, parentChannel, new IOException("Connection pool was closed while creating a new stream."));
            return;
        }
        promise.setSuccess((Object)stream);
    }

    private void tryExpandConnectionWindow(Channel parentChannel) {
        NettyUtils.doInEventLoop((EventExecutor)parentChannel.eventLoop(), () -> {
            Http2Connection http2Connection = (Http2Connection)parentChannel.attr(ChannelAttributeKey.HTTP2_CONNECTION).get();
            Integer initialWindowSize = (Integer)parentChannel.attr(ChannelAttributeKey.HTTP2_INITIAL_WINDOW_SIZE).get();
            Validate.notNull((Object)http2Connection, (String)("http2Connection should not be null on channel " + parentChannel), (Object[])new Object[0]);
            Validate.notNull((Object)http2Connection, (String)("initialWindowSize should not be null on channel " + parentChannel), (Object[])new Object[0]);
            Http2Stream connectionStream = http2Connection.connectionStream();
            log.debug(parentChannel, () -> "Expanding connection window size for " + parentChannel + " by " + initialWindowSize);
            try {
                Http2LocalFlowController localFlowController = (Http2LocalFlowController)http2Connection.local().flowController();
                localFlowController.incrementWindowSize(connectionStream, initialWindowSize.intValue());
            }
            catch (Http2Exception e) {
                log.warn(parentChannel, () -> "Failed to increment windowSize of connection " + parentChannel, e);
            }
        });
    }

    private Void failAndCloseParent(Promise<Channel> promise, Channel parentChannel, Throwable exception) {
        log.debug(parentChannel, () -> "Channel acquiring failed, closing connection " + parentChannel, exception);
        promise.setFailure(exception);
        this.closeAndReleaseParent(parentChannel);
        return null;
    }

    private boolean acquireStreamOnInitializedConnection(MultiplexedChannelRecord channelRecord, Promise<Channel> promise) {
        Promise acquirePromise = channelRecord.getConnection().eventLoop().newPromise();
        if (!channelRecord.acquireStream((Promise<Channel>)acquirePromise)) {
            return false;
        }
        acquirePromise.addListener(f -> {
            try {
                if (!acquirePromise.isSuccess()) {
                    promise.setFailure(acquirePromise.cause());
                    return;
                }
                Channel channel = (Channel)acquirePromise.getNow();
                channel.attr(ChannelAttributeKey.HTTP2_MULTIPLEXED_CHANNEL_POOL).set((Object)this);
                channel.attr(MULTIPLEXED_CHANNEL).set((Object)channelRecord);
                promise.setSuccess((Object)channel);
                this.tryExpandConnectionWindow(channel.parent());
            }
            catch (Exception e) {
                promise.setFailure((Throwable)e);
            }
        });
        return true;
    }

    public Future<Void> release(Channel childChannel) {
        return this.release(childChannel, (Promise<Void>)childChannel.eventLoop().newPromise());
    }

    public Future<Void> release(Channel childChannel, Promise<Void> promise) {
        if (childChannel.parent() == null) {
            this.closeAndReleaseParent(childChannel);
            return promise.setFailure((Throwable)new IllegalArgumentException("Channel (" + childChannel + ") is not a child channel."));
        }
        Channel parentChannel = childChannel.parent();
        MultiplexedChannelRecord multiplexedChannel = (MultiplexedChannelRecord)parentChannel.attr(MULTIPLEXED_CHANNEL).get();
        if (multiplexedChannel == null) {
            IOException exception = new IOException("Channel (" + childChannel + ") is not associated with any channel records. It will be closed, but cannot be released within this pool.");
            log.error(childChannel, exception::getMessage);
            childChannel.close();
            return promise.setFailure((Throwable)exception);
        }
        multiplexedChannel.closeAndReleaseChild(childChannel);
        if (multiplexedChannel.canBeClosedAndReleased()) {
            return this.closeAndReleaseParent(parentChannel, null, promise);
        }
        return promise.setSuccess(null);
    }

    private Future<Void> closeAndReleaseParent(Channel parentChannel) {
        return this.closeAndReleaseParent(parentChannel, null, (Promise<Void>)parentChannel.eventLoop().newPromise());
    }

    private Future<Void> closeAndReleaseParent(Channel parentChannel, Throwable cause) {
        return this.closeAndReleaseParent(parentChannel, cause, (Promise<Void>)parentChannel.eventLoop().newPromise());
    }

    private Future<Void> closeAndReleaseParent(Channel parentChannel, Throwable cause, Promise<Void> resultPromise) {
        if (parentChannel.parent() != null) {
            IOException exception = new IOException("Channel (" + parentChannel + ") is not a parent channel. It will be closed, but cannot be released within this pool.");
            log.error(parentChannel, exception::getMessage);
            parentChannel.close();
            return resultPromise.setFailure((Throwable)exception);
        }
        MultiplexedChannelRecord multiplexedChannel = (MultiplexedChannelRecord)parentChannel.attr(MULTIPLEXED_CHANNEL).get();
        if (multiplexedChannel != null) {
            if (cause == null) {
                multiplexedChannel.closeChildChannels();
            } else {
                multiplexedChannel.closeChildChannels(cause);
            }
            this.connections.remove(multiplexedChannel);
        }
        parentChannel.close();
        if (parentChannel.attr(RELEASED).getAndSet((Object)Boolean.TRUE) == null) {
            return this.connectionPool.release(parentChannel, resultPromise);
        }
        return resultPromise.setSuccess(null);
    }

    void handleGoAway(Channel parentChannel, int lastStreamId, GoAwayException exception) {
        log.debug(parentChannel, () -> "Received GOAWAY on " + parentChannel + " with lastStreamId of " + lastStreamId);
        try {
            MultiplexedChannelRecord multiplexedChannel = (MultiplexedChannelRecord)parentChannel.attr(MULTIPLEXED_CHANNEL).get();
            if (multiplexedChannel != null) {
                multiplexedChannel.handleGoAway(lastStreamId, exception);
            } else {
                this.closeAndReleaseParent(parentChannel, exception);
            }
        }
        catch (Exception e) {
            log.error(parentChannel, () -> "Failed to handle GOAWAY frame on channel " + parentChannel, e);
        }
    }

    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            Future<?> closeCompleteFuture = this.doClose();
            try {
                if (!closeCompleteFuture.await(10L, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Event loop didn't close after 10 seconds.");
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            Throwable exception = closeCompleteFuture.cause();
            if (exception != null) {
                throw new RuntimeException("Failed to close channel pool.", exception);
            }
        }
    }

    private Future<?> doClose() {
        EventLoop closeEventLoop = this.eventLoopGroup.next();
        Promise closeFinishedPromise = closeEventLoop.newPromise();
        NettyUtils.doInEventLoop((EventExecutor)closeEventLoop, () -> {
            Promise releaseAllChannelsPromise = closeEventLoop.newPromise();
            PromiseCombiner promiseCombiner = new PromiseCombiner((EventExecutor)closeEventLoop);
            ArrayList<MultiplexedChannelRecord> channelsToRemove = new ArrayList<MultiplexedChannelRecord>(this.connections);
            for (MultiplexedChannelRecord channel : channelsToRemove) {
                promiseCombiner.add(this.closeAndReleaseParent(channel.getConnection()));
            }
            promiseCombiner.finish(releaseAllChannelsPromise);
            releaseAllChannelsPromise.addListener(f -> {
                this.connectionPool.close();
                closeFinishedPromise.setSuccess(null);
            });
        });
        return closeFinishedPromise;
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        CompletableFuture<Void> result = new CompletableFuture<Void>();
        CompletableFuture<MultiplexedChannelRecord.Metrics> summedMetrics = new CompletableFuture<MultiplexedChannelRecord.Metrics>();
        List<CompletableFuture<MultiplexedChannelRecord.Metrics>> channelMetrics = this.connections.stream().map(MultiplexedChannelRecord::getMetrics).collect(Collectors.toList());
        this.accumulateMetrics(summedMetrics, channelMetrics);
        summedMetrics.whenComplete((m, t) -> {
            if (t != null) {
                result.completeExceptionally((Throwable)t);
            } else {
                try {
                    metrics.reportMetric(HttpMetric.AVAILABLE_CONCURRENCY, (Object)Math.toIntExact(m.getAvailableStreams()));
                    result.complete(null);
                }
                catch (Exception e) {
                    result.completeExceptionally(e);
                }
            }
        });
        return result;
    }

    private void accumulateMetrics(CompletableFuture<MultiplexedChannelRecord.Metrics> result, List<CompletableFuture<MultiplexedChannelRecord.Metrics>> channelMetrics) {
        this.accumulateMetrics(result, channelMetrics, new MultiplexedChannelRecord.Metrics(), 0);
    }

    private void accumulateMetrics(CompletableFuture<MultiplexedChannelRecord.Metrics> result, List<CompletableFuture<MultiplexedChannelRecord.Metrics>> channelMetrics, MultiplexedChannelRecord.Metrics resultAccumulator, int index) {
        if (index >= channelMetrics.size()) {
            result.complete(resultAccumulator);
            return;
        }
        channelMetrics.get(index).whenComplete((m, t) -> {
            if (t != null) {
                result.completeExceptionally((Throwable)t);
            } else {
                resultAccumulator.add((MultiplexedChannelRecord.Metrics)m);
                this.accumulateMetrics(result, channelMetrics, resultAccumulator, index + 1);
            }
        });
    }

    @ChannelHandler.Sharable
    private static final class ReleaseOnExceptionHandler
    extends ChannelDuplexHandler {
        private static final ReleaseOnExceptionHandler INSTANCE = new ReleaseOnExceptionHandler();

        private ReleaseOnExceptionHandler() {
        }

        public void channelInactive(ChannelHandlerContext ctx) {
            this.closeAndReleaseParent(ctx, new ClosedChannelException());
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            if (cause instanceof Http2ConnectionTerminatingException) {
                this.closeConnectionToNewRequests(ctx, cause);
            } else {
                this.closeAndReleaseParent(ctx, cause);
            }
        }

        void closeConnectionToNewRequests(ChannelHandlerContext ctx, Throwable cause) {
            MultiplexedChannelRecord multiplexedChannel = (MultiplexedChannelRecord)ctx.channel().attr(MULTIPLEXED_CHANNEL).get();
            if (multiplexedChannel != null) {
                multiplexedChannel.closeToNewStreams();
            } else {
                this.closeAndReleaseParent(ctx, cause);
            }
        }

        private void closeAndReleaseParent(ChannelHandlerContext ctx, Throwable cause) {
            Http2MultiplexedChannelPool pool = (Http2MultiplexedChannelPool)ctx.channel().attr(ChannelAttributeKey.HTTP2_MULTIPLEXED_CHANNEL_POOL).get();
            pool.closeAndReleaseParent(ctx.channel(), cause);
        }
    }
}

