/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.util;

import com.hazelcast.client.impl.protocol.util.ClientProtocolBuffer;
import com.hazelcast.nio.Bits;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SafeBuffer
implements ClientProtocolBuffer {
    private ByteBuffer byteBuffer;

    public SafeBuffer(byte[] buffer) {
        this.wrap(buffer);
    }

    @Override
    public void putLong(int index, long value) {
        this.byteBuffer.putLong(index, value);
    }

    @Override
    public void putInt(int index, int value) {
        this.byteBuffer.putInt(index, value);
    }

    @Override
    public void putShort(int index, short value) {
        this.byteBuffer.putShort(index, value);
    }

    @Override
    public void putByte(int index, byte value) {
        this.byteBuffer.put(index, value);
    }

    @Override
    public void putBytes(int index, byte[] src) {
        this.putBytes(index, src, 0, src.length);
    }

    @Override
    public void putBytes(int index, byte[] src, int offset, int length) {
        this.byteBuffer.position(index);
        this.byteBuffer.put(src, offset, length);
    }

    @Override
    public void putBytes(int index, ByteBuffer src, int length) {
        this.byteBuffer.position(index);
        if (src.isDirect()) {
            int oldLimit = src.limit();
            src.limit(src.position() + length);
            this.byteBuffer.put(src);
            src.limit(oldLimit);
        } else {
            this.byteBuffer.put(src.array(), src.position(), length);
            src.position(src.position() + length);
        }
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

    @Override
    public void wrap(byte[] buffer) {
        this.byteBuffer = ByteBuffer.wrap(buffer);
        this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public byte[] byteArray() {
        return this.byteBuffer.array();
    }

    @Override
    public int capacity() {
        return this.byteBuffer.capacity();
    }

    @Override
    public long getLong(int index) {
        return this.byteBuffer.getLong(index);
    }

    @Override
    public int getInt(int index) {
        return this.byteBuffer.getInt(index);
    }

    @Override
    public short getShort(int index) {
        return this.byteBuffer.getShort(index);
    }

    @Override
    public byte getByte(int index) {
        return this.byteBuffer.get(index);
    }

    @Override
    public void getBytes(int index, byte[] dst) {
        this.getBytes(index, dst, 0, dst.length);
    }

    @Override
    public void getBytes(int index, byte[] dst, int offset, int length) {
        this.byteBuffer.position(index);
        this.byteBuffer.get(dst, offset, length);
    }

    @Override
    public String getStringUtf8(int offset, int length) {
        byte[] stringInBytes = new byte[length];
        this.getBytes(offset + 4, stringInBytes);
        return new String(stringInBytes, Bits.UTF_8);
    }
}

