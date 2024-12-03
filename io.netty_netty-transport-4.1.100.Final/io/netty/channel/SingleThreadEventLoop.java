/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.EventExecutorGroup
 *  io.netty.util.concurrent.RejectedExecutionHandler
 *  io.netty.util.concurrent.RejectedExecutionHandlers
 *  io.netty.util.concurrent.SingleThreadEventExecutor
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.SystemPropertyUtil
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public abstract class SingleThreadEventLoop
extends SingleThreadEventExecutor
implements EventLoop {
    protected static final int DEFAULT_MAX_PENDING_TASKS = Math.max(16, SystemPropertyUtil.getInt((String)"io.netty.eventLoop.maxPendingTasks", (int)Integer.MAX_VALUE));
    private final Queue<Runnable> tailTasks;

    protected SingleThreadEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
        this(parent, threadFactory, addTaskWakesUp, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
    }

    protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp) {
        this(parent, executor, addTaskWakesUp, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
    }

    protected SingleThreadEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventExecutorGroup)parent, threadFactory, addTaskWakesUp, maxPendingTasks, rejectedExecutionHandler);
        this.tailTasks = this.newTaskQueue(maxPendingTasks);
    }

    protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventExecutorGroup)parent, executor, addTaskWakesUp, maxPendingTasks, rejectedExecutionHandler);
        this.tailTasks = this.newTaskQueue(maxPendingTasks);
    }

    protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp, Queue<Runnable> taskQueue, Queue<Runnable> tailTaskQueue, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventExecutorGroup)parent, executor, addTaskWakesUp, taskQueue, rejectedExecutionHandler);
        this.tailTasks = (Queue)ObjectUtil.checkNotNull(tailTaskQueue, (String)"tailTaskQueue");
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
        ObjectUtil.checkNotNull((Object)promise, (String)"promise");
        ObjectUtil.checkNotNull((Object)channel, (String)"channel");
        channel.unsafe().register(this, promise);
        return promise;
    }

    public final void executeAfterEventLoopIteration(Runnable task) {
        ObjectUtil.checkNotNull((Object)task, (String)"task");
        if (this.isShutdown()) {
            SingleThreadEventLoop.reject();
        }
        if (!this.tailTasks.offer(task)) {
            this.reject(task);
        }
        if (this.wakesUpForTask(task)) {
            this.wakeup(this.inEventLoop());
        }
    }

    final boolean removeAfterEventLoopIterationTask(Runnable task) {
        return this.tailTasks.remove(ObjectUtil.checkNotNull((Object)task, (String)"task"));
    }

    protected void afterRunningAllTasks() {
        this.runAllTasksFrom(this.tailTasks);
    }

    protected boolean hasTasks() {
        return super.hasTasks() || !this.tailTasks.isEmpty();
    }

    public int pendingTasks() {
        return super.pendingTasks() + this.tailTasks.size();
    }

    public int registeredChannels() {
        return -1;
    }

    public Iterator<Channel> registeredChannelsIterator() {
        throw new UnsupportedOperationException("registeredChannelsIterator");
    }

    protected static final class ChannelsReadOnlyIterator<T extends Channel>
    implements Iterator<Channel> {
        private final Iterator<T> channelIterator;
        private static final Iterator<Object> EMPTY = new Iterator<Object>(){

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };

        public ChannelsReadOnlyIterator(Iterable<T> channelIterable) {
            this.channelIterator = ((Iterable)ObjectUtil.checkNotNull(channelIterable, (String)"channelIterable")).iterator();
        }

        @Override
        public boolean hasNext() {
            return this.channelIterator.hasNext();
        }

        @Override
        public Channel next() {
            return (Channel)this.channelIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        public static <T> Iterator<T> empty() {
            return EMPTY;
        }
    }
}

