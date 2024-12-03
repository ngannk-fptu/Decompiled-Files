/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.config.EventThreadPoolConfiguration
 *  com.atlassian.event.internal.DirectEventExecutorFactory
 *  com.atlassian.event.internal.EventThreadFactory
 */
package com.atlassian.confluence.event;

import com.atlassian.confluence.event.CpuBasedThreadPoolConfiguration;
import com.atlassian.event.config.EventThreadPoolConfiguration;
import com.atlassian.event.internal.DirectEventExecutorFactory;
import com.atlassian.event.internal.EventThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Deprecated
public class ConfluencePoolBasedEventExecutorFactory
extends DirectEventExecutorFactory {
    private final RejectedExecutionHandler rejectedExecutionHandler;

    public ConfluencePoolBasedEventExecutorFactory(EventThreadPoolConfiguration configuration, EventThreadFactory eventThreadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        super(configuration, eventThreadFactory);
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    protected BlockingQueue<Runnable> getQueue() {
        return new LinkedBlockingQueue<Runnable>(CpuBasedThreadPoolConfiguration.QUEUE_SIZE);
    }

    public ThreadPoolExecutor getExecutor() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor)super.getExecutor();
        executor.setRejectedExecutionHandler(this.getRejectedExecutionHandler());
        return executor;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return this.rejectedExecutionHandler;
    }
}

