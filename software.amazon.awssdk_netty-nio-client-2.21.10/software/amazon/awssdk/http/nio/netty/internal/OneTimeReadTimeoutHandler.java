/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.timeout.ReadTimeoutHandler
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class OneTimeReadTimeoutHandler
extends ReadTimeoutHandler {
    OneTimeReadTimeoutHandler(Duration timeout) {
        super(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.pipeline().remove((ChannelHandler)this);
        super.channelRead(ctx, msg);
    }
}

