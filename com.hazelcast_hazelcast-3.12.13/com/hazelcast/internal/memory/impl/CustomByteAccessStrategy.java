/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory.impl;

import com.hazelcast.internal.memory.ByteAccessStrategy;
import com.hazelcast.internal.memory.MemoryAccessor;

public final class CustomByteAccessStrategy
implements ByteAccessStrategy<MemoryAccessor> {
    public static final CustomByteAccessStrategy INSTANCE = new CustomByteAccessStrategy();

    private CustomByteAccessStrategy() {
    }

    @Override
    public byte getByte(MemoryAccessor mem, long offset) {
        return mem.getByte(offset);
    }

    @Override
    public void putByte(MemoryAccessor mem, long offset, byte value) {
        mem.putByte(offset, value);
    }
}

