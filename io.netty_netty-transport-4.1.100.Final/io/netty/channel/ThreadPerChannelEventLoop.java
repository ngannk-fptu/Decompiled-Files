/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.ThreadPerChannelEventLoopGroup;

@Deprecated
public class ThreadPerChannelEventLoop
extends SingleThreadEventLoop {
    private final ThreadPerChannelEventLoopGroup parent;
    private Channel ch;

    public ThreadPerChannelEventLoop(ThreadPerChannelEventLoopGroup parent) {
        super((EventLoopGroup)parent, parent.executor, true);
        this.parent = parent;
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        return super.register(promise).addListener(new ChannelFutureListener(){

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    ThreadPerChannelEventLoop.this.ch = future.channel();
                } else {
                    ThreadPerChannelEventLoop.this.deregister();
                }
            }
        });
    }

    @Override
    @Deprecated
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        return super.register(channel, promise).addListener(new ChannelFutureListener(){

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    ThreadPerChannelEventLoop.this.ch = future.channel();
                } else {
                    ThreadPerChannelEventLoop.this.deregister();
                }
            }
        });
    }

    protected void run() {
        while (true) {
            Runnable task;
            if ((task = this.takeTask()) != null) {
                task.run();
                this.updateLastExecutionTime();
            }
            Channel ch = this.ch;
            if (this.isShuttingDown()) {
                if (ch != null) {
                    ch.unsafe().close(ch.unsafe().voidPromise());
                }
                if (!this.confirmShutdown()) continue;
                break;
            }
            if (ch == null || ch.isRegistered()) continue;
            this.runAllTasks();
            this.deregister();
        }
    }

    protected void deregister() {
        this.ch = null;
        this.parent.activeChildren.remove(this);
        this.parent.idleChildren.add(this);
    }

    @Override
    public int registeredChannels() {
        return 1;
    }
}

