/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

public enum CodecUtils {


    static int sanitize(String singleOctets, byte[] dest) {
        int capacity = dest.length;
        char[] src = singleOctets.toCharArray();
        int limit = 0;
        for (int i = 0; i < capacity; ++i) {
            char c = src[i];
            if (c == '\r' || c == '\n' || c == ' ') continue;
            if (c > '\u007f') {
                throw new IllegalArgumentException("Invalid character found at position " + i + " for " + singleOctets);
            }
            dest[limit++] = (byte)c;
        }
        return limit;
    }

    public static byte[] toBytesDirect(String singleOctets) {
        char[] src = singleOctets.toCharArray();
        byte[] dest = new byte[src.length];
        for (int i = 0; i < dest.length; ++i) {
            char c = src[i];
            if (c > '\u007f') {
                throw new IllegalArgumentException("Invalid character found at position " + i + " for " + singleOctets);
            }
            dest[i] = (byte)c;
        }
        return dest;
    }

    public static String toStringDirect(byte[] bytes) {
        char[] dest = new char[bytes.length];
        int i = 0;
        for (byte b : bytes) {
            dest[i++] = (char)b;
        }
        return new String(dest);
    }

    static void sanityCheckLastPos(int pos, int mask) {
        if ((pos & mask) != 0) {
            throw new IllegalArgumentException("Invalid last non-pad character detected");
        }
    }
}

