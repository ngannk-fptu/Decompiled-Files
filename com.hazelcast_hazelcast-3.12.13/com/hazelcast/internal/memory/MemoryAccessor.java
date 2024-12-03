/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory;

public interface MemoryAccessor {
    public boolean isBigEndian();

    public boolean getBoolean(long var1);

    public void putBoolean(long var1, boolean var3);

    public byte getByte(long var1);

    public void putByte(long var1, byte var3);

    public char getChar(long var1);

    public void putChar(long var1, char var3);

    public short getShort(long var1);

    public void putShort(long var1, short var3);

    public int getInt(long var1);

    public void putInt(long var1, int var3);

    public float getFloat(long var1);

    public void putFloat(long var1, float var3);

    public long getLong(long var1);

    public void putLong(long var1, long var3);

    public double getDouble(long var1);

    public void putDouble(long var1, double var3);

    public void copyMemory(long var1, long var3, long var5);

    public void copyFromByteArray(byte[] var1, int var2, long var3, int var5);

    public void copyToByteArray(long var1, byte[] var3, int var4, int var5);

    public void setMemory(long var1, long var3, byte var5);
}

