/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.util.concurrent.GenericFutureListener
 */
package io.netty.handler.ipfilter;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;

public abstract class AbstractRemoteAddressFilter<T extends SocketAddress>
extends ChannelInboundHandlerAdapter {
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.handleNewChannel(ctx);
        ctx.fireChannelRegistered();
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!this.handleNewChannel(ctx)) {
            throw new IllegalStateException("cannot determine to accept or reject a channel: " + ctx.channel());
        }
        ctx.fireChannelActive();
    }

    private boolean handleNewChannel(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress == null) {
            return false;
        }
        ctx.pipeline().remove((ChannelHandler)this);
        if (this.accept(ctx, remoteAddress)) {
            this.channelAccepted(ctx, remoteAddress);
        } else {
            ChannelFuture rejectedFuture = this.channelRejected(ctx, remoteAddress);
            if (rejectedFuture != null) {
                rejectedFuture.addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
            } else {
                ctx.close();
            }
        }
        return true;
    }

    protected abstract boolean accept(ChannelHandlerContext var1, T var2) throws Exception;

    protected void channelAccepted(ChannelHandlerContext ctx, T remoteAddress) {
    }

    protected ChannelFuture channelRejected(ChannelHandlerContext ctx, T remoteAddress) {
        return null;
    }
}

