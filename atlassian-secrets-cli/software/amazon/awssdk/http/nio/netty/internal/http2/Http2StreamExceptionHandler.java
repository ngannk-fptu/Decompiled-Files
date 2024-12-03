/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.TimeoutException;
import java.io.IOException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2ConnectionTerminatingException;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;

@ChannelHandler.Sharable
@SdkInternalApi
public final class Http2StreamExceptionHandler
extends ChannelInboundHandlerAdapter {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(Http2StreamExceptionHandler.class);
    private static final Http2StreamExceptionHandler INSTANCE = new Http2StreamExceptionHandler();

    private Http2StreamExceptionHandler() {
    }

    public static Http2StreamExceptionHandler create() {
        return INSTANCE;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (this.isIoError(cause) && ctx.channel().parent() != null) {
            Channel parent = ctx.channel().parent();
            log.debug(parent, () -> "An I/O error occurred on an Http2 stream, notifying the connection channel " + parent);
            parent.pipeline().fireExceptionCaught(new Http2ConnectionTerminatingException("An I/O error occurred on an associated Http2 stream " + ctx.channel()));
        }
        ctx.fireExceptionCaught(cause);
    }

    private boolean isIoError(Throwable cause) {
        return cause instanceof TimeoutException || cause instanceof IOException;
    }
}

