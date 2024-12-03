/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.staxex;

class Base64Encoder {
    private static final char[] encodeMap = Base64Encoder.initEncodeMap();

    Base64Encoder() {
    }

    private static char[] initEncodeMap() {
        int i;
        char[] map = new char[64];
        for (i = 0; i < 26; ++i) {
            map[i] = (char)(65 + i);
        }
        for (i = 26; i < 52; ++i) {
            map[i] = (char)(97 + (i - 26));
        }
        for (i = 52; i < 62; ++i) {
            map[i] = (char)(48 + (i - 52));
        }
        map[62] = 43;
        map[63] = 47;
        return map;
    }

    public static char encode(int i) {
        return encodeMap[i & 0x3F];
    }

    public static byte encodeByte(int i) {
        return (byte)encodeMap[i & 0x3F];
    }

    public static String print(byte[] input, int offset, int len) {
        char[] buf = new char[(len + 2) / 3 * 4];
        int ptr = Base64Encoder.print(input, offset, len, buf, 0);
        assert (ptr == buf.length);
        return new String(buf);
    }

    public static int print(byte[] input, int offset, int len, char[] buf, int ptr) {
        block4: for (int i = offset; i < len; i += 3) {
            switch (len - i) {
                case 1: {
                    buf[ptr++] = Base64Encoder.encode(input[i] >> 2);
                    buf[ptr++] = Base64Encoder.encode((input[i] & 3) << 4);
                    buf[ptr++] = 61;
                    buf[ptr++] = 61;
                    continue block4;
                }
                case 2: {
                    buf[ptr++] = Base64Encoder.encode(input[i] >> 2);
                    buf[ptr++] = Base64Encoder.encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF);
                    buf[ptr++] = Base64Encoder.encode((input[i + 1] & 0xF) << 2);
                    buf[ptr++] = 61;
                    continue block4;
                }
                default: {
                    buf[ptr++] = Base64Encoder.encode(input[i] >> 2);
                    buf[ptr++] = Base64Encoder.encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF);
                    buf[ptr++] = Base64Encoder.encode((input[i + 1] & 0xF) << 2 | input[i + 2] >> 6 & 3);
                    buf[ptr++] = Base64Encoder.encode(input[i + 2] & 0x3F);
                }
            }
        }
        return ptr;
    }
}

