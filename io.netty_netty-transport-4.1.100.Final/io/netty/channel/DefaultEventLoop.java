/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.DefaultThreadFactory
 */
package io.netty.channel;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventLoop
extends SingleThreadEventLoop {
    public DefaultEventLoop() {
        this((EventLoopGroup)null);
    }

    public DefaultEventLoop(ThreadFactory threadFactory) {
        this(null, threadFactory);
    }

    public DefaultEventLoop(Executor executor) {
        this(null, executor);
    }

    public DefaultEventLoop(EventLoopGroup parent) {
        this(parent, (ThreadFactory)new DefaultThreadFactory(DefaultEventLoop.class));
    }

    public DefaultEventLoop(EventLoopGroup parent, ThreadFactory threadFactory) {
        super(parent, threadFactory, true);
    }

    public DefaultEventLoop(EventLoopGroup parent, Executor executor) {
        super(parent, executor, true);
    }

    protected void run() {
        do {
            Runnable task;
            if ((task = this.takeTask()) == null) continue;
            DefaultEventLoop.runTask((Runnable)task);
            this.updateLastExecutionTime();
        } while (!this.confirmShutdown());
    }
}

