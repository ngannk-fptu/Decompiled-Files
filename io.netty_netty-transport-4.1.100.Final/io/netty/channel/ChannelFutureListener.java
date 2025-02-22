/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.GenericFutureListener
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public interface ChannelFutureListener
extends GenericFutureListener<ChannelFuture> {
    public static final ChannelFutureListener CLOSE = new ChannelFutureListener(){

        public void operationComplete(ChannelFuture future) {
            future.channel().close();
        }
    };
    public static final ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener(){

        public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
                future.channel().close();
            }
        }
    };
    public static final ChannelFutureListener FIRE_EXCEPTION_ON_FAILURE = new ChannelFutureListener(){

        public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
                future.channel().pipeline().fireExceptionCaught(future.cause());
            }
        }
    };
}

