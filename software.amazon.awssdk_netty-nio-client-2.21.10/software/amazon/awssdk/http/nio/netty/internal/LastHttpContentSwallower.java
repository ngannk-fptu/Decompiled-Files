/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.handler.codec.http.HttpObject
 *  io.netty.handler.codec.http.LastHttpContent
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;
import software.amazon.awssdk.annotations.SdkInternalApi;

@ChannelHandler.Sharable
@SdkInternalApi
final class LastHttpContentSwallower
extends SimpleChannelInboundHandler<HttpObject> {
    private static final LastHttpContentSwallower INSTANCE = new LastHttpContentSwallower();

    private LastHttpContentSwallower() {
    }

    protected void channelRead0(ChannelHandlerContext ctx, HttpObject obj) {
        if (obj instanceof LastHttpContent) {
            ctx.read();
        } else {
            ctx.fireChannelRead((Object)obj);
        }
        ctx.pipeline().remove((ChannelHandler)this);
    }

    public static LastHttpContentSwallower getInstance() {
        return INSTANCE;
    }
}

