/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory;

import com.hazelcast.internal.memory.HeapMemoryAccessor;

public interface ConcurrentHeapMemoryAccessor
extends HeapMemoryAccessor {
    public boolean getBooleanVolatile(Object var1, long var2);

    public void putBooleanVolatile(Object var1, long var2, boolean var4);

    public byte getByteVolatile(Object var1, long var2);

    public void putByteVolatile(Object var1, long var2, byte var4);

    public char getCharVolatile(Object var1, long var2);

    public void putCharVolatile(Object var1, long var2, char var4);

    public short getShortVolatile(Object var1, long var2);

    public void putShortVolatile(Object var1, long var2, short var4);

    public int getIntVolatile(Object var1, long var2);

    public void putIntVolatile(Object var1, long var2, int var4);

    public float getFloatVolatile(Object var1, long var2);

    public void putFloatVolatile(Object var1, long var2, float var4);

    public long getLongVolatile(Object var1, long var2);

    public void putLongVolatile(Object var1, long var2, long var4);

    public double getDoubleVolatile(Object var1, long var2);

    public void putDoubleVolatile(Object var1, long var2, double var4);

    public Object getObjectVolatile(Object var1, long var2);

    public void putObjectVolatile(Object var1, long var2, Object var4);

    public boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);

    public boolean compareAndSwapLong(Object var1, long var2, long var4, long var6);

    public boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5);

    public void putOrderedInt(Object var1, long var2, int var4);

    public void putOrderedLong(Object var1, long var2, long var4);

    public void putOrderedObject(Object var1, long var2, Object var4);
}

