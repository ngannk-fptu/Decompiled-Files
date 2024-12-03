/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory;

import com.hazelcast.internal.memory.ByteAccessStrategy;
import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import java.lang.reflect.Field;

public interface HeapMemoryAccessor
extends ByteAccessStrategy<Object> {
    public static final int ARRAY_BOOLEAN_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(boolean[].class) : -1;
    public static final int ARRAY_BYTE_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(byte[].class) : -1;
    public static final int ARRAY_SHORT_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(short[].class) : -1;
    public static final int ARRAY_CHAR_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(char[].class) : -1;
    public static final int ARRAY_INT_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(int[].class) : -1;
    public static final int ARRAY_FLOAT_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(float[].class) : -1;
    public static final int ARRAY_LONG_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(long[].class) : -1;
    public static final int ARRAY_DOUBLE_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(double[].class) : -1;
    public static final int ARRAY_OBJECT_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(Object[].class) : -1;
    public static final int ARRAY_BOOLEAN_INDEX_SCALE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(boolean[].class) : -1;
    public static final int ARRAY_BYTE_INDEX_SCALE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(byte[].class) : -1;
    public static final int ARRAY_SHORT_INDEX_SCALE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(short[].class) : -1;
    public static final int ARRAY_CHAR_INDEX_SCALE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(char[].class) : -1;
    public static final int ARRAY_INT_INDEX_SCALE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(int[].class) : -1;
    public static final int ARRAY_FLOAT_INDEX_SCALE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(float[].class) : -1;
    public static final int ARRAY_LONG_INDEX_SCALE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(long[].class) : -1;
    public static final int ARRAY_DOUBLE_INDEX_SCALE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(double[].class) : -1;
    public static final int ARRAY_OBJECT_INDEX_SCALE = GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? GlobalMemoryAccessorRegistry.MEM.arrayIndexScale(Object[].class) : -1;

    public long objectFieldOffset(Field var1);

    public int arrayBaseOffset(Class<?> var1);

    public int arrayIndexScale(Class<?> var1);

    public void copyMemory(Object var1, long var2, Object var4, long var5, long var7);

    public Object getObject(Object var1, long var2);

    public void putObject(Object var1, long var2, Object var4);

    public boolean getBoolean(Object var1, long var2);

    public void putBoolean(Object var1, long var2, boolean var4);

    @Override
    public byte getByte(Object var1, long var2);

    @Override
    public void putByte(Object var1, long var2, byte var4);

    public char getChar(Object var1, long var2);

    public void putChar(Object var1, long var2, char var4);

    public short getShort(Object var1, long var2);

    public void putShort(Object var1, long var2, short var4);

    public int getInt(Object var1, long var2);

    public void putInt(Object var1, long var2, int var4);

    public float getFloat(Object var1, long var2);

    public void putFloat(Object var1, long var2, float var4);

    public long getLong(Object var1, long var2);

    public void putLong(Object var1, long var2, long var4);

    public double getDouble(Object var1, long var2);

    public void putDouble(Object var1, long var2, double var4);
}

