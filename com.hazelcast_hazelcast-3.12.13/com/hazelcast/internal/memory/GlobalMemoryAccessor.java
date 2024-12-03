/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory;

import com.hazelcast.internal.memory.ByteAccessStrategy;
import com.hazelcast.internal.memory.ConcurrentHeapMemoryAccessor;
import com.hazelcast.internal.memory.ConcurrentMemoryAccessor;

public interface GlobalMemoryAccessor
extends ConcurrentMemoryAccessor,
ConcurrentHeapMemoryAccessor,
ByteAccessStrategy<Object> {
    public static final int MEM_COPY_THRESHOLD = 0x100000;
}

