/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.event.internal;

import com.atlassian.event.internal.AnnotationAsynchronousEventResolver;
import com.atlassian.event.internal.AsynchronousEventResolver;
import com.atlassian.event.spi.EventDispatcher;
import com.atlassian.event.spi.EventExecutorFactory;
import com.atlassian.event.spi.EventRunnableFactory;
import com.atlassian.event.spi.ListenerInvoker;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsynchronousAbleEventDispatcher
implements EventDispatcher {
    private static final Logger log = LoggerFactory.getLogger(AsynchronousAbleEventDispatcher.class);
    private static final Executor SYNCHRONOUS_EXECUTOR = Runnable::run;
    private static final EventRunnableFactory SIMPLE_RUNNABLE_FACTORY = (invoker, event) -> () -> {
        block2: {
            try {
                invoker.invoke(event);
            }
            catch (Exception e) {
                if (!log.isErrorEnabled()) break block2;
                log.error("There was an exception thrown trying to dispatch event [{}] from the invoker [{}]", new Object[]{event, invoker, e});
            }
        }
    };
    private final Executor asynchronousExecutor;
    private final AsynchronousEventResolver asynchronousEventResolver;
    private final EventRunnableFactory runnableFactory;

    public AsynchronousAbleEventDispatcher(Executor executor, AsynchronousEventResolver asynchronousEventResolver, EventRunnableFactory runnableFactory) {
        this.asynchronousEventResolver = (AsynchronousEventResolver)Preconditions.checkNotNull((Object)asynchronousEventResolver);
        this.asynchronousExecutor = (Executor)Preconditions.checkNotNull((Object)executor);
        this.runnableFactory = (EventRunnableFactory)Preconditions.checkNotNull((Object)runnableFactory);
    }

    public AsynchronousAbleEventDispatcher(Executor executor, AsynchronousEventResolver asynchronousEventResolver) {
        this(executor, asynchronousEventResolver, SIMPLE_RUNNABLE_FACTORY);
    }

    public AsynchronousAbleEventDispatcher(EventExecutorFactory executorFactory, AsynchronousEventResolver asynchronousEventResolver, EventRunnableFactory runnableFactory) {
        this(((EventExecutorFactory)Preconditions.checkNotNull((Object)executorFactory)).getExecutor(), asynchronousEventResolver, runnableFactory);
    }

    public AsynchronousAbleEventDispatcher(EventExecutorFactory executorFactory, AsynchronousEventResolver asynchronousEventResolver) {
        this(executorFactory, asynchronousEventResolver, SIMPLE_RUNNABLE_FACTORY);
    }

    public AsynchronousAbleEventDispatcher(EventExecutorFactory executorFactory) {
        this(executorFactory, (AsynchronousEventResolver)new AnnotationAsynchronousEventResolver(), SIMPLE_RUNNABLE_FACTORY);
    }

    @Override
    public void dispatch(@Nonnull ListenerInvoker invoker, @Nonnull Object event) {
        this.executorFor((ListenerInvoker)Preconditions.checkNotNull((Object)invoker), Preconditions.checkNotNull((Object)event)).execute(this.runnableFactory.getRunnable(invoker, event));
    }

    protected Executor executorFor(ListenerInvoker invoker, Object event) {
        return this.asynchronousEventResolver.isAsynchronousEvent(event) && invoker.supportAsynchronousEvents() ? this.getAsynchronousExecutor() : this.getSynchronousExecutor();
    }

    protected Executor getSynchronousExecutor() {
        return SYNCHRONOUS_EXECUTOR;
    }

    protected Executor getAsynchronousExecutor() {
        return this.asynchronousExecutor;
    }
}

