/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.embedded;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
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

    @Override
    public void execute(Runnable command) {
        this.tasks.add(ObjectUtil.checkNotNull(command, "command"));
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

    @Override
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

    @Override
    protected void cancelScheduledTasks() {
        super.cancelScheduledTasks();
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> terminationFuture() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return false;
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return this.register(new DefaultChannelPromise(channel, this));
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        promise.channel().unsafe().register(this, promise);
        return promise;
    }

    @Override
    @Deprecated
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        channel.unsafe().register(this, promise);
        return promise;
    }

    @Override
    public boolean inEventLoop() {
        return true;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return true;
    }
}

