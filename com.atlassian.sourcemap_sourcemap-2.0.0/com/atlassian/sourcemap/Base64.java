/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import java.util.Arrays;

class Base64 {
    private static final String BASE64_MAP = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final int[] BASE64_DECODE_MAP = new int[256];

    private Base64() {
    }

    public static char toBase64(int value) {
        assert (value <= 63 && value >= 0) : "value out of range:" + value;
        return BASE64_MAP.charAt(value);
    }

    public static int fromBase64(char c) {
        int result = BASE64_DECODE_MAP[c];
        assert (result != -1) : "invalid char";
        return BASE64_DECODE_MAP[c];
    }

    public static String base64EncodeInt(int value) {
        char[] c = new char[6];
        for (int i = 0; i < 5; ++i) {
            c[i] = Base64.toBase64(value >> 26 - i * 6 & 0x3F);
        }
        c[5] = Base64.toBase64(value << 4 & 0x3F);
        return new String(c);
    }

    static {
        Arrays.fill(BASE64_DECODE_MAP, -1);
        for (int i = 0; i < BASE64_MAP.length(); ++i) {
            Base64.BASE64_DECODE_MAP[BASE64_MAP.charAt((int)i)] = i;
        }
    }
}

