/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.internal.serialization.impl.VersionedObjectDataInput;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.serialization.Data;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class ObjectDataInputStream
extends VersionedObjectDataInput
implements Closeable {
    private static final int SHORT_MASK = 65535;
    private final InternalSerializationService serializationService;
    private final DataInputStream dataInput;
    private final ByteOrder byteOrder;

    public ObjectDataInputStream(InputStream in, InternalSerializationService serializationService) {
        this.serializationService = serializationService;
        this.dataInput = new DataInputStream(in);
        this.byteOrder = serializationService.getByteOrder();
    }

    @Override
    public int read() throws IOException {
        return this.readByte();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.dataInput.skip(n);
    }

    @Override
    public int available() throws IOException {
        return this.dataInput.available();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.dataInput.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.dataInput.read(b, off, len);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.dataInput.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.dataInput.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.dataInput.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.dataInput.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.dataInput.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.dataInput.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        short v = this.dataInput.readShort();
        return this.bigEndian() ? v : Short.reverseBytes(v);
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.readShort() & 0xFFFF;
    }

    @Override
    public char readChar() throws IOException {
        char v = this.dataInput.readChar();
        return this.bigEndian() ? v : Character.reverseBytes(v);
    }

    @Override
    public int readInt() throws IOException {
        int v = this.dataInput.readInt();
        return this.bigEndian() ? v : Integer.reverseBytes(v);
    }

    @Override
    public long readLong() throws IOException {
        long v = this.dataInput.readLong();
        return this.bigEndian() ? v : Long.reverseBytes(v);
    }

    @Override
    public float readFloat() throws IOException {
        if (this.bigEndian()) {
            return this.dataInput.readFloat();
        }
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() throws IOException {
        if (this.bigEndian()) {
            return this.dataInput.readDouble();
        }
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public byte[] readByteArray() throws IOException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            byte[] b = new byte[len];
            this.readFully(b);
            return b;
        }
        return new byte[0];
    }

    @Override
    public boolean[] readBooleanArray() throws IOException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            boolean[] values = new boolean[len];
            for (int i = 0; i < len; ++i) {
                values[i] = this.readBoolean();
            }
            return values;
        }
        return new boolean[0];
    }

    @Override
    public char[] readCharArray() throws IOException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            char[] values = new char[len];
            for (int i = 0; i < len; ++i) {
                values[i] = this.readChar();
            }
            return values;
        }
        return new char[0];
    }

    @Override
    public int[] readIntArray() throws IOException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            int[] values = new int[len];
            for (int i = 0; i < len; ++i) {
                values[i] = this.readInt();
            }
            return values;
        }
        return new int[0];
    }

    @Override
    public long[] readLongArray() throws IOException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            long[] values = new long[len];
            for (int i = 0; i < len; ++i) {
                values[i] = this.readLong();
            }
            return values;
        }
        return new long[0];
    }

    @Override
    public double[] readDoubleArray() throws IOException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            double[] values = new double[len];
            for (int i = 0; i < len; ++i) {
                values[i] = this.readDouble();
            }
            return values;
        }
        return new double[0];
    }

    @Override
    public float[] readFloatArray() throws IOException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            float[] values = new float[len];
            for (int i = 0; i < len; ++i) {
                values[i] = this.readFloat();
            }
            return values;
        }
        return new float[0];
    }

    @Override
    public short[] readShortArray() throws IOException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            short[] values = new short[len];
            for (int i = 0; i < len; ++i) {
                values[i] = this.readShort();
            }
            return values;
        }
        return new short[0];
    }

    @Override
    public String[] readUTFArray() throws IOException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            String[] values = new String[len];
            for (int i = 0; i < len; ++i) {
                values[i] = this.readUTF();
            }
            return values;
        }
        return new String[0];
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return this.dataInput.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        int charCount = this.readInt();
        if (charCount == -1) {
            return null;
        }
        char[] charBuffer = new char[charCount];
        for (int i = 0; i < charCount; ++i) {
            byte b = this.dataInput.readByte();
            charBuffer[i] = b < 0 ? Bits.readUtf8Char(this.dataInput, b) : (char)b;
        }
        return new String(charBuffer, 0, charCount);
    }

    @Override
    public void close() throws IOException {
        this.dataInput.close();
    }

    @Override
    public void mark(int readlimit) {
        this.dataInput.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        this.dataInput.reset();
    }

    @Override
    public boolean markSupported() {
        return this.dataInput.markSupported();
    }

    public Object readObject() throws IOException {
        return this.serializationService.readObject(this);
    }

    @Override
    public <T> T readDataAsObject() throws IOException {
        Data data = this.readData();
        return data == null ? null : (T)this.serializationService.toObject(data);
    }

    public Object readObject(Class aClass) throws IOException {
        return this.serializationService.readObject(this, aClass);
    }

    @Override
    public Data readData() throws IOException {
        byte[] bytes = this.readByteArray();
        return bytes == null ? null : new HeapData(bytes);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.serializationService.getClassLoader();
    }

    @Override
    public InternalSerializationService getSerializationService() {
        return this.serializationService;
    }

    @Override
    public ByteOrder getByteOrder() {
        return this.byteOrder;
    }

    private boolean bigEndian() {
        return this.byteOrder == ByteOrder.BIG_ENDIAN;
    }
}

