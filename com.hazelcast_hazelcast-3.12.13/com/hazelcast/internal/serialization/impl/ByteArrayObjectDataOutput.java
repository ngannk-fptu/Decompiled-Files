/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.VersionedObjectDataOutput;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.collection.ArrayUtils;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.nio.ByteOrder;

class ByteArrayObjectDataOutput
extends VersionedObjectDataOutput
implements BufferObjectDataOutput {
    final int initialSize;
    byte[] buffer;
    int pos;
    final InternalSerializationService service;
    private final boolean isBigEndian;

    ByteArrayObjectDataOutput(int size, InternalSerializationService service, ByteOrder byteOrder) {
        this.initialSize = size;
        this.buffer = new byte[size];
        this.service = service;
        this.isBigEndian = byteOrder == ByteOrder.BIG_ENDIAN;
    }

    @Override
    public void write(int b) {
        this.ensureAvailable(1);
        this.buffer[this.pos++] = (byte)b;
    }

    @Override
    public void write(int position, int b) {
        this.buffer[position] = (byte)b;
    }

    @Override
    public void write(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        ArrayUtils.boundsCheck(b.length, off, len);
        if (len == 0) {
            return;
        }
        this.ensureAvailable(len);
        System.arraycopy(b, off, this.buffer, this.pos, len);
        this.pos += len;
    }

    @Override
    public final void writeBoolean(boolean v) throws IOException {
        this.write(v ? 1 : 0);
    }

    @Override
    public final void writeBoolean(int position, boolean v) throws IOException {
        this.write(position, v ? 1 : 0);
    }

    @Override
    public final void writeByte(int v) throws IOException {
        this.write(v);
    }

    @Override
    public final void writeZeroBytes(int count) {
        for (int k = 0; k < count; ++k) {
            this.write(0);
        }
    }

    @Override
    public final void writeByte(int position, int v) throws IOException {
        this.write(position, v);
    }

    @Override
    public final void writeBytes(String s) throws IOException {
        int len = s.length();
        this.ensureAvailable(len);
        for (int i = 0; i < len; ++i) {
            this.buffer[this.pos++] = (byte)s.charAt(i);
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.ensureAvailable(2);
        Bits.writeChar(this.buffer, this.pos, (char)v, this.isBigEndian);
        this.pos += 2;
    }

    @Override
    public void writeChar(int position, int v) throws IOException {
        Bits.writeChar(this.buffer, position, (char)v, this.isBigEndian);
    }

    @Override
    public void writeChars(String s) throws IOException {
        int len = s.length();
        this.ensureAvailable(len * 2);
        for (int i = 0; i < len; ++i) {
            char v = s.charAt(i);
            this.writeChar(this.pos, v);
            this.pos += 2;
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeDouble(int position, double v) throws IOException {
        this.writeLong(position, Double.doubleToLongBits(v));
    }

    @Override
    public void writeDouble(double v, ByteOrder byteOrder) throws IOException {
        this.writeLong(Double.doubleToLongBits(v), byteOrder);
    }

    @Override
    public void writeDouble(int position, double v, ByteOrder byteOrder) throws IOException {
        this.writeLong(position, Double.doubleToLongBits(v), byteOrder);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeFloat(int position, float v) throws IOException {
        this.writeInt(position, Float.floatToIntBits(v));
    }

    @Override
    public void writeFloat(float v, ByteOrder byteOrder) throws IOException {
        this.writeInt(Float.floatToIntBits(v), byteOrder);
    }

    @Override
    public void writeFloat(int position, float v, ByteOrder byteOrder) throws IOException {
        this.writeInt(position, Float.floatToIntBits(v), byteOrder);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.ensureAvailable(4);
        Bits.writeInt(this.buffer, this.pos, v, this.isBigEndian);
        this.pos += 4;
    }

    @Override
    public void writeInt(int position, int v) throws IOException {
        Bits.writeInt(this.buffer, position, v, this.isBigEndian);
    }

    @Override
    public void writeInt(int v, ByteOrder byteOrder) throws IOException {
        this.ensureAvailable(4);
        Bits.writeInt(this.buffer, this.pos, v, byteOrder == ByteOrder.BIG_ENDIAN);
        this.pos += 4;
    }

    @Override
    public void writeInt(int position, int v, ByteOrder byteOrder) throws IOException {
        Bits.writeInt(this.buffer, position, v, byteOrder == ByteOrder.BIG_ENDIAN);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.ensureAvailable(8);
        Bits.writeLong(this.buffer, this.pos, v, this.isBigEndian);
        this.pos += 8;
    }

    @Override
    public void writeLong(int position, long v) throws IOException {
        Bits.writeLong(this.buffer, position, v, this.isBigEndian);
    }

    @Override
    public void writeLong(long v, ByteOrder byteOrder) throws IOException {
        this.ensureAvailable(8);
        Bits.writeLong(this.buffer, this.pos, v, byteOrder == ByteOrder.BIG_ENDIAN);
        this.pos += 8;
    }

    @Override
    public void writeLong(int position, long v, ByteOrder byteOrder) throws IOException {
        Bits.writeLong(this.buffer, position, v, byteOrder == ByteOrder.BIG_ENDIAN);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.ensureAvailable(2);
        Bits.writeShort(this.buffer, this.pos, (short)v, this.isBigEndian);
        this.pos += 2;
    }

    @Override
    public void writeShort(int position, int v) throws IOException {
        Bits.writeShort(this.buffer, position, (short)v, this.isBigEndian);
    }

    @Override
    public void writeShort(int v, ByteOrder byteOrder) throws IOException {
        this.ensureAvailable(2);
        Bits.writeShort(this.buffer, this.pos, (short)v, byteOrder == ByteOrder.BIG_ENDIAN);
        this.pos += 2;
    }

    @Override
    public void writeShort(int position, int v, ByteOrder byteOrder) throws IOException {
        Bits.writeShort(this.buffer, position, (short)v, byteOrder == ByteOrder.BIG_ENDIAN);
    }

    @Override
    public void writeUTF(String str) throws IOException {
        int len = str != null ? str.length() : -1;
        this.writeInt(len);
        if (len > 0) {
            this.ensureAvailable(len * 3);
            for (int i = 0; i < len; ++i) {
                this.pos += Bits.writeUtf8Char(this.buffer, this.pos, str.charAt(i));
            }
        }
    }

    @Override
    public void writeByteArray(byte[] bytes) throws IOException {
        int len = bytes != null ? bytes.length : -1;
        this.writeInt(len);
        if (len > 0) {
            this.write(bytes);
        }
    }

    @Override
    public void writeBooleanArray(boolean[] booleans) throws IOException {
        int len = booleans != null ? booleans.length : -1;
        this.writeInt(len);
        if (len > 0) {
            for (boolean b : booleans) {
                this.writeBoolean(b);
            }
        }
    }

    @Override
    public void writeCharArray(char[] chars) throws IOException {
        int len = chars != null ? chars.length : -1;
        this.writeInt(len);
        if (len > 0) {
            for (char c : chars) {
                this.writeChar(c);
            }
        }
    }

    @Override
    public void writeIntArray(int[] ints) throws IOException {
        int len = ints != null ? ints.length : -1;
        this.writeInt(len);
        if (len > 0) {
            for (int i : ints) {
                this.writeInt(i);
            }
        }
    }

    @Override
    public void writeLongArray(long[] longs) throws IOException {
        int len = longs != null ? longs.length : -1;
        this.writeInt(len);
        if (len > 0) {
            for (long l : longs) {
                this.writeLong(l);
            }
        }
    }

    @Override
    public void writeDoubleArray(double[] doubles) throws IOException {
        int len = doubles != null ? doubles.length : -1;
        this.writeInt(len);
        if (len > 0) {
            for (double d : doubles) {
                this.writeDouble(d);
            }
        }
    }

    @Override
    public void writeFloatArray(float[] floats) throws IOException {
        int len = floats != null ? floats.length : -1;
        this.writeInt(len);
        if (len > 0) {
            for (float f : floats) {
                this.writeFloat(f);
            }
        }
    }

    @Override
    public void writeShortArray(short[] shorts) throws IOException {
        int len = shorts != null ? shorts.length : -1;
        this.writeInt(len);
        if (len > 0) {
            for (short s : shorts) {
                this.writeShort(s);
            }
        }
    }

    @Override
    public void writeUTFArray(String[] strings) throws IOException {
        int len = strings != null ? strings.length : -1;
        this.writeInt(len);
        if (len > 0) {
            for (String s : strings) {
                this.writeUTF(s);
            }
        }
    }

    final void ensureAvailable(int len) {
        if (this.available() < len) {
            if (this.buffer != null) {
                int newCap = Math.max(this.buffer.length << 1, this.buffer.length + len);
                byte[] newBuffer = new byte[newCap];
                System.arraycopy(this.buffer, 0, newBuffer, 0, this.pos);
                this.buffer = newBuffer;
            } else {
                this.buffer = new byte[len > this.initialSize / 2 ? len * 2 : this.initialSize];
            }
        }
    }

    @Override
    public void writeObject(Object object) throws IOException {
        this.service.writeObject(this, object);
    }

    @Override
    public void writeData(Data data) throws IOException {
        int len = data == null ? -1 : data.totalSize();
        this.writeInt(len);
        if (len > 0) {
            this.ensureAvailable(len);
            data.copyTo(this.buffer, this.pos);
            this.pos += len;
        }
    }

    @Override
    public final int position() {
        return this.pos;
    }

    @Override
    public void position(int newPos) {
        if (newPos > this.buffer.length || newPos < 0) {
            throw new IllegalArgumentException();
        }
        this.pos = newPos;
    }

    public int available() {
        return this.buffer != null ? this.buffer.length - this.pos : 0;
    }

    @Override
    public byte[] toByteArray() {
        return this.toByteArray(0);
    }

    @Override
    public byte[] toByteArray(int padding) {
        if (this.buffer == null || this.pos == 0) {
            return new byte[padding];
        }
        byte[] newBuffer = new byte[padding + this.pos];
        System.arraycopy(this.buffer, 0, newBuffer, padding, this.pos);
        return newBuffer;
    }

    @Override
    public void clear() {
        this.pos = 0;
        if (this.buffer != null && this.buffer.length > this.initialSize * 8) {
            this.buffer = new byte[this.initialSize * 8];
        }
        this.version = Version.UNKNOWN;
    }

    @Override
    public void close() {
        this.pos = 0;
        this.buffer = null;
    }

    @Override
    public ByteOrder getByteOrder() {
        return this.isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public SerializationService getSerializationService() {
        return this.service;
    }

    public String toString() {
        return "ByteArrayObjectDataOutput{size=" + (this.buffer != null ? this.buffer.length : 0) + ", pos=" + this.pos + '}';
    }
}

