/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

public class HexCoDec {
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static byte[] decode(String hexValue) {
        return HexCoDec.decode(hexValue.toCharArray());
    }

    public static byte[] decode(char[] hexChars) {
        return HexCoDec.decode(hexChars, 0, hexChars.length);
    }

    public static byte[] decode(char[] hexChars, int startIndex, int length) {
        if ((length & 1) != 0) {
            throw new IllegalArgumentException("Length must be even");
        }
        byte[] result = new byte[length / 2];
        for (int j = 0; j < result.length; ++j) {
            result[j] = (byte)(HexCoDec.hexCharToNibble(hexChars[startIndex++]) * 16 + HexCoDec.hexCharToNibble(hexChars[startIndex++]));
        }
        return result;
    }

    public static char[] encode(byte[] bites) {
        return HexCoDec.encode(bites, 0, bites.length);
    }

    public static char[] encode(byte[] bites, int startIndex, int length) {
        char[] result = new char[length * 2];
        int j = 0;
        for (int i = 0; i < length; ++i) {
            int bite = bites[startIndex++] & 0xFF;
            result[j++] = HEX_CHARS[bite >> 4];
            result[j++] = HEX_CHARS[bite & 0xF];
        }
        return result;
    }

    private static int hexCharToNibble(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - 48;
        }
        if (ch >= 'a' && ch <= 'f') {
            return ch - 97 + 10;
        }
        if (ch >= 'A' && ch <= 'F') {
            return ch - 65 + 10;
        }
        throw new IllegalArgumentException("Not a hex char - '" + ch + "'");
    }
}

