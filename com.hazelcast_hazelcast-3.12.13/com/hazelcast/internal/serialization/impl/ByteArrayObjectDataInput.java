/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.internal.serialization.impl.VersionedObjectDataInput;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.collection.ArrayUtils;
import com.hazelcast.version.Version;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;

class ByteArrayObjectDataInput
extends VersionedObjectDataInput
implements BufferObjectDataInput {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    byte[] data;
    int size;
    int pos;
    int mark;
    char[] charBuffer;
    private final InternalSerializationService service;
    private final boolean bigEndian;

    ByteArrayObjectDataInput(byte[] data, InternalSerializationService service, ByteOrder byteOrder) {
        this(data, 0, service, byteOrder);
    }

    ByteArrayObjectDataInput(byte[] data, int offset, InternalSerializationService service, ByteOrder byteOrder) {
        this.data = data;
        this.size = data != null ? data.length : 0;
        this.pos = offset;
        this.service = service;
        this.bigEndian = byteOrder == ByteOrder.BIG_ENDIAN;
    }

    @Override
    public void init(byte[] data, int offset) {
        this.data = data;
        this.size = data != null ? data.length : 0;
        this.pos = offset;
    }

    @Override
    public void clear() {
        this.data = null;
        this.size = 0;
        this.pos = 0;
        this.mark = 0;
        if (this.charBuffer != null && this.charBuffer.length > 8192) {
            this.charBuffer = new char[8192];
        }
        this.version = Version.UNKNOWN;
    }

    @Override
    public int read() throws EOFException {
        return this.pos < this.size ? this.data[this.pos++] & 0xFF : -1;
    }

    @Override
    public int read(int position) throws EOFException {
        return position < this.size ? this.data[position] & 0xFF : -1;
    }

    @Override
    public final int read(byte[] b, int off, int len) throws EOFException {
        if (b == null) {
            throw new NullPointerException();
        }
        ArrayUtils.boundsCheck(b.length, off, len);
        if (len == 0) {
            return 0;
        }
        if (this.pos >= this.size) {
            return -1;
        }
        if (this.pos + len > this.size) {
            len = this.size - this.pos;
        }
        System.arraycopy(this.data, this.pos, b, off, len);
        this.pos += len;
        return len;
    }

    @Override
    public final boolean readBoolean() throws EOFException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch != 0;
    }

    @Override
    public final boolean readBoolean(int position) throws EOFException {
        int ch = this.read(position);
        if (ch < 0) {
            throw new EOFException();
        }
        return ch != 0;
    }

    @Override
    public final byte readByte() throws EOFException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte)ch;
    }

    @Override
    public final byte readByte(int position) throws EOFException {
        int ch = this.read(position);
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte)ch;
    }

    @Override
    public final char readChar() throws EOFException {
        char c = this.readChar(this.pos);
        this.pos += 2;
        return c;
    }

    @Override
    public char readChar(int position) throws EOFException {
        this.checkAvailable(position, 2);
        return Bits.readChar(this.data, position, this.bigEndian);
    }

    @Override
    public double readDouble() throws EOFException {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public double readDouble(int position) throws EOFException {
        return Double.longBitsToDouble(this.readLong(position));
    }

    @Override
    public double readDouble(ByteOrder byteOrder) throws EOFException {
        return Double.longBitsToDouble(this.readLong(byteOrder));
    }

    @Override
    public double readDouble(int position, ByteOrder byteOrder) throws EOFException {
        return Double.longBitsToDouble(this.readLong(position, byteOrder));
    }

    @Override
    public float readFloat() throws EOFException {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public float readFloat(int position) throws EOFException {
        return Float.intBitsToFloat(this.readInt(position));
    }

    @Override
    public float readFloat(ByteOrder byteOrder) throws EOFException {
        return Float.intBitsToFloat(this.readInt(byteOrder));
    }

    @Override
    public float readFloat(int position, ByteOrder byteOrder) throws EOFException {
        return Float.intBitsToFloat(this.readInt(position, byteOrder));
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        if (this.read(b) == -1) {
            throw new EOFException("End of stream reached");
        }
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws EOFException {
        if (this.read(b, off, len) == -1) {
            throw new EOFException("End of stream reached");
        }
    }

    @Override
    public final int readInt() throws EOFException {
        int i = this.readInt(this.pos);
        this.pos += 4;
        return i;
    }

    @Override
    public int readInt(int position) throws EOFException {
        this.checkAvailable(position, 4);
        return Bits.readInt(this.data, position, this.bigEndian);
    }

    @Override
    public final int readInt(ByteOrder byteOrder) throws EOFException {
        int i = this.readInt(this.pos, byteOrder);
        this.pos += 4;
        return i;
    }

    @Override
    public int readInt(int position, ByteOrder byteOrder) throws EOFException {
        this.checkAvailable(position, 4);
        return Bits.readInt(this.data, position, byteOrder == ByteOrder.BIG_ENDIAN);
    }

    @Override
    @Deprecated
    public final String readLine() throws EOFException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final long readLong() throws EOFException {
        long l = this.readLong(this.pos);
        this.pos += 8;
        return l;
    }

    @Override
    public long readLong(int position) throws EOFException {
        this.checkAvailable(position, 8);
        return Bits.readLong(this.data, position, this.bigEndian);
    }

    @Override
    public final long readLong(ByteOrder byteOrder) throws EOFException {
        long l = this.readLong(this.pos, byteOrder);
        this.pos += 8;
        return l;
    }

    @Override
    public long readLong(int position, ByteOrder byteOrder) throws EOFException {
        this.checkAvailable(position, 8);
        return Bits.readLong(this.data, position, byteOrder == ByteOrder.BIG_ENDIAN);
    }

    @Override
    public final short readShort() throws EOFException {
        short s = this.readShort(this.pos);
        this.pos += 2;
        return s;
    }

    @Override
    public short readShort(int position) throws EOFException {
        this.checkAvailable(position, 2);
        return Bits.readShort(this.data, position, this.bigEndian);
    }

    @Override
    public final short readShort(ByteOrder byteOrder) throws EOFException {
        short s = this.readShort(this.pos, byteOrder);
        this.pos += 2;
        return s;
    }

    @Override
    public short readShort(int position, ByteOrder byteOrder) throws EOFException {
        this.checkAvailable(position, 2);
        return Bits.readShort(this.data, position, byteOrder == ByteOrder.BIG_ENDIAN);
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
        return EMPTY_BYTE_ARRAY;
    }

    @Override
    public boolean[] readBooleanArray() throws EOFException {
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
    public char[] readCharArray() throws EOFException {
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
    public int[] readIntArray() throws EOFException {
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
    public long[] readLongArray() throws EOFException {
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
    public double[] readDoubleArray() throws EOFException {
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
    public float[] readFloatArray() throws EOFException {
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
    public short[] readShortArray() throws EOFException {
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
    public int readUnsignedByte() throws EOFException {
        return this.readByte() & 0xFF;
    }

    @Override
    public int readUnsignedShort() throws EOFException {
        return this.readShort() & 0xFFFF;
    }

    @Override
    public final String readUTF() throws IOException {
        int charCount = this.readInt();
        if (charCount == -1) {
            return null;
        }
        if (this.charBuffer == null || charCount > this.charBuffer.length) {
            this.charBuffer = new char[charCount];
        }
        for (int i = 0; i < charCount; ++i) {
            byte b = this.readByte();
            this.charBuffer[i] = b < 0 ? Bits.readUtf8Char(this, b) : (char)b;
        }
        return new String(this.charBuffer, 0, charCount);
    }

    public final Object readObject() throws EOFException {
        return this.service.readObject(this);
    }

    @Override
    public <T> T readObject(Class aClass) throws IOException {
        return this.service.readObject(this, aClass);
    }

    @Override
    public <T> T readDataAsObject() throws IOException {
        Data data = this.readData();
        return data == null ? null : (T)this.service.toObject(data);
    }

    @Override
    public final Data readData() throws IOException {
        byte[] bytes = this.readByteArray();
        return bytes == null ? null : new HeapData(bytes);
    }

    @Override
    public final long skip(long n) {
        if (n <= 0L || n >= Integer.MAX_VALUE) {
            return 0L;
        }
        return this.skipBytes((int)n);
    }

    @Override
    public final int skipBytes(int n) {
        if (n <= 0) {
            return 0;
        }
        int skip = n;
        int pos = this.position();
        if (pos + skip > this.size) {
            skip = this.size - pos;
        }
        this.position(pos + skip);
        return skip;
    }

    @Override
    public final int position() {
        return this.pos;
    }

    @Override
    public final void position(int newPos) {
        if (newPos > this.size || newPos < 0) {
            throw new IllegalArgumentException();
        }
        this.pos = newPos;
        if (this.mark > this.pos) {
            this.mark = -1;
        }
    }

    final void checkAvailable(int pos, int k) throws EOFException {
        if (pos < 0) {
            throw new IllegalArgumentException("Negative pos! -> " + pos);
        }
        if (this.size - pos < k) {
            throw new EOFException("Cannot read " + k + " bytes!");
        }
    }

    @Override
    public final int available() {
        return this.size - this.pos;
    }

    @Override
    public final boolean markSupported() {
        return true;
    }

    @Override
    public final void mark(int readlimit) {
        this.mark = this.pos;
    }

    @Override
    public final void reset() {
        this.pos = this.mark;
    }

    @Override
    public final void close() {
        this.data = null;
        this.charBuffer = null;
    }

    @Override
    public final ClassLoader getClassLoader() {
        return this.service.getClassLoader();
    }

    @Override
    public InternalSerializationService getSerializationService() {
        return this.service;
    }

    @Override
    public ByteOrder getByteOrder() {
        return this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    public String toString() {
        return "ByteArrayObjectDataInput{size=" + this.size + ", pos=" + this.pos + ", mark=" + this.mark + '}';
    }
}

