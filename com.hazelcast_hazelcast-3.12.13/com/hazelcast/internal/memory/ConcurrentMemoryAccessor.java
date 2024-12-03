/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory;

import com.hazelcast.internal.memory.MemoryAccessor;

public interface ConcurrentMemoryAccessor
extends MemoryAccessor {
    public boolean getBooleanVolatile(long var1);

    public void putBooleanVolatile(long var1, boolean var3);

    public byte getByteVolatile(long var1);

    public void putByteVolatile(long var1, byte var3);

    public char getCharVolatile(long var1);

    public void putCharVolatile(long var1, char var3);

    public short getShortVolatile(long var1);

    public void putShortVolatile(long var1, short var3);

    public int getIntVolatile(long var1);

    public void putIntVolatile(long var1, int var3);

    public float getFloatVolatile(long var1);

    public void putFloatVolatile(long var1, float var3);

    public long getLongVolatile(long var1);

    public void putLongVolatile(long var1, long var3);

    public double getDoubleVolatile(long var1);

    public void putDoubleVolatile(long var1, double var3);

    public boolean compareAndSwapInt(long var1, int var3, int var4);

    public boolean compareAndSwapLong(long var1, long var3, long var5);

    public boolean compareAndSwapObject(long var1, Object var3, Object var4);

    public void putOrderedInt(long var1, int var3);

    public void putOrderedLong(long var1, long var3);

    public void putOrderedObject(long var1, Object var3);
}

