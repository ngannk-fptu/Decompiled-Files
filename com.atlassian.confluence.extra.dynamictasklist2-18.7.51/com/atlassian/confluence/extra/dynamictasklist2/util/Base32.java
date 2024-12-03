/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.dynamictasklist2.util;

public class Base32 {
    private static final String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int[] base32Lookup = new int[]{255, 255, 26, 27, 28, 29, 30, 31, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 255, 255, 255, 255, 255, 255, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 255, 255, 255, 255, 255};

    public static String encode(byte[] bytes) {
        int i = 0;
        int index = 0;
        int digit = 0;
        StringBuffer base32 = new StringBuffer((bytes.length + 7) * 8 / 5);
        while (i < bytes.length) {
            int currByte;
            int n = currByte = bytes[i] >= 0 ? bytes[i] : bytes[i] + 256;
            if (index > 3) {
                int nextByte = i + 1 < bytes.length ? (bytes[i + 1] >= 0 ? bytes[i + 1] : bytes[i + 1] + 256) : 0;
                digit = currByte & 255 >> index;
                index = (index + 5) % 8;
                digit <<= index;
                digit |= nextByte >> 8 - index;
                ++i;
            } else {
                digit = currByte >> 8 - (index + 5) & 0x1F;
                if ((index = (index + 5) % 8) == 0) {
                    ++i;
                }
            }
            base32.append(base32Chars.charAt(digit));
        }
        return base32.toString();
    }

    public static byte[] decode(String base32) {
        byte[] bytes = new byte[base32.length() * 5 / 8];
        int index = 0;
        int offset = 0;
        for (int i = 0; i < base32.length(); ++i) {
            int digit;
            int lookup = base32.charAt(i) - 48;
            if (lookup < 0 || lookup >= base32Lookup.length || (digit = base32Lookup[lookup]) == 255) continue;
            if (index <= 3) {
                if ((index = (index + 5) % 8) == 0) {
                    int n = offset++;
                    bytes[n] = (byte)(bytes[n] | digit);
                    if (offset < bytes.length) continue;
                    break;
                }
                int n = offset;
                bytes[n] = (byte)(bytes[n] | digit << 8 - index);
                continue;
            }
            index = (index + 5) % 8;
            int n = offset++;
            bytes[n] = (byte)(bytes[n] | digit >>> index);
            if (offset >= bytes.length) break;
            int n2 = offset;
            bytes[n2] = (byte)(bytes[n2] | digit << 8 - index);
        }
        return bytes;
    }
}

