/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

public class SmallFloat {
    public static byte floatToByte(float f, int numMantissaBits, int zeroExp) {
        int fzero = 63 - zeroExp << numMantissaBits;
        int bits = Float.floatToRawIntBits(f);
        int smallfloat = bits >> 24 - numMantissaBits;
        if (smallfloat <= fzero) {
            return bits <= 0 ? (byte)0 : 1;
        }
        if (smallfloat >= fzero + 256) {
            return -1;
        }
        return (byte)(smallfloat - fzero);
    }

    public static float byteToFloat(byte b, int numMantissaBits, int zeroExp) {
        if (b == 0) {
            return 0.0f;
        }
        int bits = (b & 0xFF) << 24 - numMantissaBits;
        return Float.intBitsToFloat(bits += 63 - zeroExp << 24);
    }

    public static byte floatToByte315(float f) {
        int bits = Float.floatToRawIntBits(f);
        int smallfloat = bits >> 21;
        if (smallfloat <= 384) {
            return bits <= 0 ? (byte)0 : 1;
        }
        if (smallfloat >= 640) {
            return -1;
        }
        return (byte)(smallfloat - 384);
    }

    public static float byte315ToFloat(byte b) {
        if (b == 0) {
            return 0.0f;
        }
        int bits = (b & 0xFF) << 21;
        return Float.intBitsToFloat(bits += 0x30000000);
    }

    public static byte floatToByte52(float f) {
        int bits = Float.floatToRawIntBits(f);
        int smallfloat = bits >> 19;
        if (smallfloat <= 1952) {
            return bits <= 0 ? (byte)0 : 1;
        }
        if (smallfloat >= 2208) {
            return -1;
        }
        return (byte)(smallfloat - 1952);
    }

    public static float byte52ToFloat(byte b) {
        if (b == 0) {
            return 0.0f;
        }
        int bits = (b & 0xFF) << 19;
        return Float.intBitsToFloat(bits += 0x3D000000);
    }
}

