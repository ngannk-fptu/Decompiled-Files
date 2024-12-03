/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import com.hazelcast.util.executor.AbstractExecutorThreadFactory;
import com.hazelcast.util.executor.HazelcastManagedThread;

public final class SingleExecutorThreadFactory
extends AbstractExecutorThreadFactory {
    private final String threadName;

    public SingleExecutorThreadFactory(ClassLoader classLoader, String threadName) {
        super(classLoader);
        this.threadName = threadName;
    }

    @Override
    protected Thread createThread(Runnable r) {
        return new ManagedThread(r);
    }

    private class ManagedThread
    extends HazelcastManagedThread {
        public ManagedThread(Runnable target) {
            super(target, SingleExecutorThreadFactory.this.threadName);
        }
    }
}

