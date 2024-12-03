/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.handler.codec.http2.DefaultHttp2PingFrame
 *  io.netty.handler.codec.http2.Http2PingFrame
 *  io.netty.util.concurrent.ScheduledFuture
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.Protocol
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http2.DefaultHttp2PingFrame;
import io.netty.handler.codec.http2.Http2PingFrame;
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

    public void handlerAdded(ChannelHandlerContext ctx) {
        CompletableFuture protocolFuture = (CompletableFuture)ctx.channel().attr(ChannelAttributeKey.PROTOCOL_FUTURE).get();
        Validate.validState((protocolFuture != null ? 1 : 0) != 0, (String)"Protocol future must be initialized before handler is added.", (Object[])new Object[0]);
        protocolFuture.thenAccept(p -> this.start((Protocol)p, ctx));
    }

    private void start(Protocol protocol, ChannelHandlerContext ctx) {
        if (protocol == Protocol.HTTP2 && this.periodicPing == null) {
            this.periodicPing = ctx.channel().eventLoop().scheduleAtFixedRate(() -> this.doPeriodicPing(ctx.channel()), 0L, this.pingTimeoutMillis, TimeUnit.MILLISECONDS);
        }
    }

    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.stop();
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        this.stop();
        ctx.fireChannelInactive();
    }

    protected void channelRead0(ChannelHandlerContext ctx, Http2PingFrame frame) {
        if (frame.ack()) {
            log.debug(ctx.channel(), () -> "Received PING ACK from channel " + ctx.channel());
            this.lastPingAckTime = System.currentTimeMillis();
        } else {
            ctx.fireChannelRead((Object)frame);
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
        channel.writeAndFlush((Object)DEFAULT_PING_FRAME).addListener(res -> {
            if (!res.isSuccess()) {
                log.debug(channel, () -> "Failed to write and flush PING frame to connection", res.cause());
                this.channelIsUnhealthy(channel, new PingFailedException("Failed to send PING to the service", res.cause()));
            } else {
                this.lastPingSendTime = System.currentTimeMillis();
            }
        });
    }

    private void channelIsUnhealthy(Channel channel, PingFailedException exception) {
        this.stop();
        channel.pipeline().fireExceptionCaught((Throwable)exception);
    }

    private void stop() {
        if (this.periodicPing != null) {
            this.periodicPing.cancel(false);
            this.periodicPing = null;
        }
    }
}

