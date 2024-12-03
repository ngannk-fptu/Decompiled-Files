/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.util;

import com.hazelcast.client.impl.protocol.util.ClientProtocolBuffer;
import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import com.hazelcast.nio.Bits;
import com.hazelcast.util.collection.ArrayUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressFBWarnings(value={"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class UnsafeBuffer
implements ClientProtocolBuffer {
    private static final String DISABLE_BOUNDS_CHECKS_PROP_NAME = "hazelcast.disable.bounds.checks";
    private static final boolean SHOULD_BOUNDS_CHECK = !Boolean.getBoolean("hazelcast.disable.bounds.checks");
    private static final ByteOrder NATIVE_BYTE_ORDER = ByteOrder.nativeOrder();
    private static final ByteOrder PROTOCOL_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static final long ARRAY_BASE_OFFSET = GlobalMemoryAccessorRegistry.MEM.arrayBaseOffset(byte[].class);
    private byte[] byteArray;
    private long addressOffset;
    private int capacity;

    public UnsafeBuffer(byte[] buffer) {
        this.wrap(buffer);
    }

    @Override
    public void wrap(byte[] buffer) {
        this.addressOffset = ARRAY_BASE_OFFSET;
        this.capacity = buffer.length;
        this.byteArray = buffer;
    }

    @Override
    public byte[] byteArray() {
        return this.byteArray;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public long getLong(int index) {
        this.boundsCheck(index, 8);
        long bits = GlobalMemoryAccessorRegistry.MEM.getLong(this.byteArray, this.addressOffset + (long)index);
        if (NATIVE_BYTE_ORDER != PROTOCOL_BYTE_ORDER) {
            bits = Long.reverseBytes(bits);
        }
        return bits;
    }

    @Override
    public void putLong(int index, long value) {
        this.boundsCheck(index, 8);
        long bits = value;
        if (NATIVE_BYTE_ORDER != PROTOCOL_BYTE_ORDER) {
            bits = Long.reverseBytes(bits);
        }
        GlobalMemoryAccessorRegistry.MEM.putLong(this.byteArray, this.addressOffset + (long)index, bits);
    }

    @Override
    public int getInt(int index) {
        this.boundsCheck(index, 4);
        int bits = GlobalMemoryAccessorRegistry.MEM.getInt(this.byteArray, this.addressOffset + (long)index);
        if (NATIVE_BYTE_ORDER != PROTOCOL_BYTE_ORDER) {
            bits = Integer.reverseBytes(bits);
        }
        return bits;
    }

    @Override
    public void putInt(int index, int value) {
        this.boundsCheck(index, 4);
        int bits = value;
        if (NATIVE_BYTE_ORDER != PROTOCOL_BYTE_ORDER) {
            bits = Integer.reverseBytes(bits);
        }
        GlobalMemoryAccessorRegistry.MEM.putInt(this.byteArray, this.addressOffset + (long)index, bits);
    }

    @Override
    public short getShort(int index) {
        this.boundsCheck(index, 2);
        short bits = GlobalMemoryAccessorRegistry.MEM.getShort(this.byteArray, this.addressOffset + (long)index);
        if (NATIVE_BYTE_ORDER != PROTOCOL_BYTE_ORDER) {
            bits = Short.reverseBytes(bits);
        }
        return bits;
    }

    @Override
    public void putShort(int index, short value) {
        this.boundsCheck(index, 2);
        short bits = value;
        if (NATIVE_BYTE_ORDER != PROTOCOL_BYTE_ORDER) {
            bits = Short.reverseBytes(bits);
        }
        GlobalMemoryAccessorRegistry.MEM.putShort(this.byteArray, this.addressOffset + (long)index, bits);
    }

    @Override
    public byte getByte(int index) {
        this.boundsCheck(index, 1);
        return GlobalMemoryAccessorRegistry.MEM.getByte(this.byteArray, this.addressOffset + (long)index);
    }

    @Override
    public void putByte(int index, byte value) {
        this.boundsCheck(index, 1);
        GlobalMemoryAccessorRegistry.MEM.putByte(this.byteArray, this.addressOffset + (long)index, value);
    }

    @Override
    public void getBytes(int index, byte[] dst) {
        this.getBytes(index, dst, 0, dst.length);
    }

    @Override
    public void getBytes(int index, byte[] dst, int offset, int length) {
        this.boundsCheck(index, length);
        UnsafeBuffer.boundsCheck(dst, offset, length);
        GlobalMemoryAccessorRegistry.MEM.copyMemory(this.byteArray, this.addressOffset + (long)index, dst, ARRAY_BASE_OFFSET + (long)offset, length);
    }

    @Override
    public void putBytes(int index, byte[] src) {
        this.putBytes(index, src, 0, src.length);
    }

    @Override
    public void putBytes(int index, byte[] src, int offset, int length) {
        this.boundsCheck(index, length);
        UnsafeBuffer.boundsCheck(src, offset, length);
        GlobalMemoryAccessorRegistry.MEM.copyMemory(src, ARRAY_BASE_OFFSET + (long)offset, this.byteArray, this.addressOffset + (long)index, length);
    }

    @Override
    public void putBytes(int index, ByteBuffer src, int length) {
        if (src.isDirect()) {
            src.get(this.byteArray, index, length);
        } else {
            this.putBytes(index, src.array(), src.position(), length);
            src.position(src.position() + length);
        }
    }

    @Override
    public String getStringUtf8(int offset, int length) {
        byte[] stringInBytes = new byte[length];
        this.getBytes(offset + 4, stringInBytes);
        return new String(stringInBytes, Bits.UTF_8);
    }

    @Override
    public int putStringUtf8(int index, String value) {
        return this.putStringUtf8(index, value, Integer.MAX_VALUE);
    }

    @Override
    public int putStringUtf8(int index, String value, int maxEncodedSize) {
        byte[] bytes = value.getBytes(Bits.UTF_8);
        if (bytes.length > maxEncodedSize) {
            throw new IllegalArgumentException("Encoded string larger than maximum size: " + maxEncodedSize);
        }
        this.putInt(index, bytes.length);
        this.putBytes(index + 4, bytes);
        return 4 + bytes.length;
    }

    private void boundsCheck(int index, int length) {
        if (SHOULD_BOUNDS_CHECK) {
            ArrayUtils.boundsCheck(this.capacity, index, length);
        }
    }

    private static void boundsCheck(byte[] buffer, int index, int length) {
        if (SHOULD_BOUNDS_CHECK) {
            ArrayUtils.boundsCheck(buffer.length, index, length);
        }
    }
}

