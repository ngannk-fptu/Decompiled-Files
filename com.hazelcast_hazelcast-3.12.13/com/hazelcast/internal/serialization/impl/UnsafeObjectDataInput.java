/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import com.hazelcast.internal.memory.HeapMemoryAccessor;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.ByteArrayObjectDataInput;
import java.io.EOFException;
import java.nio.ByteOrder;

class UnsafeObjectDataInput
extends ByteArrayObjectDataInput {
    UnsafeObjectDataInput(byte[] buffer, InternalSerializationService service) {
        super(buffer, service, ByteOrder.nativeOrder());
    }

    UnsafeObjectDataInput(byte[] buffer, int offset, InternalSerializationService service) {
        super(buffer, offset, service, ByteOrder.nativeOrder());
    }

    @Override
    public int read() {
        return this.pos < this.size ? GlobalMemoryAccessorRegistry.MEM.getByte(this.data, (long)(HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + this.pos++)) & 0xFF : -1;
    }

    @Override
    public int read(int position) {
        return position < this.size ? (int)GlobalMemoryAccessorRegistry.MEM.getByte(this.data, (long)(HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position)) : -1;
    }

    @Override
    public char readChar(int position) throws EOFException {
        this.checkAvailable(position, 2);
        return GlobalMemoryAccessorRegistry.MEM.getChar(this.data, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position);
    }

    @Override
    public double readDouble() throws EOFException {
        double d = this.readDouble(this.pos);
        this.pos += 8;
        return d;
    }

    @Override
    public double readDouble(int position) throws EOFException {
        this.checkAvailable(position, 8);
        return GlobalMemoryAccessorRegistry.MEM.getDouble(this.data, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position);
    }

    @Override
    public float readFloat() throws EOFException {
        float f = this.readFloat(this.pos);
        this.pos += 4;
        return f;
    }

    @Override
    public float readFloat(int position) throws EOFException {
        this.checkAvailable(position, 4);
        return GlobalMemoryAccessorRegistry.MEM.getFloat(this.data, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position);
    }

    @Override
    public int readInt(int position) throws EOFException {
        this.checkAvailable(position, 4);
        return GlobalMemoryAccessorRegistry.MEM.getInt(this.data, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position);
    }

    @Override
    public int readInt(int position, ByteOrder byteOrder) throws EOFException {
        int v = this.readInt(position);
        if (byteOrder != ByteOrder.nativeOrder()) {
            v = Integer.reverseBytes(v);
        }
        return v;
    }

    @Override
    public long readLong(int position) throws EOFException {
        this.checkAvailable(position, 8);
        return GlobalMemoryAccessorRegistry.MEM.getLong(this.data, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position);
    }

    @Override
    public long readLong(int position, ByteOrder byteOrder) throws EOFException {
        long v = this.readLong(position);
        if (byteOrder != ByteOrder.nativeOrder()) {
            v = Long.reverseBytes(v);
        }
        return v;
    }

    @Override
    public short readShort(int position) throws EOFException {
        this.checkAvailable(position, 2);
        return GlobalMemoryAccessorRegistry.MEM.getShort(this.data, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position);
    }

    @Override
    public short readShort(int position, ByteOrder byteOrder) throws EOFException {
        short v = this.readShort(position);
        if (byteOrder != ByteOrder.nativeOrder()) {
            v = Short.reverseBytes(v);
        }
        return v;
    }

    @Override
    public char[] readCharArray() throws EOFException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            char[] values = new char[len];
            this.memCopy(values, HeapMemoryAccessor.ARRAY_CHAR_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_CHAR_INDEX_SCALE);
            return values;
        }
        return new char[0];
    }

    @Override
    public boolean[] readBooleanArray() throws EOFException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            boolean[] values = new boolean[len];
            this.memCopy(values, HeapMemoryAccessor.ARRAY_BOOLEAN_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_BOOLEAN_INDEX_SCALE);
            return values;
        }
        return new boolean[0];
    }

    @Override
    public byte[] readByteArray() throws EOFException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            byte[] values = new byte[len];
            this.memCopy(values, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_BYTE_INDEX_SCALE);
            return values;
        }
        return new byte[0];
    }

    @Override
    public int[] readIntArray() throws EOFException {
        int len = this.readInt();
        if (len == -1) {
            return null;
        }
        if (len > 0) {
            int[] values = new int[len];
            this.memCopy(values, HeapMemoryAccessor.ARRAY_INT_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_INT_INDEX_SCALE);
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
            this.memCopy(values, HeapMemoryAccessor.ARRAY_LONG_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_LONG_INDEX_SCALE);
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
            this.memCopy(values, HeapMemoryAccessor.ARRAY_DOUBLE_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_DOUBLE_INDEX_SCALE);
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
            this.memCopy(values, HeapMemoryAccessor.ARRAY_FLOAT_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_FLOAT_INDEX_SCALE);
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
            this.memCopy(values, HeapMemoryAccessor.ARRAY_SHORT_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_SHORT_INDEX_SCALE);
            return values;
        }
        return new short[0];
    }

    private void memCopy(Object dest, long destOffset, int length, int indexScale) throws EOFException {
        if (length < 0) {
            throw new NegativeArraySizeException("Destination length is negative: " + length);
        }
        int remaining = length * indexScale;
        this.checkAvailable(this.pos, remaining);
        long offset = destOffset;
        while (remaining > 0) {
            int chunk = remaining > 0x100000 ? 0x100000 : remaining;
            GlobalMemoryAccessorRegistry.MEM.copyMemory(this.data, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + this.pos, dest, offset, chunk);
            remaining -= chunk;
            offset += (long)chunk;
            this.pos += chunk;
        }
    }

    @Override
    public ByteOrder getByteOrder() {
        return ByteOrder.nativeOrder();
    }

    @Override
    public String toString() {
        return "UnsafeObjectDataInput{size=" + this.size + ", pos=" + this.pos + ", mark=" + this.mark + ", byteOrder=" + this.getByteOrder() + '}';
    }
}

