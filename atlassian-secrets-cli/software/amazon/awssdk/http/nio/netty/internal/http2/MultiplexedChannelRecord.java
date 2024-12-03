/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamChannelBootstrap;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.ChannelDiagnostics;
import software.amazon.awssdk.http.nio.netty.internal.UnusedChannelExceptionHandler;
import software.amazon.awssdk.http.nio.netty.internal.http2.GoAwayException;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;

@SdkInternalApi
public class MultiplexedChannelRecord {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(MultiplexedChannelRecord.class);
    private final Channel connection;
    private final long maxConcurrencyPerConnection;
    private final Long allowedIdleConnectionTimeMillis;
    private final AtomicLong availableChildChannels;
    private volatile long lastReserveAttemptTimeMillis;
    private final Map<ChannelId, Http2StreamChannel> childChannels = new HashMap<ChannelId, Http2StreamChannel>();
    private ScheduledFuture<?> closeIfIdleTask;
    private volatile RecordState state = RecordState.OPEN;
    private volatile int lastStreamId;

    MultiplexedChannelRecord(Channel connection, long maxConcurrencyPerConnection, Duration allowedIdleConnectionTime) {
        this.connection = connection;
        this.maxConcurrencyPerConnection = maxConcurrencyPerConnection;
        this.availableChildChannels = new AtomicLong(maxConcurrencyPerConnection);
        this.allowedIdleConnectionTimeMillis = allowedIdleConnectionTime == null ? null : Long.valueOf(allowedIdleConnectionTime.toMillis());
    }

    boolean acquireStream(Promise<Channel> promise) {
        if (this.claimStream()) {
            this.releaseClaimOnFailure(promise);
            this.acquireClaimedStream(promise);
            return true;
        }
        return false;
    }

    void acquireClaimedStream(Promise<Channel> promise) {
        NettyUtils.doInEventLoop(this.connection.eventLoop(), () -> {
            if (this.state != RecordState.OPEN) {
                String message = this.state == RecordState.CLOSED_TO_NEW ? String.format("Connection %s received GOAWAY with Last Stream ID %d. Unable to open new streams on this connection.", this.connection, this.lastStreamId) : String.format("Connection %s was closed while acquiring new stream.", this.connection);
                log.warn(this.connection, () -> message);
                promise.setFailure(new IOException(message));
                return;
            }
            Future<Http2StreamChannel> streamFuture = new Http2StreamChannelBootstrap(this.connection).open();
            streamFuture.addListener(future -> {
                NettyUtils.warnIfNotInEventLoop(this.connection.eventLoop());
                if (!future.isSuccess()) {
                    promise.setFailure(future.cause());
                    return;
                }
                Http2StreamChannel channel = (Http2StreamChannel)future.getNow();
                channel.pipeline().addLast(UnusedChannelExceptionHandler.getInstance());
                channel.attr(ChannelAttributeKey.HTTP2_FRAME_STREAM).set(channel.stream());
                channel.attr(ChannelAttributeKey.CHANNEL_DIAGNOSTICS).set(new ChannelDiagnostics(channel));
                this.childChannels.put(channel.id(), channel);
                promise.setSuccess(channel);
                if (this.closeIfIdleTask == null && this.allowedIdleConnectionTimeMillis != null) {
                    this.enableCloseIfIdleTask();
                }
            });
        }, promise);
    }

