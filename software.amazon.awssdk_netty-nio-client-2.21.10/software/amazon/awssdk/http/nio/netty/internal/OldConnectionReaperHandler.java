/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.util.concurrent.ScheduledFuture
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class OldConnectionReaperHandler
extends ChannelDuplexHandler {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(OldConnectionReaperHandler.class);
    private final int connectionTtlMillis;
    private ScheduledFuture<?> channelKiller;

    public OldConnectionReaperHandler(int connectionTtlMillis) {
        Validate.isPositive((int)connectionTtlMillis, (String)"connectionTtlMillis");
        this.connectionTtlMillis = connectionTtlMillis;
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.initialize(ctx);
        super.handlerAdded(ctx);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.initialize(ctx);
        super.channelActive(ctx);
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.initialize(ctx);
        super.channelRegistered(ctx);
    }

    private void initialize(ChannelHandlerContext ctx) {
        if (this.channelKiller == null) {
            this.channelKiller = ctx.channel().eventLoop().schedule(() -> this.closeChannel(ctx), (long)this.connectionTtlMillis, TimeUnit.MILLISECONDS);
        }
    }

    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.destroy();
    }

    private void destroy() {
        if (this.channelKiller != null) {
            this.channelKiller.cancel(false);
            this.channelKiller = null;
        }
    }

    private void closeChannel(ChannelHandlerContext ctx) {
        assert (ctx.channel().eventLoop().inEventLoop());
        if (ctx.channel().isOpen()) {
            if (Boolean.FALSE.equals(ctx.channel().attr(ChannelAttributeKey.IN_USE).get())) {
                log.debug(ctx.channel(), () -> "Closing unused connection (" + ctx.channel().id() + ") because it has reached its maximum time to live of " + this.connectionTtlMillis + " milliseconds.");
                ctx.close();
            } else {
                log.debug(ctx.channel(), () -> "Connection (" + ctx.channel().id() + ") will be closed during its next release, because it has reached its maximum time to live of " + this.connectionTtlMillis + " milliseconds.");
                ctx.channel().attr(ChannelAttributeKey.CLOSE_ON_RELEASE).set((Object)true);
            }
        }
        this.channelKiller = null;
    }
}

