/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import com.hazelcast.internal.memory.HeapMemoryAccessor;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.ByteArrayObjectDataOutput;
import java.io.IOException;
import java.nio.ByteOrder;

class UnsafeObjectDataOutput
extends ByteArrayObjectDataOutput {
    UnsafeObjectDataOutput(int size, InternalSerializationService service) {
        super(size, service, ByteOrder.nativeOrder());
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.ensureAvailable(2);
        GlobalMemoryAccessorRegistry.MEM.putChar(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + this.pos, (char)v);
        this.pos += 2;
    }

    @Override
    public void writeChar(int position, int v) throws IOException {
        this.checkAvailable(position, 2);
        GlobalMemoryAccessorRegistry.MEM.putChar(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position, (char)v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.ensureAvailable(8);
        GlobalMemoryAccessorRegistry.MEM.putDouble(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + this.pos, v);
        this.pos += 8;
    }

    @Override
    public void writeDouble(int position, double v) throws IOException {
        this.checkAvailable(position, 8);
        GlobalMemoryAccessorRegistry.MEM.putDouble(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position, v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.ensureAvailable(4);
        GlobalMemoryAccessorRegistry.MEM.putFloat(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + this.pos, v);
        this.pos += 4;
    }

    @Override
    public void writeFloat(int position, float v) throws IOException {
        this.checkAvailable(position, 4);
        GlobalMemoryAccessorRegistry.MEM.putFloat(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position, v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.ensureAvailable(4);
        GlobalMemoryAccessorRegistry.MEM.putInt(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + this.pos, v);
        this.pos += 4;
    }

    @Override
    public void writeInt(int position, int v) throws IOException {
        this.checkAvailable(position, 4);
        GlobalMemoryAccessorRegistry.MEM.putInt(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position, v);
    }

    @Override
    public void writeInt(int v, ByteOrder byteOrder) throws IOException {
        if (byteOrder != ByteOrder.nativeOrder()) {
            this.writeInt(Integer.reverseBytes(v));
        } else {
            this.writeInt(v);
        }
    }

    @Override
    public void writeInt(int position, int v, ByteOrder byteOrder) throws IOException {
        if (byteOrder != ByteOrder.nativeOrder()) {
            this.writeInt(position, Integer.reverseBytes(v));
        } else {
            this.writeInt(position, v);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.ensureAvailable(8);
        GlobalMemoryAccessorRegistry.MEM.putLong(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + this.pos, v);
        this.pos += 8;
    }

    @Override
    public void writeLong(int position, long v) throws IOException {
        this.checkAvailable(position, 8);
        GlobalMemoryAccessorRegistry.MEM.putLong(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position, v);
    }

    @Override
    public void writeLong(long v, ByteOrder byteOrder) throws IOException {
        if (byteOrder != ByteOrder.nativeOrder()) {
            this.writeLong(Long.reverseBytes(v));
        } else {
            this.writeLong(v);
        }
    }

    @Override
    public void writeLong(int position, long v, ByteOrder byteOrder) throws IOException {
        if (byteOrder != ByteOrder.nativeOrder()) {
            this.writeLong(position, Long.reverseBytes(v));
        } else {
            this.writeLong(position, v);
        }
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.ensureAvailable(2);
        GlobalMemoryAccessorRegistry.MEM.putShort(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + this.pos, (short)v);
        this.pos += 2;
    }

    @Override
    public void writeShort(int position, int v) throws IOException {
        this.checkAvailable(position, 2);
        GlobalMemoryAccessorRegistry.MEM.putShort(this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + position, (short)v);
    }

    @Override
    public void writeShort(int v, ByteOrder byteOrder) throws IOException {
        short s = (short)v;
        if (byteOrder != ByteOrder.nativeOrder()) {
            this.writeShort(Short.reverseBytes(s));
        } else {
            this.writeShort(v);
        }
    }

    @Override
    public void writeShort(int position, int v, ByteOrder byteOrder) throws IOException {
        short s = (short)v;
        if (byteOrder != ByteOrder.nativeOrder()) {
            this.writeShort(position, Short.reverseBytes(s));
        } else {
            this.writeShort(position, v);
        }
    }

    @Override
    public void writeBooleanArray(boolean[] booleans) throws IOException {
        int len = booleans != null ? booleans.length : -1;
        this.writeInt(len);
        if (len > 0) {
            this.memCopy(booleans, HeapMemoryAccessor.ARRAY_BOOLEAN_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_BOOLEAN_INDEX_SCALE);
        }
    }

    @Override
    public void writeByteArray(byte[] bytes) throws IOException {
        int len = bytes != null ? bytes.length : -1;
        this.writeInt(len);
        if (len > 0) {
            this.memCopy(bytes, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_BYTE_INDEX_SCALE);
        }
    }

    @Override
    public void writeCharArray(char[] values) throws IOException {
        int len = values != null ? values.length : -1;
        this.writeInt(len);
        if (len > 0) {
            this.memCopy(values, HeapMemoryAccessor.ARRAY_CHAR_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_CHAR_INDEX_SCALE);
        }
    }

    @Override
    public void writeShortArray(short[] values) throws IOException {
        int len = values != null ? values.length : -1;
        this.writeInt(len);
        if (len > 0) {
            this.memCopy(values, HeapMemoryAccessor.ARRAY_SHORT_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_SHORT_INDEX_SCALE);
        }
    }

    @Override
    public void writeIntArray(int[] values) throws IOException {
        int len = values != null ? values.length : -1;
        this.writeInt(len);
        if (len > 0) {
            this.memCopy(values, HeapMemoryAccessor.ARRAY_INT_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_INT_INDEX_SCALE);
        }
    }

    @Override
    public void writeFloatArray(float[] values) throws IOException {
        int len = values != null ? values.length : -1;
        this.writeInt(len);
        if (len > 0) {
            this.memCopy(values, HeapMemoryAccessor.ARRAY_FLOAT_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_FLOAT_INDEX_SCALE);
        }
    }

    @Override
    public void writeLongArray(long[] values) throws IOException {
        int len = values != null ? values.length : -1;
        this.writeInt(len);
        if (len > 0) {
            this.memCopy(values, HeapMemoryAccessor.ARRAY_LONG_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_LONG_INDEX_SCALE);
        }
    }

    @Override
    public void writeDoubleArray(double[] values) throws IOException {
        int len = values != null ? values.length : -1;
        this.writeInt(len);
        if (len > 0) {
            this.memCopy(values, HeapMemoryAccessor.ARRAY_DOUBLE_BASE_OFFSET, len, HeapMemoryAccessor.ARRAY_DOUBLE_INDEX_SCALE);
        }
    }

    private void memCopy(Object src, long srcOffset, int length, int indexScale) {
        if (length < 0) {
            throw new NegativeArraySizeException("Source length is negative: " + length);
        }
        int remaining = indexScale * length;
        long offset = srcOffset;
        this.ensureAvailable(remaining);
        while (remaining > 0) {
            int chunk = Math.min(remaining, 0x100000);
            GlobalMemoryAccessorRegistry.MEM.copyMemory(src, offset, this.buffer, HeapMemoryAccessor.ARRAY_BYTE_BASE_OFFSET + this.pos, chunk);
            remaining -= chunk;
            offset += (long)chunk;
            this.pos += chunk;
        }
    }

    @Override
    public ByteOrder getByteOrder() {
        return ByteOrder.nativeOrder();
    }

    private void checkAvailable(int pos, int k) throws IOException {
        int size;
        if (pos < 0) {
            throw new IllegalArgumentException("Negative pos! -> " + pos);
        }
        int n = size = this.buffer != null ? this.buffer.length : 0;
        if (size - pos < k) {
            throw new IOException("Cannot write " + k + " bytes!");
        }
    }

    @Override
    public String toString() {
        return "UnsafeObjectDataOutput{size=" + (this.buffer != null ? this.buffer.length : 0) + ", pos=" + this.pos + ", byteOrder=" + this.getByteOrder() + '}';
    }
}