    private void enableCloseIfIdleTask() {
        NettyUtils.warnIfNotInEventLoop(this.connection.eventLoop());
        long taskFrequencyMillis = Math.max(this.allowedIdleConnectionTimeMillis, 1000L);
        this.closeIfIdleTask = this.connection.eventLoop().scheduleAtFixedRate(this::closeIfIdle, taskFrequencyMillis, taskFrequencyMillis, TimeUnit.MILLISECONDS);
        this.connection.closeFuture().addListener((GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener<Future>)f -> this.closeIfIdleTask.cancel(false)));
    }

    private void releaseClaimOnFailure(Promise<Channel> promise) {
        try {
            promise.addListener(f -> {
                if (!promise.isSuccess()) {
                    this.releaseClaim();
                }
            });
        }
        catch (Throwable e) {
            this.releaseClaim();
            throw e;
        }
    }

    private void releaseClaim() {
        if (this.availableChildChannels.incrementAndGet() > this.maxConcurrencyPerConnection) {
            assert (false);
            log.warn(this.connection, () -> "Child channel count was caught attempting to be increased over max concurrency. Please report this issue to the AWS SDK for Java team.");
            this.availableChildChannels.decrementAndGet();
        }
    }

    void handleGoAway(int lastStreamId, GoAwayException exception) {
        NettyUtils.doInEventLoop(this.connection.eventLoop(), () -> {
            this.lastStreamId = lastStreamId;
            if (this.state == RecordState.CLOSED) {
                return;
            }
            if (this.state == RecordState.OPEN) {
                this.state = RecordState.CLOSED_TO_NEW;
            }
            ArrayList<Http2StreamChannel> childrenToClose = new ArrayList<Http2StreamChannel>(this.childChannels.values());
            childrenToClose.stream().filter(cc -> cc.stream().id() > lastStreamId).forEach(cc -> cc.pipeline().fireExceptionCaught(exception));
        });
    }

    void closeToNewStreams() {
        NettyUtils.doInEventLoop(this.connection.eventLoop(), () -> {
            if (this.state == RecordState.OPEN) {
                this.state = RecordState.CLOSED_TO_NEW;
            }
        });
    }

    void closeChildChannels() {
        this.closeAndExecuteOnChildChannels(ChannelOutboundInvoker::close);
    }

    void closeChildChannels(Throwable t) {
        this.closeAndExecuteOnChildChannels(ch -> ch.pipeline().fireExceptionCaught(this.decorateConnectionException(t)));
    }

    private Throwable decorateConnectionException(Throwable t) {
        String message = String.format("An error occurred on the connection: %s, [channel: %s]. All streams will be closed", t, this.connection.id());
        if (t instanceof IOException) {
            return new IOException(message, t);
        }
        return new Throwable(message, t);
    }

    private void closeAndExecuteOnChildChannels(Consumer<Channel> childChannelConsumer) {
        NettyUtils.doInEventLoop(this.connection.eventLoop(), () -> {
            if (this.state == RecordState.CLOSED) {
                return;
            }
            this.state = RecordState.CLOSED;
            ArrayList<Http2StreamChannel> childrenToClose = new ArrayList<Http2StreamChannel>(this.childChannels.values());
            for (Channel channel : childrenToClose) {
                childChannelConsumer.accept(channel);
            }
        });
    }

    void closeAndReleaseChild(Channel childChannel) {
        childChannel.close();
        NettyUtils.doInEventLoop(this.connection.eventLoop(), () -> {
            this.childChannels.remove(childChannel.id());
            this.releaseClaim();
        });
    }

    private void closeIfIdle() {
        NettyUtils.warnIfNotInEventLoop(this.connection.eventLoop());
        if (!this.childChannels.isEmpty()) {
            return;
        }
        long nonVolatileLastReserveAttemptTimeMillis = this.lastReserveAttemptTimeMillis;
        if (nonVolatileLastReserveAttemptTimeMillis > System.currentTimeMillis() - this.allowedIdleConnectionTimeMillis) {
            return;
        }
        if (!this.availableChildChannels.compareAndSet(this.maxConcurrencyPerConnection, 0L)) {
            return;
        }
        if (this.state != RecordState.OPEN) {
            return;
        }
        log.debug(this.connection, () -> "Connection " + this.connection + " has been idle for " + (System.currentTimeMillis() - nonVolatileLastReserveAttemptTimeMillis) + "ms and will be shut down.");
        this.state = RecordState.CLOSED;
        this.connection.close();
    }

    public Channel getConnection() {
        return this.connection;
    }

    private boolean claimStream() {
        this.lastReserveAttemptTimeMillis = System.currentTimeMillis();
        for (int attempt = 0; attempt < 5; ++attempt) {
            if (this.state != RecordState.OPEN) {
                return false;
            }
            long currentlyAvailable = this.availableChildChannels.get();
            if (currentlyAvailable <= 0L) {
                return false;
            }
            if (!this.availableChildChannels.compareAndSet(currentlyAvailable, currentlyAvailable - 1L)) continue;
            return true;
        }
        return false;
    }

    boolean canBeClosedAndReleased() {
        return this.state != RecordState.OPEN && this.availableChildChannels.get() == this.maxConcurrencyPerConnection;
    }

    CompletableFuture<Metrics> getMetrics() {
        CompletableFuture<Metrics> result = new CompletableFuture<Metrics>();
        NettyUtils.doInEventLoop(this.connection.eventLoop(), () -> {
            int streamCount = this.childChannels.size();
            result.complete(new Metrics().setAvailableStreams(this.maxConcurrencyPerConnection - (long)streamCount));
        });
        return result;
    }

    public static class Metrics {
        private long availableStreams = 0L;

        public long getAvailableStreams() {
            return this.availableStreams;
        }

        public Metrics setAvailableStreams(long availableStreams) {
            this.availableStreams = availableStreams;
            return this;
        }

        public void add(Metrics rhs) {
            this.availableStreams += rhs.availableStreams;
        }
    }

    private static enum RecordState {
        OPEN,
        CLOSED_TO_NEW,
        CLOSED;

    }
}

