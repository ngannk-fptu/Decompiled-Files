/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class OptionalSslHandler
extends ByteToMessageDecoder {
    private final SslContext sslContext;

    public OptionalSslHandler(SslContext sslContext) {
        this.sslContext = (SslContext)ObjectUtil.checkNotNull((Object)sslContext, (String)"sslContext");
    }

    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 5) {
            return;
        }
        if (SslHandler.isEncrypted(in)) {
            this.handleSsl(context);
        } else {
            this.handleNonSsl(context);
        }
    }

    private void handleSsl(ChannelHandlerContext context) {
        SslHandler sslHandler = null;
        try {
            sslHandler = this.newSslHandler(context, this.sslContext);
            context.pipeline().replace((ChannelHandler)this, this.newSslHandlerName(), (ChannelHandler)sslHandler);
            sslHandler = null;
        }
        finally {
            if (sslHandler != null) {
                ReferenceCountUtil.safeRelease((Object)sslHandler.engine());
            }
        }
    }

    private void handleNonSsl(ChannelHandlerContext context) {
        ChannelHandler handler = this.newNonSslHandler(context);
        if (handler != null) {
            context.pipeline().replace((ChannelHandler)this, this.newNonSslHandlerName(), handler);
        } else {
            context.pipeline().remove((ChannelHandler)this);
        }
    }

    protected String newSslHandlerName() {
        return null;
    }

    protected SslHandler newSslHandler(ChannelHandlerContext context, SslContext sslContext) {
        return sslContext.newHandler(context.alloc());
    }

    protected String newNonSslHandlerName() {
        return null;
    }

    protected ChannelHandler newNonSslHandler(ChannelHandlerContext context) {
        return null;
    }
}

