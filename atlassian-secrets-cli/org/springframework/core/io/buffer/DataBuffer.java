/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io.buffer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.IntPredicate;
import org.springframework.core.io.buffer.DataBufferFactory;

public interface DataBuffer {
    public DataBufferFactory factory();

    public int indexOf(IntPredicate var1, int var2);

    public int lastIndexOf(IntPredicate var1, int var2);

    public int readableByteCount();

    public int writableByteCount();

    public int capacity();

    public DataBuffer capacity(int var1);

    public int readPosition();

    public DataBuffer readPosition(int var1);

    public int writePosition();

    public DataBuffer writePosition(int var1);

    public byte getByte(int var1);

    public byte read();

    public DataBuffer read(byte[] var1);

    public DataBuffer read(byte[] var1, int var2, int var3);

    public DataBuffer write(byte var1);

    public DataBuffer write(byte[] var1);

    public DataBuffer write(byte[] var1, int var2, int var3);

    public DataBuffer write(DataBuffer ... var1);

    public DataBuffer write(ByteBuffer ... var1);

    public DataBuffer slice(int var1, int var2);

    public ByteBuffer asByteBuffer();

    public ByteBuffer asByteBuffer(int var1, int var2);

    public InputStream asInputStream();

    public InputStream asInputStream(boolean var1);

    public OutputStream asOutputStream();
}

