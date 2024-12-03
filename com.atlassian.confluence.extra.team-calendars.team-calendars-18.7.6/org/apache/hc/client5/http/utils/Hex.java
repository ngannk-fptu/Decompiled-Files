/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.utils;

import org.apache.hc.core5.annotation.Internal;

@Internal
public class Hex {
    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private Hex() {
    }

    public static String encodeHexString(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        Hex.encodeHex(bytes, 0, bytes.length, DIGITS_LOWER, out, 0);
        return new String(out);
    }

    private static void encodeHex(byte[] data, int dataOffset, int dataLen, char[] toDigits, char[] out, int outOffset) {
        int j = outOffset;
        for (int i = dataOffset; i < dataOffset + dataLen; ++i) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0xF & data[i]];
        }
    }
}

