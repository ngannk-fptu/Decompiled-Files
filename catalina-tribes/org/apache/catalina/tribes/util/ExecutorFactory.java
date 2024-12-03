/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.tribes.util.StringManager;

public class ExecutorFactory {
    protected static final StringManager sm = StringManager.getManager(ExecutorFactory.class);

    public static ExecutorService newThreadPool(int minThreads, int maxThreads, long maxIdleTime, TimeUnit unit) {
        TaskQueue taskqueue = new TaskQueue();
        TribesThreadPoolExecutor service = new TribesThreadPoolExecutor(minThreads, maxThreads, maxIdleTime, unit, taskqueue);
        taskqueue.setParent(service);
        return service;
    }

    public static ExecutorService newThreadPool(int minThreads, int maxThreads, long maxIdleTime, TimeUnit unit, ThreadFactory threadFactory) {
        TaskQueue taskqueue = new TaskQueue();
        TribesThreadPoolExecutor service = new TribesThreadPoolExecutor(minThreads, maxThreads, maxIdleTime, unit, (BlockingQueue<Runnable>)taskqueue, threadFactory);
        taskqueue.setParent(service);
        return service;
    }

    private static class TaskQueue
    extends LinkedBlockingQueue<Runnable> {
        private static final long serialVersionUID = 1L;
        transient ThreadPoolExecutor parent = null;

        TaskQueue() {
        }

        public void setParent(ThreadPoolExecutor tp) {
            this.parent = tp;
        }

        public boolean force(Runnable o) {
            if (this.parent != null && this.parent.isShutdown()) {
                throw new RejectedExecutionException(sm.getString("executorFactory.not.running"));
            }
            return super.offer(o);
        }

        @Override
        public boolean offer(Runnable o) {
            if (this.parent == null) {
                return super.offer(o);
            }
            if (this.parent.getPoolSize() == this.parent.getMaximumPoolSize()) {
                return super.offer(o);
            }
            if (this.parent.getActiveCount() < this.parent.getPoolSize()) {
                return super.offer(o);
            }
            if (this.parent.getPoolSize() < this.parent.getMaximumPoolSize()) {
                return false;
            }
            return super.offer(o);
        }
    }

    private static class TribesThreadPoolExecutor
    extends ThreadPoolExecutor {
        TribesThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
            this.prestartAllCoreThreads();
        }

        TribesThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
            this.prestartAllCoreThreads();
        }

        TribesThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
            this.prestartAllCoreThreads();
        }

        TribesThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
            this.prestartAllCoreThreads();
        }

        @Override
        public void execute(Runnable command) {
            block2: {
                try {
                    super.execute(command);
                }
                catch (RejectedExecutionException rx) {
                    TaskQueue queue;
                    if (!(super.getQueue() instanceof TaskQueue) || (queue = (TaskQueue)super.getQueue()).force(command)) break block2;
                    throw new RejectedExecutionException(sm.getString("executorFactory.queue.full"));
                }
            }
        }
    }
}

