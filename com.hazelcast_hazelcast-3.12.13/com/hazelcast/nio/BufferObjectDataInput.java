/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.version.Version;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteOrder;

@PrivateApi
public interface BufferObjectDataInput
extends ObjectDataInput,
Closeable {
    public static final int UTF_BUFFER_SIZE = 1024;

    public int read(int var1) throws IOException;

    public int readInt(int var1) throws IOException;

    public int readInt(ByteOrder var1) throws IOException;

    public int readInt(int var1, ByteOrder var2) throws IOException;

    public long readLong(int var1) throws IOException;

    public long readLong(ByteOrder var1) throws IOException;

    public long readLong(int var1, ByteOrder var2) throws IOException;

    public boolean readBoolean(int var1) throws IOException;

    public byte readByte(int var1) throws IOException;

    public char readChar(int var1) throws IOException;

    public double readDouble(int var1) throws IOException;

    public double readDouble(ByteOrder var1) throws IOException;

    public double readDouble(int var1, ByteOrder var2) throws IOException;

    public float readFloat(int var1) throws IOException;

    public float readFloat(ByteOrder var1) throws IOException;

    public float readFloat(int var1, ByteOrder var2) throws IOException;

    public short readShort(int var1) throws IOException;

    public short readShort(ByteOrder var1) throws IOException;

    public short readShort(int var1, ByteOrder var2) throws IOException;

    public int position();

    public void position(int var1);

    public void reset();

    public void clear();

    public void init(byte[] var1, int var2);

    public void setVersion(Version var1);
}

