/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.pool.ChannelPool
 *  io.netty.handler.codec.http2.Http2SettingsFrame
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.Protocol
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http2.Http2SettingsFrame;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;

@SdkInternalApi
public final class Http2SettingsFrameHandler
extends SimpleChannelInboundHandler<Http2SettingsFrame> {
    private Channel channel;
    private final long clientMaxStreams;
    private AtomicReference<ChannelPool> channelPoolRef;

    public Http2SettingsFrameHandler(Channel channel, long clientMaxStreams, AtomicReference<ChannelPool> channelPoolRef) {
        this.channel = channel;
        this.clientMaxStreams = clientMaxStreams;
        this.channelPoolRef = channelPoolRef;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Http2SettingsFrame msg) {
        Long serverMaxStreams = Optional.ofNullable(msg.settings().maxConcurrentStreams()).orElse(Long.MAX_VALUE);
        this.channel.attr(ChannelAttributeKey.MAX_CONCURRENT_STREAMS).set((Object)Math.min(this.clientMaxStreams, serverMaxStreams));
        ((CompletableFuture)this.channel.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get()).complete(Protocol.HTTP2);
    }

    public void channelUnregistered(ChannelHandlerContext ctx) {
        if (!((CompletableFuture)this.channel.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get()).isDone()) {
            this.channelError(new IOException("The channel was closed before the protocol could be determined."), this.channel, ctx);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.channelError(cause, this.channel, ctx);
    }

    private void channelError(Throwable cause, Channel ch, ChannelHandlerContext ctx) {
        ((CompletableFuture)ch.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get()).completeExceptionally(cause);
        ctx.fireExceptionCaught(cause);
        ch.eventLoop().submit(() -> {
            try {
                if (ch.isActive()) {
                    ch.close();
                }
            }
            finally {
                this.channelPoolRef.get().release(ch);
            }
        });
    }
}

