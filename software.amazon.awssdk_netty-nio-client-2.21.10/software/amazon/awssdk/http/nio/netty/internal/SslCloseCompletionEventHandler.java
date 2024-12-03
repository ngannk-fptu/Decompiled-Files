/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.handler.ssl.SslCloseCompletionEvent
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslCloseCompletionEvent;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.utils.ChannelUtils;

@ChannelHandler.Sharable
@SdkInternalApi
public final class SslCloseCompletionEventHandler
extends ChannelInboundHandlerAdapter {
    private static final SslCloseCompletionEventHandler INSTANCE = new SslCloseCompletionEventHandler();

    private SslCloseCompletionEventHandler() {
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        boolean channelInUse = ChannelUtils.getAttribute(ctx.channel(), ChannelAttributeKey.IN_USE).orElse(false);
        if (!channelInUse && evt instanceof SslCloseCompletionEvent) {
            ctx.close();
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    public static SslCloseCompletionEventHandler getInstance() {
        return INSTANCE;
    }
}

