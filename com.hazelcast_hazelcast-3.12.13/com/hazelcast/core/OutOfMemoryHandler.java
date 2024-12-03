/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.OutOfMemoryHandlerHelper;

public abstract class OutOfMemoryHandler {
    public abstract void onOutOfMemory(OutOfMemoryError var1, HazelcastInstance[] var2);

    public boolean shouldHandle(OutOfMemoryError oome) {
        return true;
    }

    protected final void tryCloseConnections(HazelcastInstance hazelcastInstance) {
        OutOfMemoryHandlerHelper.tryCloseConnections(hazelcastInstance);
    }

    protected final void tryShutdown(HazelcastInstance hazelcastInstance) {
        OutOfMemoryHandlerHelper.tryShutdown(hazelcastInstance);
    }
}

