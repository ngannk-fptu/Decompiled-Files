/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.utils;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.utils.DelegatingChannelHandlerContext;

@SdkInternalApi
public class OrderedWriteChannelHandlerContext
extends DelegatingChannelHandlerContext {
    private OrderedWriteChannelHandlerContext(ChannelHandlerContext delegate) {
        super(delegate);
    }

    public static ChannelHandlerContext wrap(ChannelHandlerContext ctx) {
        return new OrderedWriteChannelHandlerContext(ctx);
    }

    @Override
    public ChannelFuture write(Object msg) {
        return this.doInOrder((ChannelPromise promise) -> super.write(msg, (ChannelPromise)promise));
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        this.doInOrder(() -> super.write(msg, promise));
        return promise;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return this.doInOrder((ChannelPromise promise) -> super.writeAndFlush(msg, (ChannelPromise)promise));
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        this.doInOrder(() -> super.writeAndFlush(msg, promise));
        return promise;
    }

    private ChannelFuture doInOrder(Consumer<ChannelPromise> task) {
        ChannelPromise promise = this.newPromise();
        if (!this.channel().eventLoop().inEventLoop()) {
            task.accept(promise);
        } else {
            this.channel().eventLoop().execute(() -> task.accept(promise));
        }
        return promise;
    }

    private void doInOrder(Runnable task) {
        if (!this.channel().eventLoop().inEventLoop()) {
            task.run();
        } else {
            this.channel().eventLoop().execute(task);
        }
    }
}

