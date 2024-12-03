/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory.impl;

import com.hazelcast.internal.memory.impl.UnsafeBasedMemoryAccessor;
import com.hazelcast.internal.memory.impl.UnsafeUtil;
import java.lang.reflect.Field;

public final class StandardMemoryAccessor
extends UnsafeBasedMemoryAccessor {
    public static final StandardMemoryAccessor INSTANCE = UnsafeUtil.UNSAFE_AVAILABLE ? new StandardMemoryAccessor() : null;

    StandardMemoryAccessor() {
        if (!UnsafeUtil.UNSAFE_AVAILABLE) {
            throw new IllegalStateException(this.getClass().getName() + " can only be used only when Unsafe is available!");
        }
    }

    @Override
    public boolean getBoolean(long address) {
        return UnsafeUtil.UNSAFE.getBoolean(null, address);
    }

    @Override
    public void putBoolean(long address, boolean x) {
        UnsafeUtil.UNSAFE.putBoolean(null, address, x);
    }

    @Override
    public byte getByte(long address) {
        return UnsafeUtil.UNSAFE.getByte(address);
    }

    @Override
    public void putByte(long address, byte x) {
        UnsafeUtil.UNSAFE.putByte(address, x);
    }

    @Override
    public char getChar(long address) {
        return UnsafeUtil.UNSAFE.getChar(address);
    }

    @Override
    public void putChar(long address, char x) {
        UnsafeUtil.UNSAFE.putChar(address, x);
    }

    @Override
    public short getShort(long address) {
        return UnsafeUtil.UNSAFE.getShort(address);
    }

    @Override
    public void putShort(long address, short x) {
        UnsafeUtil.UNSAFE.putShort(address, x);
    }

    @Override
    public int getInt(long address) {
        return UnsafeUtil.UNSAFE.getInt(address);
    }

    @Override
    public void putInt(long address, int x) {
        UnsafeUtil.UNSAFE.putInt(address, x);
    }

    @Override
    public float getFloat(long address) {
        return UnsafeUtil.UNSAFE.getFloat(address);
    }

    @Override
    public void putFloat(long address, float x) {
        UnsafeUtil.UNSAFE.putFloat(address, x);
    }

    @Override
    public long getLong(long address) {
        return UnsafeUtil.UNSAFE.getLong(address);
    }

    @Override
    public void putLong(long address, long x) {
        UnsafeUtil.UNSAFE.putLong(address, x);
    }

    @Override
    public double getDouble(long address) {
        return UnsafeUtil.UNSAFE.getDouble(address);
    }

    @Override
    public void putDouble(long address, double x) {
        UnsafeUtil.UNSAFE.putDouble(address, x);
    }

    @Override
    public void copyMemory(long srcAddress, long destAddress, long lengthBytes) {
        UnsafeUtil.UNSAFE.copyMemory(srcAddress, destAddress, lengthBytes);
    }

    @Override
    public void copyFromByteArray(byte[] source, int offset, long destAddress, int length) {
        this.copyMemory(source, ARRAY_BYTE_BASE_OFFSET + ARRAY_BYTE_INDEX_SCALE * offset, null, destAddress, length);
    }

    @Override
    public void copyToByteArray(long srcAddress, byte[] destination, int offset, int length) {
        this.copyMemory(null, srcAddress, destination, ARRAY_BYTE_BASE_OFFSET + ARRAY_BYTE_INDEX_SCALE * offset, length);
    }

    @Override
    public void setMemory(long address, long lengthBytes, byte value) {
        UnsafeUtil.UNSAFE.setMemory(address, lengthBytes, value);
    }

    @Override
    public boolean getBooleanVolatile(long address) {
        return UnsafeUtil.UNSAFE.getBooleanVolatile(null, address);
    }

    @Override
    public void putBooleanVolatile(long address, boolean x) {
        UnsafeUtil.UNSAFE.putBooleanVolatile(null, address, x);
    }

    @Override
    public byte getByteVolatile(long address) {
        return UnsafeUtil.UNSAFE.getByteVolatile(null, address);
    }

    @Override
    public void putByteVolatile(long address, byte x) {
        UnsafeUtil.UNSAFE.putByteVolatile(null, address, x);
    }

    @Override
    public char getCharVolatile(long address) {
        return UnsafeUtil.UNSAFE.getCharVolatile(null, address);
    }

    @Override
    public void putCharVolatile(long address, char x) {
        UnsafeUtil.UNSAFE.putCharVolatile(null, address, x);
    }

    @Override
    public short getShortVolatile(long address) {
        return UnsafeUtil.UNSAFE.getShortVolatile(null, address);
    }

    @Override
    public void putShortVolatile(long address, short x) {
        UnsafeUtil.UNSAFE.putShortVolatile(null, address, x);
    }

    @Override
    public int getIntVolatile(long address) {
        return UnsafeUtil.UNSAFE.getIntVolatile(null, address);
    }

    @Override
    public void putIntVolatile(long address, int x) {
        UnsafeUtil.UNSAFE.putIntVolatile(null, address, x);
    }

    @Override
    public float getFloatVolatile(long address) {
        return UnsafeUtil.UNSAFE.getFloatVolatile(null, address);
    }

    @Override
    public void putFloatVolatile(long address, float x) {
        UnsafeUtil.UNSAFE.putFloatVolatile(null, address, x);
    }

    @Override
    public long getLongVolatile(long address) {
        return UnsafeUtil.UNSAFE.getLongVolatile(null, address);
    }

    @Override
    public void putLongVolatile(long address, long x) {
        UnsafeUtil.UNSAFE.putLongVolatile(null, address, x);
    }

    @Override
    public double getDoubleVolatile(long address) {
        return UnsafeUtil.UNSAFE.getDoubleVolatile(null, address);
    }

    @Override
    public void putDoubleVolatile(long address, double x) {
        UnsafeUtil.UNSAFE.putDoubleVolatile(null, address, x);
    }

    @Override
    public boolean compareAndSwapInt(long address, int expected, int x) {
        return UnsafeUtil.UNSAFE.compareAndSwapInt(null, address, expected, x);
    }

    @Override
    public boolean compareAndSwapLong(long address, long expected, long x) {
        return UnsafeUtil.UNSAFE.compareAndSwapLong(null, address, expected, x);
    }

    @Override
    public boolean compareAndSwapObject(long address, Object expected, Object x) {
        return UnsafeUtil.UNSAFE.compareAndSwapObject(null, address, expected, x);
    }

    @Override
    public void putOrderedInt(long address, int x) {
        UnsafeUtil.UNSAFE.putOrderedInt(null, address, x);
    }

    @Override
    public void putOrderedLong(long address, long x) {
        UnsafeUtil.UNSAFE.putOrderedLong(null, address, x);
    }

    @Override
    public void putOrderedObject(long address, Object x) {
        UnsafeUtil.UNSAFE.putOrderedObject(null, address, x);
    }

    @Override
    public long objectFieldOffset(Field field) {
        return UnsafeUtil.UNSAFE.objectFieldOffset(field);
    }

    @Override
    public int arrayBaseOffset(Class<?> arrayClass) {
        return UnsafeUtil.UNSAFE.arrayBaseOffset(arrayClass);
    }

    @Override
    public int arrayIndexScale(Class<?> arrayClass) {
        return UnsafeUtil.UNSAFE.arrayIndexScale(arrayClass);
    }

    @Override
    public Object getObject(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getObject(base, offset);
    }

    @Override
    public void putObject(Object base, long offset, Object x) {
        UnsafeUtil.UNSAFE.putObject(base, offset, x);
    }

    @Override
    public boolean getBoolean(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getBoolean(base, offset);
    }

    @Override
    public void putBoolean(Object base, long offset, boolean x) {
        UnsafeUtil.UNSAFE.putBoolean(base, offset, x);
    }

    @Override
    public byte getByte(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getByte(base, offset);
    }

    @Override
    public void putByte(Object base, long offset, byte x) {
        UnsafeUtil.UNSAFE.putByte(base, offset, x);
    }

    @Override
    public char getChar(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getChar(base, offset);
    }

    @Override
    public void putChar(Object base, long offset, char x) {
        UnsafeUtil.UNSAFE.putChar(base, offset, x);
    }

    @Override
    public short getShort(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getShort(base, offset);
    }

    @Override
    public void putShort(Object base, long offset, short x) {
        UnsafeUtil.UNSAFE.putShort(base, offset, x);
    }

    @Override
    public int getInt(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getInt(base, offset);
    }

    @Override
    public void putInt(Object base, long offset, int x) {
        UnsafeUtil.UNSAFE.putInt(base, offset, x);
    }

    @Override
    public float getFloat(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getFloat(base, offset);
    }

    @Override
    public void putFloat(Object base, long offset, float x) {
        UnsafeUtil.UNSAFE.putFloat(base, offset, x);
    }

    @Override
    public long getLong(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getLong(base, offset);
    }

    @Override
    public void putLong(Object base, long offset, long x) {
        UnsafeUtil.UNSAFE.putLong(base, offset, x);
    }

    @Override
    public double getDouble(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getDouble(base, offset);
    }

    @Override
    public void putDouble(Object base, long offset, double x) {
        UnsafeUtil.UNSAFE.putDouble(base, offset, x);
    }

    @Override
    public void copyMemory(Object srcObj, long srcOffset, Object destObj, long destOffset, long lengthBytes) {
        UnsafeUtil.UNSAFE.copyMemory(srcObj, srcOffset, destObj, destOffset, lengthBytes);
    }

    @Override
    public Object getObjectVolatile(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getObjectVolatile(base, offset);
    }

    @Override
    public void putObjectVolatile(Object base, long offset, Object x) {
        UnsafeUtil.UNSAFE.putObjectVolatile(base, offset, x);
    }

    @Override
    public boolean getBooleanVolatile(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getBooleanVolatile(base, offset);
    }

    @Override
    public void putBooleanVolatile(Object base, long offset, boolean x) {
        UnsafeUtil.UNSAFE.putBooleanVolatile(base, offset, x);
    }

    @Override
    public byte getByteVolatile(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getByteVolatile(base, offset);
    }

    @Override
    public void putByteVolatile(Object base, long offset, byte x) {
        UnsafeUtil.UNSAFE.putByteVolatile(base, offset, x);
    }

    @Override
    public char getCharVolatile(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getCharVolatile(base, offset);
    }

    @Override
    public void putCharVolatile(Object base, long offset, char x) {
        UnsafeUtil.UNSAFE.putCharVolatile(base, offset, x);
    }

    @Override
    public short getShortVolatile(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getShortVolatile(base, offset);
    }

    @Override
    public void putShortVolatile(Object base, long offset, short x) {
        UnsafeUtil.UNSAFE.putShortVolatile(base, offset, x);
    }

    @Override
    public int getIntVolatile(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getIntVolatile(base, offset);
    }

    @Override
    public void putIntVolatile(Object base, long offset, int x) {
        UnsafeUtil.UNSAFE.putIntVolatile(base, offset, x);
    }

    @Override
    public float getFloatVolatile(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getFloatVolatile(base, offset);
    }

    @Override
    public void putFloatVolatile(Object base, long offset, float x) {
        UnsafeUtil.UNSAFE.putFloatVolatile(base, offset, x);
    }

    @Override
    public long getLongVolatile(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getLongVolatile(base, offset);
    }

    @Override
    public void putLongVolatile(Object base, long offset, long x) {
        UnsafeUtil.UNSAFE.putLongVolatile(base, offset, x);
    }

    @Override
    public double getDoubleVolatile(Object base, long offset) {
        return UnsafeUtil.UNSAFE.getDoubleVolatile(base, offset);
    }

    @Override
    public void putDoubleVolatile(Object base, long offset, double x) {
        UnsafeUtil.UNSAFE.putDoubleVolatile(base, offset, x);
    }

    @Override
    public void putOrderedInt(Object base, long offset, int x) {
        UnsafeUtil.UNSAFE.putOrderedInt(base, offset, x);
    }

    @Override
    public void putOrderedLong(Object base, long offset, long x) {
        UnsafeUtil.UNSAFE.putOrderedLong(base, offset, x);
    }

    @Override
    public void putOrderedObject(Object base, long offset, Object x) {
        UnsafeUtil.UNSAFE.putOrderedObject(base, offset, x);
    }

    @Override
    public boolean compareAndSwapInt(Object base, long offset, int expected, int x) {
        return UnsafeUtil.UNSAFE.compareAndSwapInt(base, offset, expected, x);
    }

    @Override
    public boolean compareAndSwapLong(Object base, long offset, long expected, long x) {
        return UnsafeUtil.UNSAFE.compareAndSwapLong(base, offset, expected, x);
    }

    @Override
    public boolean compareAndSwapObject(Object base, long offset, Object expected, Object x) {
        return UnsafeUtil.UNSAFE.compareAndSwapObject(base, offset, expected, x);
    }
}

