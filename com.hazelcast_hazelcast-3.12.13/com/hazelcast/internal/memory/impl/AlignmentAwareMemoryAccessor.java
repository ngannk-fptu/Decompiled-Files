/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory.impl;

import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import com.hazelcast.internal.memory.impl.AlignmentUtil;
import com.hazelcast.internal.memory.impl.EndiannessUtil;
import com.hazelcast.internal.memory.impl.UnsafeBasedMemoryAccessor;
import com.hazelcast.internal.memory.impl.UnsafeUtil;
import java.lang.reflect.Field;

public final class AlignmentAwareMemoryAccessor
extends UnsafeBasedMemoryAccessor {
    public static final AlignmentAwareMemoryAccessor INSTANCE = UnsafeUtil.UNSAFE_AVAILABLE ? new AlignmentAwareMemoryAccessor() : null;

    private AlignmentAwareMemoryAccessor() {
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
        return AlignmentUtil.is2BytesAligned(address) ? UnsafeUtil.UNSAFE.getChar(address) : EndiannessUtil.readChar(EndiannessUtil.NATIVE_ACCESS, null, address, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putChar(long address, char x) {
        if (AlignmentUtil.is2BytesAligned(address)) {
            UnsafeUtil.UNSAFE.putChar(address, x);
        } else {
            EndiannessUtil.writeChar(EndiannessUtil.NATIVE_ACCESS, null, address, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public short getShort(long address) {
        return AlignmentUtil.is2BytesAligned(address) ? UnsafeUtil.UNSAFE.getShort(address) : EndiannessUtil.readShort(EndiannessUtil.NATIVE_ACCESS, null, address, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putShort(long address, short x) {
        if (AlignmentUtil.is2BytesAligned(address)) {
            UnsafeUtil.UNSAFE.putShort(address, x);
        } else {
            EndiannessUtil.writeShort(EndiannessUtil.NATIVE_ACCESS, null, address, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public int getInt(long address) {
        return AlignmentUtil.is4BytesAligned(address) ? UnsafeUtil.UNSAFE.getInt(address) : EndiannessUtil.readInt(EndiannessUtil.NATIVE_ACCESS, null, address, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putInt(long address, int x) {
        if (AlignmentUtil.is4BytesAligned(address)) {
            UnsafeUtil.UNSAFE.putInt(address, x);
        } else {
            EndiannessUtil.writeInt(EndiannessUtil.NATIVE_ACCESS, null, address, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public float getFloat(long address) {
        return AlignmentUtil.is4BytesAligned(address) ? UnsafeUtil.UNSAFE.getFloat(address) : EndiannessUtil.readFloat(EndiannessUtil.NATIVE_ACCESS, null, address, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putFloat(long address, float x) {
        if (AlignmentUtil.is4BytesAligned(address)) {
            UnsafeUtil.UNSAFE.putFloat(address, x);
        } else {
            EndiannessUtil.writeFloat(EndiannessUtil.NATIVE_ACCESS, null, address, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public long getLong(long address) {
        return AlignmentUtil.is8BytesAligned(address) ? UnsafeUtil.UNSAFE.getLong(address) : EndiannessUtil.readLong(EndiannessUtil.NATIVE_ACCESS, null, address, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putLong(long address, long x) {
        if (AlignmentUtil.is8BytesAligned(address)) {
            UnsafeUtil.UNSAFE.putLong(address, x);
        } else {
            EndiannessUtil.writeLong(EndiannessUtil.NATIVE_ACCESS, null, address, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public double getDouble(long address) {
        return AlignmentUtil.is8BytesAligned(address) ? UnsafeUtil.UNSAFE.getDouble(address) : EndiannessUtil.readDouble(EndiannessUtil.NATIVE_ACCESS, null, address, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putDouble(long address, double x) {
        if (AlignmentUtil.is8BytesAligned(address)) {
            UnsafeUtil.UNSAFE.putDouble(address, x);
        } else {
            EndiannessUtil.writeDouble(EndiannessUtil.NATIVE_ACCESS, null, address, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public void copyMemory(long srcAddress, long destAddress, long lengthBytes) {
        UnsafeUtil.UNSAFE.copyMemory(srcAddress, destAddress, lengthBytes);
    }

    @Override
    public void copyFromByteArray(byte[] source, int offset, long destAddress, int length) {
        GlobalMemoryAccessorRegistry.AMEM.copyFromByteArray(source, offset, destAddress, length);
    }

    @Override
    public void copyToByteArray(long srcAddress, byte[] destination, int offset, int length) {
        GlobalMemoryAccessorRegistry.AMEM.copyToByteArray(srcAddress, destination, offset, length);
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
        AlignmentUtil.check2BytesAligned(address);
        return UnsafeUtil.UNSAFE.getCharVolatile(null, address);
    }

    @Override
    public void putCharVolatile(long address, char x) {
        AlignmentUtil.check2BytesAligned(address);
        UnsafeUtil.UNSAFE.putCharVolatile(null, address, x);
    }

    @Override
    public short getShortVolatile(long address) {
        AlignmentUtil.check2BytesAligned(address);
        return UnsafeUtil.UNSAFE.getShortVolatile(null, address);
    }

    @Override
    public void putShortVolatile(long address, short x) {
        AlignmentUtil.check2BytesAligned(address);
        UnsafeUtil.UNSAFE.putShortVolatile(null, address, x);
    }

    @Override
    public int getIntVolatile(long address) {
        AlignmentUtil.check4BytesAligned(address);
        return UnsafeUtil.UNSAFE.getIntVolatile(null, address);
    }

    @Override
    public void putIntVolatile(long address, int x) {
        AlignmentUtil.check4BytesAligned(address);
        UnsafeUtil.UNSAFE.putIntVolatile(null, address, x);
    }

    @Override
    public float getFloatVolatile(long address) {
        AlignmentUtil.check4BytesAligned(address);
        return UnsafeUtil.UNSAFE.getFloatVolatile(null, address);
    }

    @Override
    public void putFloatVolatile(long address, float x) {
        AlignmentUtil.check4BytesAligned(address);
        UnsafeUtil.UNSAFE.putFloatVolatile(null, address, x);
    }

    @Override
    public long getLongVolatile(long address) {
        AlignmentUtil.check8BytesAligned(address);
        return UnsafeUtil.UNSAFE.getLongVolatile(null, address);
    }

    @Override
    public void putLongVolatile(long address, long x) {
        AlignmentUtil.check8BytesAligned(address);
        UnsafeUtil.UNSAFE.putLongVolatile(null, address, x);
    }

    @Override
    public double getDoubleVolatile(long address) {
        AlignmentUtil.check8BytesAligned(address);
        return UnsafeUtil.UNSAFE.getDoubleVolatile(null, address);
    }

    @Override
    public void putDoubleVolatile(long address, double x) {
        AlignmentUtil.check8BytesAligned(address);
        UnsafeUtil.UNSAFE.putDoubleVolatile(null, address, x);
    }

    @Override
    public void putOrderedInt(long address, int x) {
        AlignmentUtil.check4BytesAligned(address);
        UnsafeUtil.UNSAFE.putOrderedInt(null, address, x);
    }

    @Override
    public void putOrderedLong(long address, long x) {
        AlignmentUtil.check8BytesAligned(address);
        UnsafeUtil.UNSAFE.putOrderedLong(null, address, x);
    }

    @Override
    public void putOrderedObject(long address, Object x) {
        AlignmentUtil.checkReferenceAligned(address);
        UnsafeUtil.UNSAFE.putOrderedObject(null, address, x);
    }

    @Override
    public boolean compareAndSwapInt(long address, int expected, int x) {
        AlignmentUtil.check4BytesAligned(address);
        return UnsafeUtil.UNSAFE.compareAndSwapInt(null, address, expected, x);
    }

    @Override
    public boolean compareAndSwapLong(long address, long expected, long x) {
        AlignmentUtil.check8BytesAligned(address);
        return UnsafeUtil.UNSAFE.compareAndSwapLong(null, address, expected, x);
    }

    @Override
    public boolean compareAndSwapObject(long address, Object expected, Object x) {
        AlignmentUtil.checkReferenceAligned(address);
        return UnsafeUtil.UNSAFE.compareAndSwapObject(null, address, expected, x);
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
    public void copyMemory(Object srcObj, long srcOffset, Object destObj, long destOffset, long lengthBytes) {
        UnsafeUtil.UNSAFE.copyMemory(srcObj, srcOffset, destObj, destOffset, lengthBytes);
    }

    @Override
    public Object getObject(Object base, long offset) {
        AlignmentUtil.checkReferenceAligned(offset);
        return UnsafeUtil.UNSAFE.getObject(base, offset);
    }

    @Override
    public void putObject(Object base, long offset, Object x) {
        AlignmentUtil.checkReferenceAligned(offset);
        UnsafeUtil.UNSAFE.putObject(base, offset, x);
    }

    @Override
    public char getChar(Object base, long offset) {
        return AlignmentUtil.is2BytesAligned(offset) ? UnsafeUtil.UNSAFE.getChar(base, offset) : EndiannessUtil.readChar(EndiannessUtil.NATIVE_ACCESS, base, offset, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putChar(Object base, long offset, char x) {
        if (AlignmentUtil.is2BytesAligned(offset)) {
            UnsafeUtil.UNSAFE.putChar(base, offset, x);
        } else {
            EndiannessUtil.writeChar(EndiannessUtil.NATIVE_ACCESS, base, offset, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public short getShort(Object base, long offset) {
        return AlignmentUtil.is2BytesAligned(offset) ? UnsafeUtil.UNSAFE.getShort(base, offset) : EndiannessUtil.readShort(EndiannessUtil.NATIVE_ACCESS, base, offset, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putShort(Object base, long offset, short x) {
        if (AlignmentUtil.is2BytesAligned(offset)) {
            UnsafeUtil.UNSAFE.putShort(base, offset, x);
        } else {
            EndiannessUtil.writeShort(EndiannessUtil.NATIVE_ACCESS, base, offset, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public int getInt(Object base, long offset) {
        return AlignmentUtil.is4BytesAligned(offset) ? UnsafeUtil.UNSAFE.getInt(base, offset) : EndiannessUtil.readInt(EndiannessUtil.NATIVE_ACCESS, base, offset, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putInt(Object base, long offset, int x) {
        if (AlignmentUtil.is4BytesAligned(offset)) {
            UnsafeUtil.UNSAFE.putInt(base, offset, x);
        } else {
            EndiannessUtil.writeInt(EndiannessUtil.NATIVE_ACCESS, base, offset, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public float getFloat(Object base, long offset) {
        return AlignmentUtil.is4BytesAligned(offset) ? UnsafeUtil.UNSAFE.getFloat(base, offset) : EndiannessUtil.readFloat(EndiannessUtil.NATIVE_ACCESS, base, offset, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putFloat(Object base, long offset, float x) {
        if (AlignmentUtil.is4BytesAligned(offset)) {
            UnsafeUtil.UNSAFE.putFloat(base, offset, x);
        } else {
            EndiannessUtil.writeFloat(EndiannessUtil.NATIVE_ACCESS, base, offset, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public long getLong(Object base, long offset) {
        return AlignmentUtil.is8BytesAligned(offset) ? UnsafeUtil.UNSAFE.getLong(base, offset) : EndiannessUtil.readLong(EndiannessUtil.NATIVE_ACCESS, base, offset, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putLong(Object base, long offset, long x) {
        if (AlignmentUtil.is8BytesAligned(offset)) {
            UnsafeUtil.UNSAFE.putLong(base, offset, x);
        } else {
            EndiannessUtil.writeLong(EndiannessUtil.NATIVE_ACCESS, base, offset, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
    }

    @Override
    public double getDouble(Object base, long offset) {
        return AlignmentUtil.is8BytesAligned(offset) ? UnsafeUtil.UNSAFE.getDouble(base, offset) : EndiannessUtil.readDouble(EndiannessUtil.NATIVE_ACCESS, base, offset, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
    }

    @Override
    public void putDouble(Object base, long offset, double x) {
        if (AlignmentUtil.is8BytesAligned(offset)) {
            UnsafeUtil.UNSAFE.putDouble(base, offset, x);
        } else {
            EndiannessUtil.writeDouble(EndiannessUtil.NATIVE_ACCESS, base, offset, x, AlignmentUtil.IS_PLATFORM_BIG_ENDIAN);
        }
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
    public Object getObjectVolatile(Object base, long offset) {
        AlignmentUtil.checkReferenceAligned(offset);
        return UnsafeUtil.UNSAFE.getObjectVolatile(base, offset);
    }

    @Override
    public void putObjectVolatile(Object base, long offset, Object x) {
        AlignmentUtil.checkReferenceAligned(offset);
        UnsafeUtil.UNSAFE.putObjectVolatile(base, offset, x);
    }

    @Override
    public char getCharVolatile(Object base, long offset) {
        AlignmentUtil.check2BytesAligned(offset);
        return UnsafeUtil.UNSAFE.getCharVolatile(base, offset);
    }

    @Override
    public void putCharVolatile(Object base, long offset, char x) {
        AlignmentUtil.check2BytesAligned(offset);
        UnsafeUtil.UNSAFE.putCharVolatile(base, offset, x);
    }

    @Override
    public short getShortVolatile(Object base, long offset) {
        AlignmentUtil.check2BytesAligned(offset);
        return UnsafeUtil.UNSAFE.getShortVolatile(base, offset);
    }

    @Override
    public void putShortVolatile(Object base, long offset, short x) {
        AlignmentUtil.check2BytesAligned(offset);
        UnsafeUtil.UNSAFE.putShortVolatile(base, offset, x);
    }

    @Override
    public int getIntVolatile(Object base, long offset) {
        AlignmentUtil.check4BytesAligned(offset);
        return UnsafeUtil.UNSAFE.getIntVolatile(base, offset);
    }

    @Override
    public void putIntVolatile(Object base, long offset, int x) {
        AlignmentUtil.check4BytesAligned(offset);
        UnsafeUtil.UNSAFE.putIntVolatile(base, offset, x);
    }

    @Override
    public float getFloatVolatile(Object base, long offset) {
        AlignmentUtil.check4BytesAligned(offset);
        return UnsafeUtil.UNSAFE.getFloatVolatile(base, offset);
    }

    @Override
    public void putFloatVolatile(Object base, long offset, float x) {
        AlignmentUtil.check4BytesAligned(offset);
        UnsafeUtil.UNSAFE.putFloatVolatile(base, offset, x);
    }

    @Override
    public long getLongVolatile(Object base, long offset) {
        AlignmentUtil.check8BytesAligned(offset);
        return UnsafeUtil.UNSAFE.getLongVolatile(base, offset);
    }

    @Override
    public void putLongVolatile(Object base, long offset, long x) {
        AlignmentUtil.check8BytesAligned(offset);
        UnsafeUtil.UNSAFE.putLongVolatile(base, offset, x);
    }

    @Override
    public double getDoubleVolatile(Object base, long offset) {
        AlignmentUtil.check8BytesAligned(offset);
        return UnsafeUtil.UNSAFE.getDoubleVolatile(base, offset);
    }

    @Override
    public void putDoubleVolatile(Object base, long offset, double x) {
        AlignmentUtil.check8BytesAligned(offset);
        UnsafeUtil.UNSAFE.putDoubleVolatile(base, offset, x);
    }

    @Override
    public void putOrderedInt(Object base, long offset, int x) {
        AlignmentUtil.check4BytesAligned(offset);
        UnsafeUtil.UNSAFE.putOrderedInt(base, offset, x);
    }

    @Override
    public void putOrderedLong(Object base, long offset, long x) {
        AlignmentUtil.check8BytesAligned(offset);
        UnsafeUtil.UNSAFE.putOrderedLong(base, offset, x);
    }

    @Override
    public void putOrderedObject(Object base, long offset, Object x) {
        AlignmentUtil.checkReferenceAligned(offset);
        UnsafeUtil.UNSAFE.putOrderedObject(base, offset, x);
    }

    @Override
    public boolean compareAndSwapInt(Object base, long offset, int expected, int x) {
        AlignmentUtil.check4BytesAligned(offset);
        return UnsafeUtil.UNSAFE.compareAndSwapInt(base, offset, expected, x);
    }

    @Override
    public boolean compareAndSwapLong(Object base, long offset, long expected, long x) {
        AlignmentUtil.check8BytesAligned(offset);
        return UnsafeUtil.UNSAFE.compareAndSwapLong(base, offset, expected, x);
    }

    @Override
    public boolean compareAndSwapObject(Object base, long offset, Object expected, Object x) {
        AlignmentUtil.checkReferenceAligned(offset);
        return UnsafeUtil.UNSAFE.compareAndSwapObject(base, offset, expected, x);
    }
}

