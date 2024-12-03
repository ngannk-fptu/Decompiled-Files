/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.timeout.IdleStateEvent
 *  io.netty.handler.timeout.IdleStateHandler
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;

@SdkInternalApi
public class IdleConnectionReaperHandler
extends IdleStateHandler {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(IdleConnectionReaperHandler.class);
    private final int maxIdleTimeMillis;

    public IdleConnectionReaperHandler(int maxIdleTimeMillis) {
        super(0L, 0L, (long)maxIdleTimeMillis, TimeUnit.MILLISECONDS);
        this.maxIdleTimeMillis = maxIdleTimeMillis;
    }

    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent event) {
        assert (ctx.channel().eventLoop().inEventLoop());
        boolean channelNotInUse = Boolean.FALSE.equals(ctx.channel().attr(ChannelAttributeKey.IN_USE).get());
        if (channelNotInUse && ctx.channel().isOpen()) {
            log.debug(ctx.channel(), () -> "Closing unused connection (" + ctx.channel().id() + ") because it has been idle for longer than " + this.maxIdleTimeMillis + " milliseconds.");
            ctx.close();
        }
    }
}

