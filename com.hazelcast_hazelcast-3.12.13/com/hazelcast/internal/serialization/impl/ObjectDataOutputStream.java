/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.VersionedObjectDataOutput;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

public class ObjectDataOutputStream
extends VersionedObjectDataOutput
implements ObjectDataOutput,
Closeable {
    private final InternalSerializationService serializationService;
    private final DataOutputStream dataOut;
    private final ByteOrder byteOrder;

    public ObjectDataOutputStream(OutputStream outputStream, InternalSerializationService serializationService) {
        this.serializationService = serializationService;
        this.dataOut = new DataOutputStream(outputStream);
        this.byteOrder = serializationService.getByteOrder();
    }

    @Override
    public void write(int b) throws IOException {
        this.dataOut.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.dataOut.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.dataOut.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.dataOut.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (this.bigEndian()) {
            this.dataOut.writeShort(v);
        } else {
            this.dataOut.writeShort(Short.reverseBytes((short)v));
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        if (this.bigEndian()) {
            this.dataOut.writeChar(v);
        } else {
            this.dataOut.writeChar(Character.reverseBytes((char)v));
        }
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (this.bigEndian()) {
            this.dataOut.writeInt(v);
        } else {
            this.dataOut.writeInt(Integer.reverseBytes(v));
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (this.bigEndian()) {
            this.dataOut.writeLong(v);
        } else {
            this.dataOut.writeLong(Long.reverseBytes(v));
        }
    }

    @Override
    public void writeFloat(float v) throws IOException {
        if (this.bigEndian()) {
            this.dataOut.writeFloat(v);
        } else {
            this.writeInt(Float.floatToIntBits(v));
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        if (this.bigEndian()) {
            this.dataOut.writeDouble(v);
        } else {
            this.writeLong(Double.doubleToLongBits(v));
        }
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.dataOut.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            char v = s.charAt(i);
            this.writeChar(v);
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
            for (boolean c : booleans) {
                this.writeBoolean(c);
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

    @Override
    public void writeUTF(String str) throws IOException {
        int len = str != null ? str.length() : -1;
        this.writeInt(len);
        if (len > 0) {
            byte[] buffer = new byte[3];
            for (int i = 0; i < len; ++i) {
                int count = Bits.writeUtf8Char(buffer, 0, str.charAt(i));
                this.dataOut.write(buffer, 0, count);
            }
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.dataOut.write(b);
    }

    @Override
    public void writeObject(Object object) throws IOException {
        this.serializationService.writeObject(this, object);
    }

    @Override
    public void writeData(Data data) throws IOException {
        byte[] payload = data != null ? data.toByteArray() : null;
        this.writeByteArray(payload);
    }

    @Override
    public byte[] toByteArray() {
        return this.toByteArray(0);
    }

    @Override
    public byte[] toByteArray(int padding) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() throws IOException {
        this.dataOut.flush();
    }

    @Override
    public void close() throws IOException {
        this.dataOut.close();
    }

    @Override
    public ByteOrder getByteOrder() {
        return this.byteOrder;
    }

    @Override
    public SerializationService getSerializationService() {
        return this.serializationService;
    }

    private boolean bigEndian() {
        return this.byteOrder == ByteOrder.BIG_ENDIAN;
    }
}

