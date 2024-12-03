/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.executor;

import com.hazelcast.internal.util.executor.UnblockableThread;
import com.hazelcast.util.executor.PoolExecutorThreadFactory;

public class UnblockablePoolExecutorThreadFactory
extends PoolExecutorThreadFactory {
    public UnblockablePoolExecutorThreadFactory(String threadNamePrefix, ClassLoader classLoader) {
        super(threadNamePrefix, classLoader);
    }

    @Override
    protected PoolExecutorThreadFactory.ManagedThread createThread(Runnable r, String name, int id) {
        return new UnblockableManagedThread(r, name, id);
    }

    private class UnblockableManagedThread
    extends PoolExecutorThreadFactory.ManagedThread
    implements UnblockableThread {
        UnblockableManagedThread(Runnable target, String name, int id) {
            super(UnblockablePoolExecutorThreadFactory.this, target, name, id);
        }
    }
}

