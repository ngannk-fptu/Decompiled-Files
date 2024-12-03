/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.pipeline().remove(this);
        super.channelRead(ctx, msg);
    }
}

