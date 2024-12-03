/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.ssl.ocsp;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.internal.ObjectUtil;
import javax.net.ssl.SSLHandshakeException;

public abstract class OcspClientHandler
extends ChannelInboundHandlerAdapter {
    private final ReferenceCountedOpenSslEngine engine;

    protected OcspClientHandler(ReferenceCountedOpenSslEngine engine) {
        this.engine = (ReferenceCountedOpenSslEngine)ObjectUtil.checkNotNull((Object)engine, (String)"engine");
    }

    protected abstract boolean verify(ChannelHandlerContext var1, ReferenceCountedOpenSslEngine var2) throws Exception;

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof SslHandshakeCompletionEvent) {
            ctx.pipeline().remove((ChannelHandler)this);
            SslHandshakeCompletionEvent event = (SslHandshakeCompletionEvent)evt;
            if (event.isSuccess() && !this.verify(ctx, this.engine)) {
                throw new SSLHandshakeException("Bad OCSP response");
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
}

