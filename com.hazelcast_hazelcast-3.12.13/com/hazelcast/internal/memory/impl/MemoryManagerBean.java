/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory.impl;

import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.memory.MemoryAllocator;
import com.hazelcast.internal.memory.MemoryManager;

public class MemoryManagerBean
implements MemoryManager {
    private final MemoryAllocator allocator;
    private final MemoryAccessor accessor;

    public MemoryManagerBean(MemoryAllocator allocator, MemoryAccessor accessor) {
        this.allocator = allocator;
        this.accessor = accessor;
    }

    @Override
    public MemoryAllocator getAllocator() {
        return this.allocator;
    }

    @Override
    public MemoryAccessor getAccessor() {
        return this.accessor;
    }

    @Override
    public void dispose() {
        this.allocator.dispose();
    }
}

