/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void read(ChannelHandlerContext ctx) {
        ctx.read();
        ctx.channel().parent().flush();
    }

    public static FlushOnReadHandler getInstance() {
        return INSTANCE;
    }
}

