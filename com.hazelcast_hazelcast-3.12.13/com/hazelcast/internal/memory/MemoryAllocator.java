/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory;

import com.hazelcast.nio.Disposable;

public interface MemoryAllocator
extends Disposable {
    public static final long NULL_ADDRESS = 0L;

    public long allocate(long var1);

    public long reallocate(long var1, long var3, long var5);

    public void free(long var1, long var3);
}

