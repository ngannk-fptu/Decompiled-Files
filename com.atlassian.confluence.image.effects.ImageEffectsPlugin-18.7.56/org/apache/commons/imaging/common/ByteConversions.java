/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.RationalNumber;

public final class ByteConversions {
    private ByteConversions() {
    }

    public static byte[] toBytes(short value, ByteOrder byteOrder) {
        byte[] result = new byte[2];
        ByteConversions.toBytes(value, byteOrder, result, 0);
        return result;
    }

    public static byte[] toBytes(short[] values, ByteOrder byteOrder) {
        return ByteConversions.toBytes(values, 0, values.length, byteOrder);
    }

    private static byte[] toBytes(short[] values, int offset, int length, ByteOrder byteOrder) {
        byte[] result = new byte[length * 2];
        for (int i = 0; i < length; ++i) {
            ByteConversions.toBytes(values[offset + i], byteOrder, result, i * 2);
        }
        return result;
    }

    private static void toBytes(short value, ByteOrder byteOrder, byte[] result, int offset) {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            result[offset + 0] = (byte)(value >> 8);
            result[offset + 1] = (byte)(value >> 0);
        } else {
            result[offset + 1] = (byte)(value >> 8);
            result[offset + 0] = (byte)(value >> 0);
        }
    }

    public static byte[] toBytes(int value, ByteOrder byteOrder) {
        byte[] result = new byte[4];
        ByteConversions.toBytes(value, byteOrder, result, 0);
        return result;
    }

    public static byte[] toBytes(int[] values, ByteOrder byteOrder) {
        return ByteConversions.toBytes(values, 0, values.length, byteOrder);
    }

    private static byte[] toBytes(int[] values, int offset, int length, ByteOrder byteOrder) {
        byte[] result = new byte[length * 4];
        for (int i = 0; i < length; ++i) {
            ByteConversions.toBytes(values[offset + i], byteOrder, result, i * 4);
        }
        return result;
    }

    private static void toBytes(int value, ByteOrder byteOrder, byte[] result, int offset) {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            result[offset + 0] = (byte)(value >> 24);
            result[offset + 1] = (byte)(value >> 16);
            result[offset + 2] = (byte)(value >> 8);
            result[offset + 3] = (byte)(value >> 0);
        } else {
            result[offset + 3] = (byte)(value >> 24);
            result[offset + 2] = (byte)(value >> 16);
            result[offset + 1] = (byte)(value >> 8);
            result[offset + 0] = (byte)(value >> 0);
        }
    }

    public static byte[] toBytes(float value, ByteOrder byteOrder) {
        byte[] result = new byte[4];
        ByteConversions.toBytes(value, byteOrder, result, 0);
        return result;
    }

    public static byte[] toBytes(float[] values, ByteOrder byteOrder) {
        return ByteConversions.toBytes(values, 0, values.length, byteOrder);
    }

    private static byte[] toBytes(float[] values, int offset, int length, ByteOrder byteOrder) {
        byte[] result = new byte[length * 4];
        for (int i = 0; i < length; ++i) {
            ByteConversions.toBytes(values[offset + i], byteOrder, result, i * 4);
        }
        return result;
    }

    private static void toBytes(float value, ByteOrder byteOrder, byte[] result, int offset) {
        int bits = Float.floatToRawIntBits(value);
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            result[offset + 0] = (byte)(0xFF & bits >> 0);
            result[offset + 1] = (byte)(0xFF & bits >> 8);
            result[offset + 2] = (byte)(0xFF & bits >> 16);
            result[offset + 3] = (byte)(0xFF & bits >> 24);
        } else {
            result[offset + 3] = (byte)(0xFF & bits >> 0);
            result[offset + 2] = (byte)(0xFF & bits >> 8);
            result[offset + 1] = (byte)(0xFF & bits >> 16);
            result[offset + 0] = (byte)(0xFF & bits >> 24);
        }
    }

    public static byte[] toBytes(double value, ByteOrder byteOrder) {
        byte[] result = new byte[8];
        ByteConversions.toBytes(value, byteOrder, result, 0);
        return result;
    }

    public static byte[] toBytes(double[] values, ByteOrder byteOrder) {
        return ByteConversions.toBytes(values, 0, values.length, byteOrder);
    }

    private static byte[] toBytes(double[] values, int offset, int length, ByteOrder byteOrder) {
        byte[] result = new byte[length * 8];
        for (int i = 0; i < length; ++i) {
            ByteConversions.toBytes(values[offset + i], byteOrder, result, i * 8);
        }
        return result;
    }

    private static void toBytes(double value, ByteOrder byteOrder, byte[] result, int offset) {
        long bits = Double.doubleToRawLongBits(value);
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            result[offset + 0] = (byte)(0xFFL & bits >> 0);
            result[offset + 1] = (byte)(0xFFL & bits >> 8);
            result[offset + 2] = (byte)(0xFFL & bits >> 16);
            result[offset + 3] = (byte)(0xFFL & bits >> 24);
            result[offset + 4] = (byte)(0xFFL & bits >> 32);
            result[offset + 5] = (byte)(0xFFL & bits >> 40);
            result[offset + 6] = (byte)(0xFFL & bits >> 48);
            result[offset + 7] = (byte)(0xFFL & bits >> 56);
        } else {
            result[offset + 7] = (byte)(0xFFL & bits >> 0);
            result[offset + 6] = (byte)(0xFFL & bits >> 8);
            result[offset + 5] = (byte)(0xFFL & bits >> 16);
            result[offset + 4] = (byte)(0xFFL & bits >> 24);
            result[offset + 3] = (byte)(0xFFL & bits >> 32);
            result[offset + 2] = (byte)(0xFFL & bits >> 40);
            result[offset + 1] = (byte)(0xFFL & bits >> 48);
            result[offset + 0] = (byte)(0xFFL & bits >> 56);
        }
    }

    public static byte[] toBytes(RationalNumber value, ByteOrder byteOrder) {
        byte[] result = new byte[8];
        ByteConversions.toBytes(value, byteOrder, result, 0);
        return result;
    }

    public static byte[] toBytes(RationalNumber[] values, ByteOrder byteOrder) {
        return ByteConversions.toBytes(values, 0, values.length, byteOrder);
    }

    private static byte[] toBytes(RationalNumber[] values, int offset, int length, ByteOrder byteOrder) {
        byte[] result = new byte[length * 8];
        for (int i = 0; i < length; ++i) {
            ByteConversions.toBytes(values[offset + i], byteOrder, result, i * 8);
        }
        return result;
    }

    private static void toBytes(RationalNumber value, ByteOrder byteOrder, byte[] result, int offset) {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            result[offset + 0] = (byte)(value.numerator >> 24);
            result[offset + 1] = (byte)(value.numerator >> 16);
            result[offset + 2] = (byte)(value.numerator >> 8);
            result[offset + 3] = (byte)(value.numerator >> 0);
            result[offset + 4] = (byte)(value.divisor >> 24);
            result[offset + 5] = (byte)(value.divisor >> 16);
            result[offset + 6] = (byte)(value.divisor >> 8);
            result[offset + 7] = (byte)(value.divisor >> 0);
        } else {
            result[offset + 3] = (byte)(value.numerator >> 24);
            result[offset + 2] = (byte)(value.numerator >> 16);
            result[offset + 1] = (byte)(value.numerator >> 8);
            result[offset + 0] = (byte)(value.numerator >> 0);
            result[offset + 7] = (byte)(value.divisor >> 24);
            result[offset + 6] = (byte)(value.divisor >> 16);
            result[offset + 5] = (byte)(value.divisor >> 8);
            result[offset + 4] = (byte)(value.divisor >> 0);
        }
    }

    public static short toShort(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toShort(bytes, 0, byteOrder);
    }

    private static short toShort(byte[] bytes, int offset, ByteOrder byteOrder) {
        return (short)ByteConversions.toUInt16(bytes, offset, byteOrder);
    }

    public static short[] toShorts(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toShorts(bytes, 0, bytes.length, byteOrder);
    }

    private static short[] toShorts(byte[] bytes, int offset, int length, ByteOrder byteOrder) {
        short[] result = new short[length / 2];
        for (int i = 0; i < result.length; ++i) {
            result[i] = ByteConversions.toShort(bytes, offset + 2 * i, byteOrder);
        }
        return result;
    }

    public static int toUInt16(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toUInt16(bytes, 0, byteOrder);
    }

    public static int toUInt16(byte[] bytes, int offset, ByteOrder byteOrder) {
        int byte0 = 0xFF & bytes[offset + 0];
        int byte1 = 0xFF & bytes[offset + 1];
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return byte0 << 8 | byte1;
        }
        return byte1 << 8 | byte0;
    }

    public static int[] toUInt16s(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toUInt16s(bytes, 0, bytes.length, byteOrder);
    }

    private static int[] toUInt16s(byte[] bytes, int offset, int length, ByteOrder byteOrder) {
        int[] result = new int[length / 2];
        for (int i = 0; i < result.length; ++i) {
            result[i] = ByteConversions.toUInt16(bytes, offset + 2 * i, byteOrder);
        }
        return result;
    }

    public static int toInt(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toInt(bytes, 0, byteOrder);
    }

    public static int toInt(byte[] bytes, int offset, ByteOrder byteOrder) {
        int byte0 = 0xFF & bytes[offset + 0];
        int byte1 = 0xFF & bytes[offset + 1];
        int byte2 = 0xFF & bytes[offset + 2];
        int byte3 = 0xFF & bytes[offset + 3];
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3;
        }
        return byte3 << 24 | byte2 << 16 | byte1 << 8 | byte0;
    }

    public static int[] toInts(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toInts(bytes, 0, bytes.length, byteOrder);
    }

    private static int[] toInts(byte[] bytes, int offset, int length, ByteOrder byteOrder) {
        int[] result = new int[length / 4];
        for (int i = 0; i < result.length; ++i) {
            result[i] = ByteConversions.toInt(bytes, offset + 4 * i, byteOrder);
        }
        return result;
    }

    public static float toFloat(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toFloat(bytes, 0, byteOrder);
    }

    private static float toFloat(byte[] bytes, int offset, ByteOrder byteOrder) {
        int byte0 = 0xFF & bytes[offset + 0];
        int byte1 = 0xFF & bytes[offset + 1];
        int byte2 = 0xFF & bytes[offset + 2];
        int byte3 = 0xFF & bytes[offset + 3];
        int bits = byteOrder == ByteOrder.BIG_ENDIAN ? byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3 << 0 : byte3 << 24 | byte2 << 16 | byte1 << 8 | byte0 << 0;
        return Float.intBitsToFloat(bits);
    }

    public static float[] toFloats(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toFloats(bytes, 0, bytes.length, byteOrder);
    }

    private static float[] toFloats(byte[] bytes, int offset, int length, ByteOrder byteOrder) {
        float[] result = new float[length / 4];
        for (int i = 0; i < result.length; ++i) {
            result[i] = ByteConversions.toFloat(bytes, offset + 4 * i, byteOrder);
        }
        return result;
    }

    public static double toDouble(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toDouble(bytes, 0, byteOrder);
    }

    private static double toDouble(byte[] bytes, int offset, ByteOrder byteOrder) {
        long byte0 = 0xFFL & (long)bytes[offset + 0];
        long byte1 = 0xFFL & (long)bytes[offset + 1];
        long byte2 = 0xFFL & (long)bytes[offset + 2];
        long byte3 = 0xFFL & (long)bytes[offset + 3];
        long byte4 = 0xFFL & (long)bytes[offset + 4];
        long byte5 = 0xFFL & (long)bytes[offset + 5];
        long byte6 = 0xFFL & (long)bytes[offset + 6];
        long byte7 = 0xFFL & (long)bytes[offset + 7];
        long bits = byteOrder == ByteOrder.BIG_ENDIAN ? byte0 << 56 | byte1 << 48 | byte2 << 40 | byte3 << 32 | byte4 << 24 | byte5 << 16 | byte6 << 8 | byte7 << 0 : byte7 << 56 | byte6 << 48 | byte5 << 40 | byte4 << 32 | byte3 << 24 | byte2 << 16 | byte1 << 8 | byte0 << 0;
        return Double.longBitsToDouble(bits);
    }

    public static double[] toDoubles(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toDoubles(bytes, 0, bytes.length, byteOrder);
    }

    private static double[] toDoubles(byte[] bytes, int offset, int length, ByteOrder byteOrder) {
        double[] result = new double[length / 8];
        for (int i = 0; i < result.length; ++i) {
            result[i] = ByteConversions.toDouble(bytes, offset + 8 * i, byteOrder);
        }
        return result;
    }

    public static RationalNumber toRational(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toRational(bytes, 0, byteOrder);
    }

    private static RationalNumber toRational(byte[] bytes, int offset, ByteOrder byteOrder) {
        int divisor;
        int numerator;
        int byte0 = 0xFF & bytes[offset + 0];
        int byte1 = 0xFF & bytes[offset + 1];
        int byte2 = 0xFF & bytes[offset + 2];
        int byte3 = 0xFF & bytes[offset + 3];
        int byte4 = 0xFF & bytes[offset + 4];
        int byte5 = 0xFF & bytes[offset + 5];
        int byte6 = 0xFF & bytes[offset + 6];
        int byte7 = 0xFF & bytes[offset + 7];
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            numerator = byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3;
            divisor = byte4 << 24 | byte5 << 16 | byte6 << 8 | byte7;
        } else {
            numerator = byte3 << 24 | byte2 << 16 | byte1 << 8 | byte0;
            divisor = byte7 << 24 | byte6 << 16 | byte5 << 8 | byte4;
        }
        return new RationalNumber(numerator, divisor);
    }

    public static RationalNumber[] toRationals(byte[] bytes, ByteOrder byteOrder) {
        return ByteConversions.toRationals(bytes, 0, bytes.length, byteOrder);
    }

    private static RationalNumber[] toRationals(byte[] bytes, int offset, int length, ByteOrder byteOrder) {
        RationalNumber[] result = new RationalNumber[length / 8];
        for (int i = 0; i < result.length; ++i) {
            result[i] = ByteConversions.toRational(bytes, offset + 8 * i, byteOrder);
        }
        return result;
    }
}

