/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelOutboundHandlerAdapter
 *  io.netty.channel.ChannelPromise
 *  io.netty.util.concurrent.GenericFutureListener
 */
package io.netty.handler.address;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;

public abstract class DynamicAddressConnectHandler
extends ChannelOutboundHandlerAdapter {
    public final void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        SocketAddress local;
        SocketAddress remote;
        try {
            remote = this.remoteAddress(remoteAddress, localAddress);
            local = this.localAddress(remoteAddress, localAddress);
        }
        catch (Exception e) {
            promise.setFailure((Throwable)e);
            return;
        }
        ctx.connect(remote, local, promise).addListener((GenericFutureListener)new ChannelFutureListener(){

            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    future.channel().pipeline().remove((ChannelHandler)DynamicAddressConnectHandler.this);
                }
            }
        });
    }

    protected SocketAddress localAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        return localAddress;
    }

    protected SocketAddress remoteAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        return remoteAddress;
    }
}

