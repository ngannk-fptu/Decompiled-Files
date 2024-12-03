/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalContextManager
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 */
package com.atlassian.confluence.impl.util.concurrent;

import com.atlassian.confluence.impl.util.concurrent.TaskWrapper;
import com.atlassian.confluence.impl.util.concurrent.TaskWrappingExecutorService;
import com.atlassian.confluence.impl.util.concurrent.TaskWrappingScheduledExecutorService;
import com.atlassian.confluence.vcache.VCacheRequestContextOperations;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Deprecated
public class ConfluenceExecutors {
    public static final TaskWrapper VCACHE_TASK_WRAPPER = new VCacheTaskWrapper();
    public static final TaskWrapper THREAD_LOCAL_CONTEXT_TASK_WRAPPER = new ThreadLocalContextTaskWrapper();

    public static ExecutorService newFixedThreadPool(int numberOfThreads, ThreadFactory threadFactory) {
        return ConfluenceExecutors.wrap(Executors.newFixedThreadPool(numberOfThreads, threadFactory));
    }

    public static ScheduledExecutorService newScheduledThreadPool(int threadPoolSize, ThreadFactory threadFactory) {
        return ConfluenceExecutors.wrap(Executors.newScheduledThreadPool(threadPoolSize, threadFactory));
    }

    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        return ConfluenceExecutors.wrap(Executors.newSingleThreadExecutor(threadFactory));
    }

    public static ScheduledExecutorService wrap(ScheduledExecutorService delegate) {
        return new TaskWrappingScheduledExecutorService(delegate, VCACHE_TASK_WRAPPER);
    }

    public static ExecutorService wrap(ExecutorService delegate) {
        return ConfluenceExecutors.wrap(delegate, VCACHE_TASK_WRAPPER);
    }

    public static ExecutorService wrap(ExecutorService delegate, TaskWrapper ... taskWrappers) {
        ExecutorService result = delegate;
        for (TaskWrapper taskWrapper : taskWrappers) {
            result = new TaskWrappingExecutorService(result, taskWrapper);
        }
        return result;
    }

    private static final class ThreadLocalContextTaskWrapper<C>
    implements TaskWrapper {
        private final Supplier<ThreadLocalContextManager<C>> threadLocalContextManager = Lazy.supplier(() -> (ThreadLocalContextManager)ContainerManager.getComponent((String)"threadLocalContextManager"));

        private ThreadLocalContextTaskWrapper() {
        }

        @Override
        public Runnable wrap(Runnable task) {
            ThreadLocalContextManager contextManager = (ThreadLocalContextManager)this.threadLocalContextManager.get();
            Object parentContext = contextManager.getThreadLocalContext();
            return () -> {
                Object childContext = contextManager.getThreadLocalContext();
                contextManager.setThreadLocalContext(parentContext);
                try {
                    task.run();
                }
                finally {
                    contextManager.setThreadLocalContext(childContext);
                }
            };
        }

        @Override
        public <T> Callable<T> wrap(Callable<T> task) {
            ThreadLocalContextManager contextManager = (ThreadLocalContextManager)this.threadLocalContextManager.get();
            Object parentContext = contextManager.getThreadLocalContext();
            return () -> {
                Object childContext = contextManager.getThreadLocalContext();
                contextManager.setThreadLocalContext(parentContext);
                try {
                    Object v = task.call();
                    return v;
                }
                finally {
                    contextManager.setThreadLocalContext(childContext);
                }
            };
        }
    }

    private static final class VCacheTaskWrapper
    implements TaskWrapper {
        private final Supplier<VCacheRequestContextOperations> vCacheRequestContextOperations = Lazy.supplier(() -> (VCacheRequestContextOperations)ContainerManager.getComponent((String)"vcacheRequestContextManager"));

        private VCacheTaskWrapper() {
        }

        @Override
        public Runnable wrap(Runnable task) {
            return ((VCacheRequestContextOperations)this.vCacheRequestContextOperations.get()).withRequestContext(task);
        }

        @Override
        public <T> Callable<T> wrap(Callable<T> task) {
            return ((VCacheRequestContextOperations)this.vCacheRequestContextOperations.get()).withRequestContext(task);
        }
    }
}

