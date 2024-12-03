/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelOutboundHandlerAdapter
 *  io.netty.channel.ChannelPromise
 *  io.netty.resolver.AddressResolver
 *  io.netty.resolver.AddressResolverGroup
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.FutureListener
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.address;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class ResolveAddressHandler
extends ChannelOutboundHandlerAdapter {
    private final AddressResolverGroup<? extends SocketAddress> resolverGroup;

    public ResolveAddressHandler(AddressResolverGroup<? extends SocketAddress> resolverGroup) {
        this.resolverGroup = (AddressResolverGroup)ObjectUtil.checkNotNull(resolverGroup, (String)"resolverGroup");
    }

    public void connect(final ChannelHandlerContext ctx, SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        AddressResolver resolver = this.resolverGroup.getResolver(ctx.executor());
        if (resolver.isSupported(remoteAddress) && !resolver.isResolved(remoteAddress)) {
            resolver.resolve(remoteAddress).addListener((GenericFutureListener)new FutureListener<SocketAddress>(){

                public void operationComplete(Future<SocketAddress> future) {
                    Throwable cause = future.cause();
                    if (cause != null) {
                        promise.setFailure(cause);
                    } else {
                        ctx.connect((SocketAddress)future.getNow(), localAddress, promise);
                    }
                    ctx.pipeline().remove((ChannelHandler)ResolveAddressHandler.this);
                }
            });
        } else {
            ctx.connect(remoteAddress, localAddress, promise);
            ctx.pipeline().remove((ChannelHandler)this);
        }
    }
}

