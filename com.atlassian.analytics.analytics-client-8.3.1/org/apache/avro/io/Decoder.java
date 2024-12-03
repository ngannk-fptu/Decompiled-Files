/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.avro.util.Utf8;

public abstract class Decoder {
    public abstract void readNull() throws IOException;

    public abstract boolean readBoolean() throws IOException;

    public abstract int readInt() throws IOException;

    public abstract long readLong() throws IOException;

    public abstract float readFloat() throws IOException;

    public abstract double readDouble() throws IOException;

    public abstract Utf8 readString(Utf8 var1) throws IOException;

    public abstract String readString() throws IOException;

    public abstract void skipString() throws IOException;

    public abstract ByteBuffer readBytes(ByteBuffer var1) throws IOException;

    public abstract void skipBytes() throws IOException;

    public abstract void readFixed(byte[] var1, int var2, int var3) throws IOException;

    public void readFixed(byte[] bytes) throws IOException {
        this.readFixed(bytes, 0, bytes.length);
    }

    public abstract void skipFixed(int var1) throws IOException;

    public abstract int readEnum() throws IOException;

    public abstract long readArrayStart() throws IOException;

    public abstract long arrayNext() throws IOException;

    public abstract long skipArray() throws IOException;

    public abstract long readMapStart() throws IOException;

    public abstract long mapNext() throws IOException;

    public abstract long skipMap() throws IOException;

    public abstract int readIndex() throws IOException;
}

