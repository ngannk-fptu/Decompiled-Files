/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.version.Version;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteOrder;

@PrivateApi
public interface BufferObjectDataOutput
extends ObjectDataOutput,
Closeable {
    public static final int UTF_BUFFER_SIZE = 1024;

    public void write(int var1, int var2);

    public void writeInt(int var1, int var2) throws IOException;

    public void writeInt(int var1, ByteOrder var2) throws IOException;

    public void writeInt(int var1, int var2, ByteOrder var3) throws IOException;

    public void writeLong(int var1, long var2) throws IOException;

    public void writeLong(long var1, ByteOrder var3) throws IOException;

    public void writeLong(int var1, long var2, ByteOrder var4) throws IOException;

    public void writeBoolean(int var1, boolean var2) throws IOException;

    public void writeByte(int var1, int var2) throws IOException;

    public void writeZeroBytes(int var1);

    public void writeChar(int var1, int var2) throws IOException;

    public void writeDouble(int var1, double var2) throws IOException;

    public void writeDouble(double var1, ByteOrder var3) throws IOException;

    public void writeDouble(int var1, double var2, ByteOrder var4) throws IOException;

    public void writeFloat(int var1, float var2) throws IOException;

    public void writeFloat(float var1, ByteOrder var2) throws IOException;

    public void writeFloat(int var1, float var2, ByteOrder var3) throws IOException;

    public void writeShort(int var1, int var2) throws IOException;

    public void writeShort(int var1, ByteOrder var2) throws IOException;

    public void writeShort(int var1, int var2, ByteOrder var3) throws IOException;

    public int position();

    public void position(int var1);

    public void clear();

    public void setVersion(Version var1);
}

