/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.internal.AsynchronousAbleEventDispatcher
 *  com.atlassian.event.internal.AsynchronousEventResolver
 *  com.atlassian.event.spi.EventExecutorFactory
 *  com.atlassian.event.spi.EventRunnableFactory
 *  com.atlassian.event.spi.ListenerInvoker
 *  com.atlassian.sal.api.executor.ThreadLocalContextManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Stopwatch
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.event;

import com.atlassian.confluence.vcache.VCacheRequestContextOperations;
import com.atlassian.event.internal.AsynchronousAbleEventDispatcher;
import com.atlassian.event.internal.AsynchronousEventResolver;
import com.atlassian.event.spi.EventExecutorFactory;
import com.atlassian.event.spi.EventRunnableFactory;
import com.atlassian.event.spi.ListenerInvoker;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceEventDispatcher<C>
extends AsynchronousAbleEventDispatcher {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceEventDispatcher.class);
    private final ThreadLocalContextManager<C> threadLocalContextManager;

    public ConfluenceEventDispatcher(EventExecutorFactory executorFactory, AsynchronousEventResolver asynchronousEventResolver, ThreadLocalContextManager<C> threadLocalContextManager) {
        super(executorFactory, asynchronousEventResolver);
        this.threadLocalContextManager = threadLocalContextManager;
    }

    @Deprecated
    public ConfluenceEventDispatcher(EventExecutorFactory executorFactory, AsynchronousEventResolver asynchronousEventResolver, ThreadLocalContextManager<C> threadLocalContextManager, VCacheRequestContextOperations ignored) {
        this(executorFactory, asynchronousEventResolver, threadLocalContextManager);
    }

    protected Executor getAsynchronousExecutor() {
        return this.wrap(super.getAsynchronousExecutor());
    }

    @VisibleForTesting
    public Executor getInternalExecutor() {
        return super.getAsynchronousExecutor();
    }

    private Executor wrap(Executor delegate) {
        return command -> {
            final Object parentContext = this.threadLocalContextManager.getThreadLocalContext();
            delegate.execute(new Runnable(){

                @Override
                public void run() {
                    Object childContext = ConfluenceEventDispatcher.this.threadLocalContextManager.getThreadLocalContext();
                    ConfluenceEventDispatcher.this.threadLocalContextManager.setThreadLocalContext(parentContext);
                    try {
                        command.run();
                    }
                    finally {
                        ConfluenceEventDispatcher.this.threadLocalContextManager.setThreadLocalContext(childContext);
                    }
                }

                public String toString() {
                    return command.toString();
                }
            });
        };
    }

    private static class VCacheRequestContextRunnableFactory
    implements EventRunnableFactory {
        private final VCacheRequestContextOperations vCacheRequestContextOperations;

        public VCacheRequestContextRunnableFactory(VCacheRequestContextOperations vCacheRequestContextOperations) {
            this.vCacheRequestContextOperations = vCacheRequestContextOperations;
        }

        public @NonNull Runnable getRunnable(final ListenerInvoker invoker, final Object event) {
            return new Runnable(){

                /*
                 * Enabled force condition propagation
                 * Lifted jumps to return sites
                 */
                @Override
                public void run() {
                    Stopwatch stopwatch = null;
                    try {
                        if (log.isDebugEnabled()) {
                            stopwatch = Stopwatch.createStarted();
                        }
                        vCacheRequestContextOperations.doInRequestContext(() -> invoker.invoke(event));
                        if (stopwatch == null) return;
                    }
                    catch (Exception e) {
                        try {
                            log.error("There was an exception thrown trying to dispatch event [{}] from the invoker [{}]", new Object[]{event, invoker, e});
                            if (stopwatch == null) return;
                        }
                        catch (Throwable throwable) {
                            if (stopwatch == null) throw throwable;
                            log.debug("Event [{}] execution by [{}] took [{}] ms", new Object[]{event, invoker, stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)});
                            throw throwable;
                        }
                        log.debug("Event [{}] execution by [{}] took [{}] ms", new Object[]{event, invoker, stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)});
                        return;
                    }
                    log.debug("Event [{}] execution by [{}] took [{}] ms", new Object[]{event, invoker, stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)});
                    return;
                }

                public String toString() {
                    return "Invoker: " + invoker + "; Event: " + event;
                }
            };
        }
    }
}

