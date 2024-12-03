/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.event.internal;

import com.google.common.base.Preconditions;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class EventThreadFactory
implements ThreadFactory {
    private final ThreadFactory delegateThreadFactory;

    public EventThreadFactory() {
        this(Executors.defaultThreadFactory());
    }

    public EventThreadFactory(ThreadFactory delegateThreadFactory) {
        this.delegateThreadFactory = (ThreadFactory)Preconditions.checkNotNull((Object)delegateThreadFactory);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = this.delegateThreadFactory.newThread(r);
        thread.setName("AtlassianEvent::" + thread.getName());
        return thread;
    }
}

