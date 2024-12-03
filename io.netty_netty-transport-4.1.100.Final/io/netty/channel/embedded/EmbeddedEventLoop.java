/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.AbstractScheduledEventExecutor
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.channel.embedded;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EmbeddedEventLoop
extends AbstractScheduledEventExecutor
implements EventLoop {
    private long startTime = EmbeddedEventLoop.initialNanoTime();
    private long frozenTimestamp;
    private boolean timeFrozen;
    private final Queue<Runnable> tasks = new ArrayDeque<Runnable>(2);

    EmbeddedEventLoop() {
    }

    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup)super.parent();
    }

    @Override
    public EventLoop next() {
        return (EventLoop)super.next();
    }

    public void execute(Runnable command) {
        this.tasks.add((Runnable)ObjectUtil.checkNotNull((Object)command, (String)"command"));
    }

    void runTasks() {
        Runnable task;
        while ((task = this.tasks.poll()) != null) {
            task.run();
        }
    }

    boolean hasPendingNormalTasks() {
        return !this.tasks.isEmpty();
    }

    long runScheduledTasks() {
        long time = this.getCurrentTimeNanos();
        Runnable task;
        while ((task = this.pollScheduledTask(time)) != null) {
            task.run();
        }
        return this.nextScheduledTaskNano();
    }

    long nextScheduledTask() {
        return this.nextScheduledTaskNano();
    }

    protected long getCurrentTimeNanos() {
        if (this.timeFrozen) {
            return this.frozenTimestamp;
        }
        return System.nanoTime() - this.startTime;
    }

    void advanceTimeBy(long nanos) {
        if (this.timeFrozen) {
            this.frozenTimestamp += nanos;
        } else {
            this.startTime -= nanos;
        }
    }

    void freezeTime() {
        if (!this.timeFrozen) {
            this.frozenTimestamp = this.getCurrentTimeNanos();
            this.timeFrozen = true;
        }
    }

    void unfreezeTime() {
        if (this.timeFrozen) {
            this.startTime = System.nanoTime() - this.frozenTimestamp;
            this.timeFrozen = false;
        }
    }

    protected void cancelScheduledTasks() {
        super.cancelScheduledTasks();
    }

    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    public Future<?> terminationFuture() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    public boolean isShuttingDown() {
        return false;
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return false;
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return this.register(new DefaultChannelPromise(channel, (EventExecutor)this));
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        ObjectUtil.checkNotNull((Object)promise, (String)"promise");
        promise.channel().unsafe().register(this, promise);
        return promise;
    }

    @Override
    @Deprecated
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        channel.unsafe().register(this, promise);
        return promise;
    }

    public boolean inEventLoop() {
        return true;
    }

    public boolean inEventLoop(Thread thread) {
        return true;
    }
}

