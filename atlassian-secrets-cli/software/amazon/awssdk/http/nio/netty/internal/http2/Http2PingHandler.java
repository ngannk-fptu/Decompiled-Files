/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http2.DefaultHttp2PingFrame;
import io.netty.handler.codec.http2.Http2PingFrame;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.http2.PingFailedException;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class Http2PingHandler
extends SimpleChannelInboundHandler<Http2PingFrame> {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(Http2PingHandler.class);
    private static final Http2PingFrame DEFAULT_PING_FRAME = new DefaultHttp2PingFrame(0L);
    private final long pingTimeoutMillis;
    private ScheduledFuture<?> periodicPing;
    private long lastPingSendTime = 0L;
    private long lastPingAckTime = 0L;

    public Http2PingHandler(int pingTimeoutMillis) {
        this.pingTimeoutMillis = pingTimeoutMillis;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        CompletableFuture<Protocol> protocolFuture = ctx.channel().attr(ChannelAttributeKey.PROTOCOL_FUTURE).get();
        Validate.validState(protocolFuture != null, "Protocol future must be initialized before handler is added.", new Object[0]);
        protocolFuture.thenAccept(p -> this.start((Protocol)((Object)p), ctx));
    }

    private void start(Protocol protocol, ChannelHandlerContext ctx) {
        if (protocol == Protocol.HTTP2 && this.periodicPing == null) {
            this.periodicPing = ctx.channel().eventLoop().scheduleAtFixedRate(() -> this.doPeriodicPing(ctx.channel()), 0L, this.pingTimeoutMillis, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.stop();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.stop();
        ctx.fireChannelInactive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2PingFrame frame) {
        if (frame.ack()) {
            log.debug(ctx.channel(), () -> "Received PING ACK from channel " + ctx.channel());
            this.lastPingAckTime = System.currentTimeMillis();
        } else {
            ctx.fireChannelRead(frame);
        }
    }

    private void doPeriodicPing(Channel channel) {
        if (this.lastPingAckTime <= this.lastPingSendTime - this.pingTimeoutMillis) {
            long timeSinceLastPingSend = System.currentTimeMillis() - this.lastPingSendTime;
            this.channelIsUnhealthy(channel, new PingFailedException("Server did not respond to PING after " + timeSinceLastPingSend + "ms (limit: " + this.pingTimeoutMillis + "ms)"));
        } else {
            this.sendPing(channel);
        }
    }

    private void sendPing(Channel channel) {
        channel.writeAndFlush(DEFAULT_PING_FRAME).addListener((GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener<Future>)res -> {
            if (!res.isSuccess()) {
                log.debug(channel, () -> "Failed to write and flush PING frame to connection", res.cause());
                this.channelIsUnhealthy(channel, new PingFailedException("Failed to send PING to the service", res.cause()));
            } else {
                this.lastPingSendTime = System.currentTimeMillis();
            }
        }));
    }

    private void channelIsUnhealthy(Channel channel, PingFailedException exception) {
        this.stop();
        channel.pipeline().fireExceptionCaught(exception);
    }

    private void stop() {
        if (this.periodicPing != null) {
            this.periodicPing.cancel(false);
            this.periodicPing = null;
        }
    }
}

