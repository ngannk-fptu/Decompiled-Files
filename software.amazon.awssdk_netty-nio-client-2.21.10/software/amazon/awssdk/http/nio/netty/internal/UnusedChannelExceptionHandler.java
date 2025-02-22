/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.handler.timeout.TimeoutException
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.TimeoutException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.utils.ChannelUtils;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;

@ChannelHandler.Sharable
@SdkInternalApi
public final class UnusedChannelExceptionHandler
extends ChannelInboundHandlerAdapter {
    public static final UnusedChannelExceptionHandler INSTANCE = new UnusedChannelExceptionHandler();
    private static final NettyClientLogger log = NettyClientLogger.getLogger(UnusedChannelExceptionHandler.class);

    private UnusedChannelExceptionHandler() {
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        boolean channelInUse = ChannelUtils.getAttribute(ctx.channel(), ChannelAttributeKey.IN_USE).orElse(false);
        if (channelInUse) {
            ctx.fireExceptionCaught(cause);
        } else {
            ctx.close();
            Optional<CompletableFuture<Void>> executeFuture = ChannelUtils.getAttribute(ctx.channel(), ChannelAttributeKey.EXECUTE_FUTURE_KEY);
            if (executeFuture.isPresent() && !executeFuture.get().isDone()) {
                log.error(ctx.channel(), () -> "An exception occurred on an channel (" + ctx.channel().id() + ") that was not in use, but was associated with a future that wasn't completed. This indicates a bug in the Java SDK, where a future was not completed while the channel was in use. The channel has been closed, and the future will be completed to prevent any ongoing issues.", cause);
                executeFuture.get().completeExceptionally(cause);
            } else if (this.isNettyIoException(cause) || this.hasNettyIoExceptionCause(cause)) {
                log.debug(ctx.channel(), () -> "An I/O exception (" + cause.getMessage() + ") occurred on a channel (" + ctx.channel().id() + ") that was not in use. The channel has been closed. This is usually normal.");
            } else {
                log.warn(ctx.channel(), () -> "A non-I/O exception occurred on a channel (" + ctx.channel().id() + ") that was not in use. The channel has been closed to prevent any ongoing issues.", cause);
            }
        }
    }

    public static UnusedChannelExceptionHandler getInstance() {
        return INSTANCE;
    }

    private boolean isNettyIoException(Throwable cause) {
        return cause instanceof IOException || cause instanceof TimeoutException;
    }

    private boolean hasNettyIoExceptionCause(Throwable cause) {
        return cause.getCause() != null && this.isNettyIoException(cause.getCause());
    }
}

