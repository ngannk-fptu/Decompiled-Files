/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.util;

import java.nio.ByteBuffer;

public interface ClientProtocolBuffer {
    public void wrap(byte[] var1);

    public byte[] byteArray();

    public int capacity();

    public long getLong(int var1);

    public int getInt(int var1);

    public short getShort(int var1);

    public byte getByte(int var1);

    public void getBytes(int var1, byte[] var2);

    public void getBytes(int var1, byte[] var2, int var3, int var4);

    public String getStringUtf8(int var1, int var2);

    public void putLong(int var1, long var2);

    public void putInt(int var1, int var2);

    public void putShort(int var1, short var2);

    public void putByte(int var1, byte var2);

    public void putBytes(int var1, byte[] var2);

    public void putBytes(int var1, byte[] var2, int var3, int var4);

    public void putBytes(int var1, ByteBuffer var2, int var3);

    public int putStringUtf8(int var1, String var2);

    public int putStringUtf8(int var1, String var2, int var3);
}

