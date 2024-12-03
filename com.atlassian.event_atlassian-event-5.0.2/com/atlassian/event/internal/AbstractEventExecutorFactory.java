/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.internal;

import com.atlassian.event.config.EventThreadPoolConfiguration;
import com.atlassian.event.internal.EventThreadFactory;
import com.atlassian.event.spi.EventExecutorFactory;
import com.google.common.base.Preconditions;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nonnull;

public abstract class AbstractEventExecutorFactory
implements EventExecutorFactory {
    private final EventThreadPoolConfiguration configuration;
    private final EventThreadFactory eventThreadFactory;

    public AbstractEventExecutorFactory(EventThreadPoolConfiguration configuration, EventThreadFactory eventThreadFactory) {
        this.configuration = (EventThreadPoolConfiguration)Preconditions.checkNotNull((Object)configuration);
        this.eventThreadFactory = (EventThreadFactory)Preconditions.checkNotNull((Object)eventThreadFactory);
    }

    public AbstractEventExecutorFactory(EventThreadPoolConfiguration configuration) {
        this(configuration, new EventThreadFactory());
    }

    protected abstract BlockingQueue<Runnable> getQueue();

    @Override
    @Nonnull
    public Executor getExecutor() {
        return new ThreadPoolExecutor(this.configuration.getCorePoolSize(), this.configuration.getMaximumPoolSize(), this.configuration.getKeepAliveTime(), this.configuration.getTimeUnit(), this.getQueue(), this.eventThreadFactory);
    }
}

