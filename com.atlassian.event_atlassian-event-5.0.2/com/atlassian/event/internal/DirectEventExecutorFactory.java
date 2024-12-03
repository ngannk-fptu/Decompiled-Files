/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.event.internal;

import com.atlassian.event.config.EventThreadPoolConfiguration;
import com.atlassian.event.internal.AbstractEventExecutorFactory;
import com.atlassian.event.internal.EventThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class DirectEventExecutorFactory
extends AbstractEventExecutorFactory {
    public DirectEventExecutorFactory(EventThreadPoolConfiguration configuration, EventThreadFactory eventThreadFactory) {
        super(configuration, eventThreadFactory);
    }

    public DirectEventExecutorFactory(EventThreadPoolConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected BlockingQueue<Runnable> getQueue() {
        return new SynchronousQueue<Runnable>();
    }
}

