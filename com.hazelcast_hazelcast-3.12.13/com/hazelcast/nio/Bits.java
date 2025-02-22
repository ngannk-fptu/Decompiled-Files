/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.internal.memory.impl.EndiannessUtil;
import com.hazelcast.spi.annotation.PrivateApi;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@PrivateApi
public final class Bits {
    public static final int BYTE_SIZE_IN_BYTES = 1;
    public static final int BOOLEAN_SIZE_IN_BYTES = 1;
    public static final int SHORT_SIZE_IN_BYTES = 2;
    public static final int CHAR_SIZE_IN_BYTES = 2;
    public static final int INT_SIZE_IN_BYTES = 4;
    public static final int FLOAT_SIZE_IN_BYTES = 4;
    public static final int LONG_SIZE_IN_BYTES = 8;
    public static final int DOUBLE_SIZE_IN_BYTES = 8;
    public static final int NULL_ARRAY_LENGTH = -1;
    public static final int CACHE_LINE_LENGTH = 64;
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    private Bits() {
    }

    public static char readChar(byte[] buffer, int pos, boolean useBigEndian) {
        return EndiannessUtil.readChar(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, useBigEndian);
    }

    public static char readCharB(byte[] buffer, int pos) {
        return EndiannessUtil.readCharB(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos);
    }

    public static char readCharL(byte[] buffer, int pos) {
        return EndiannessUtil.readCharL(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos);
    }

    public static void writeChar(byte[] buffer, int pos, char v, boolean useBigEndian) {
        EndiannessUtil.writeChar(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v, useBigEndian);
    }

    public static void writeCharB(byte[] buffer, int pos, char v) {
        EndiannessUtil.writeCharB(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v);
    }

    public static void writeCharL(byte[] buffer, int pos, char v) {
        EndiannessUtil.writeCharL(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v);
    }

    public static short readShort(byte[] buffer, int pos, boolean useBigEndian) {
        return EndiannessUtil.readShort(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, useBigEndian);
    }

    public static short readShortB(byte[] buffer, int pos) {
        return EndiannessUtil.readShortB(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos);
    }

    public static short readShortL(byte[] buffer, int pos) {
        return EndiannessUtil.readShortL(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos);
    }

    public static void writeShort(byte[] buffer, int pos, short v, boolean useBigEndian) {
        EndiannessUtil.writeShort(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v, useBigEndian);
    }

    public static void writeShortB(byte[] buffer, int pos, short v) {
        EndiannessUtil.writeShortB(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v);
    }

    public static void writeShortL(byte[] buffer, int pos, short v) {
        EndiannessUtil.writeShortL(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v);
    }

    public static int readInt(byte[] buffer, int pos, boolean useBigEndian) {
        return EndiannessUtil.readInt(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, useBigEndian);
    }

    public static int readIntB(byte[] buffer, int pos) {
        return EndiannessUtil.readIntB(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos);
    }

    public static int readIntL(byte[] buffer, int pos) {
        return EndiannessUtil.readIntL(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos);
    }

    public static int readIntL(ByteBuffer buffer) {
        int byte3 = buffer.get() & 0xFF;
        int byte2 = (buffer.get() & 0xFF) << 8;
        int byte1 = (buffer.get() & 0xFF) << 16;
        int byte0 = (buffer.get() & 0xFF) << 24;
        return byte3 | byte2 | byte1 | byte0;
    }

    public static void writeInt(byte[] buffer, int pos, int v, boolean useBigEndian) {
        EndiannessUtil.writeInt(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v, useBigEndian);
    }

    public static void writeIntB(byte[] buffer, int pos, int v) {
        EndiannessUtil.writeIntB(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v);
    }

    public static void writeIntL(byte[] buffer, int pos, int v) {
        EndiannessUtil.writeIntL(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v);
    }

    public static long readLong(byte[] buffer, int pos, boolean useBigEndian) {
        return EndiannessUtil.readLong(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, useBigEndian);
    }

    public static long readLongB(byte[] buffer, int pos) {
        return EndiannessUtil.readLongB(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos);
    }

    public static long readLongL(byte[] buffer, int pos) {
        return EndiannessUtil.readLongL(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos);
    }

    public static void writeLong(byte[] buffer, int pos, long v, boolean useBigEndian) {
        EndiannessUtil.writeLong(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v, useBigEndian);
    }

    public static void writeLongB(byte[] buffer, int pos, long v) {
        EndiannessUtil.writeLongB(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v);
    }

    public static void writeLongL(byte[] buffer, int pos, long v) {
        EndiannessUtil.writeLongL(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, v);
    }

    public static int writeUtf8Char(byte[] buffer, int pos, int c) {
        return EndiannessUtil.writeUtf8Char(EndiannessUtil.BYTE_ARRAY_ACCESS, buffer, pos, c);
    }

    public static int readUtf8Char(byte[] buffer, int pos, char[] dst, int dstPos) throws IOException {
        return EndiannessUtil.readUtf8Char(buffer, pos, dst, dstPos);
    }

    public static char readUtf8Char(DataInput in, byte firstByte) throws IOException {
        return EndiannessUtil.readUtf8Char(in, firstByte);
    }

    public static byte setBit(byte value, int bit) {
        value = (byte)(value | 1 << bit);
        return value;
    }

    public static byte clearBit(byte value, int bit) {
        value = (byte)(value & ~(1 << bit));
        return value;
    }

    public static byte invertBit(byte value, int bit) {
        value = (byte)(value ^ 1 << bit);
        return value;
    }

    public static int setBit(int value, int bit) {
        return value |= 1 << bit;
    }

    public static int clearBit(int value, int bit) {
        return value &= ~(1 << bit);
    }

    public static int invertBit(int value, int bit) {
        return value ^= 1 << bit;
    }

    public static boolean isBitSet(int value, int bit) {
        return (value & 1 << bit) != 0;
    }

    public static int combineToInt(short x, short y) {
        return x << 16 | y & 0xFFFF;
    }

    public static short extractShort(int value, boolean lowerBits) {
        return (short)(lowerBits ? value : value >> 16);
    }

    public static long combineToLong(int x, int y) {
        return (long)x << 32 | (long)y & 0xFFFFFFFFL;
    }

    public static int extractInt(long value, boolean lowerBits) {
        return (int)(lowerBits ? value : value >> 32);
    }
}

