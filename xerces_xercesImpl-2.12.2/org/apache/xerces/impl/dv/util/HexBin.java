/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.util;

public final class HexBin {
    private static final int BASELENGTH = 128;
    private static final int LOOKUPLENGTH = 16;
    private static final byte[] hexNumberTable;
    private static final char[] lookUpHexAlphabet;

    public static String encode(byte[] byArray) {
        if (byArray == null) {
            return null;
        }
        int n = byArray.length;
        int n2 = n * 2;
        char[] cArray = new char[n2];
        for (int i = 0; i < n; ++i) {
            int n3 = byArray[i];
            if (n3 < 0) {
                n3 += 256;
            }
            cArray[i * 2] = lookUpHexAlphabet[n3 >> 4];
            cArray[i * 2 + 1] = lookUpHexAlphabet[n3 & 0xF];
        }
        return new String(cArray);
    }

    public static byte[] decode(String string) {
        if (string == null) {
            return null;
        }
        int n = string.length();
        if (n % 2 != 0) {
            return null;
        }
        char[] cArray = string.toCharArray();
        int n2 = n / 2;
        byte[] byArray = new byte[n2];
        for (int i = 0; i < n2; ++i) {
            int n3;
            int n4;
            char c = cArray[i * 2];
            int n5 = n4 = c < '\u0080' ? hexNumberTable[c] : -1;
            if (n4 == -1) {
                return null;
            }
            c = cArray[i * 2 + 1];
            int n6 = n3 = c < '\u0080' ? hexNumberTable[c] : -1;
            if (n3 == -1) {
                return null;
            }
            byArray[i] = (byte)(n4 << 4 | n3);
        }
        return byArray;
    }

    static {
        int n;
        hexNumberTable = new byte[128];
        lookUpHexAlphabet = new char[16];
        for (n = 0; n < 128; ++n) {
            HexBin.hexNumberTable[n] = -1;
        }
        for (n = 57; n >= 48; --n) {
            HexBin.hexNumberTable[n] = (byte)(n - 48);
        }
        for (n = 70; n >= 65; --n) {
            HexBin.hexNumberTable[n] = (byte)(n - 65 + 10);
        }
        for (n = 102; n >= 97; --n) {
            HexBin.hexNumberTable[n] = (byte)(n - 97 + 10);
        }
        for (n = 0; n < 10; ++n) {
            HexBin.lookUpHexAlphabet[n] = (char)(48 + n);
        }
        for (n = 10; n <= 15; ++n) {
            HexBin.lookUpHexAlphabet[n] = (char)(65 + n - 10);
        }
    }
}

