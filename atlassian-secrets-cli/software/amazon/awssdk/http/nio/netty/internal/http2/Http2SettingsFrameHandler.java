/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http2.Http2SettingsFrame;
import java.io.IOException;
import java.util.Optional;
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

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2SettingsFrame msg) {
        Long serverMaxStreams = Optional.ofNullable(msg.settings().maxConcurrentStreams()).orElse(Long.MAX_VALUE);
        this.channel.attr(ChannelAttributeKey.MAX_CONCURRENT_STREAMS).set(Math.min(this.clientMaxStreams, serverMaxStreams));
        this.channel.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get().complete(Protocol.HTTP2);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        if (!this.channel.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get().isDone()) {
            this.channelError(new IOException("The channel was closed before the protocol could be determined."), this.channel, ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.channelError(cause, this.channel, ctx);
    }

    private void channelError(Throwable cause, Channel ch, ChannelHandlerContext ctx) {
        ch.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get().completeExceptionally(cause);
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

