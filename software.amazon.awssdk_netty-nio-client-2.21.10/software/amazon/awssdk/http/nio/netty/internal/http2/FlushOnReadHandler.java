/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelOutboundHandlerAdapter
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import software.amazon.awssdk.annotations.SdkInternalApi;

@ChannelHandler.Sharable
@SdkInternalApi
public final class FlushOnReadHandler
extends ChannelOutboundHandlerAdapter {
    private static final FlushOnReadHandler INSTANCE = new FlushOnReadHandler();

    private FlushOnReadHandler() {
    }

    public void read(ChannelHandlerContext ctx) {
        ctx.read();
        ctx.channel().parent().flush();
    }

    public static FlushOnReadHandler getInstance() {
        return INSTANCE;
    }
}

