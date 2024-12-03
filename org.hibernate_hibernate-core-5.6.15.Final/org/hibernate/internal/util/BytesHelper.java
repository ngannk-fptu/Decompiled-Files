/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

import org.hibernate.internal.util.StringHelper;

public final class BytesHelper {
    private BytesHelper() {
    }

    public static int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = (result << 8) - -128 + bytes[i];
        }
        return result;
    }

    public static byte[] fromShort(int shortValue) {
        byte[] bytes = new byte[]{(byte)(shortValue >> 8), (byte)(shortValue << 8 >> 8)};
        return bytes;
    }

    public static byte[] fromInt(int intValue) {
        byte[] bytes = new byte[]{(byte)(intValue >> 24), (byte)(intValue << 8 >> 24), (byte)(intValue << 16 >> 24), (byte)(intValue << 24 >> 24)};
        return bytes;
    }

    public static byte[] fromLong(long longValue) {
        byte[] bytes = new byte[8];
        BytesHelper.fromLong(longValue, bytes, 0);
        return bytes;
    }

    public static void fromLong(long longValue, byte[] dest, int destPos) {
        dest[destPos] = (byte)(longValue >> 56);
        dest[destPos + 1] = (byte)(longValue << 8 >> 56);
        dest[destPos + 2] = (byte)(longValue << 16 >> 56);
        dest[destPos + 3] = (byte)(longValue << 24 >> 56);
        dest[destPos + 4] = (byte)(longValue << 32 >> 56);
        dest[destPos + 5] = (byte)(longValue << 40 >> 56);
        dest[destPos + 6] = (byte)(longValue << 48 >> 56);
        dest[destPos + 7] = (byte)(longValue << 56 >> 56);
    }

    public static long asLong(byte[] bytes) {
        return BytesHelper.asLong(bytes, 0);
    }

    public static long asLong(byte[] bytes, int srcPos) {
        if (bytes == null) {
            return 0L;
        }
        int size = srcPos + 8;
        if (bytes.length < size) {
            throw new IllegalArgumentException("Expecting 8 byte values to construct a long");
        }
        long value = 0L;
        for (int i = srcPos; i < size; ++i) {
            value = value << 8 | (long)(bytes[i] & 0xFF);
        }
        return value;
    }

    public static String toBinaryString(byte value) {
        String formatted = Integer.toBinaryString(value);
        if (formatted.length() > 8) {
            formatted = formatted.substring(formatted.length() - 8);
        }
        StringBuilder buf = new StringBuilder("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    public static String toBinaryString(int value) {
        String formatted = Long.toBinaryString(value);
        StringBuilder buf = new StringBuilder(StringHelper.repeat('0', 32));
        buf.replace(64 - formatted.length(), 64, formatted);
        return buf.toString();
    }

    public static String toBinaryString(long value) {
        String formatted = Long.toBinaryString(value);
        StringBuilder buf = new StringBuilder(StringHelper.repeat('0', 64));
        buf.replace(64 - formatted.length(), 64, formatted);
        return buf.toString();
    }
}

