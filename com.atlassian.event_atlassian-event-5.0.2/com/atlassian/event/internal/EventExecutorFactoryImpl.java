/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.event.internal;

import com.atlassian.event.config.EventThreadPoolConfiguration;
import com.atlassian.event.internal.DirectEventExecutorFactory;
import com.atlassian.event.internal.EventThreadFactory;

@Deprecated
public class EventExecutorFactoryImpl
extends DirectEventExecutorFactory {
    public EventExecutorFactoryImpl(EventThreadPoolConfiguration configuration, EventThreadFactory eventThreadFactory) {
        super(configuration, eventThreadFactory);
    }

    public EventExecutorFactoryImpl(EventThreadPoolConfiguration configuration) {
        super(configuration);
    }
}

