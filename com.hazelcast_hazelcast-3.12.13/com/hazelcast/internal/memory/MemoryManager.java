/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory;

import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.memory.MemoryAllocator;
import com.hazelcast.nio.Disposable;

public interface MemoryManager
extends Disposable {
    public MemoryAllocator getAllocator();

    public MemoryAccessor getAccessor();
}

