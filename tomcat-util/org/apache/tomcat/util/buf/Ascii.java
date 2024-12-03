/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

public final class Ascii {
    private static final byte[] toLower = new byte[256];
    private static final boolean[] isDigit = new boolean[256];
    private static final long OVERFLOW_LIMIT = 0xCCCCCCCCCCCCCCCL;

    public static int toLower(int c) {
        return toLower[c & 0xFF] & 0xFF;
    }

    private static boolean isDigit(int c) {
        return isDigit[c & 0xFF];
    }

    public static long parseLong(byte[] b, int off, int len) throws NumberFormatException {
        byte c;
        if (b == null || len <= 0 || !Ascii.isDigit(c = b[off++])) {
            throw new NumberFormatException();
        }
        long n = c - 48;
        while (--len > 0) {
            if (Ascii.isDigit(c = b[off++]) && (n < 0xCCCCCCCCCCCCCCCL || n == 0xCCCCCCCCCCCCCCCL && c - 48 < 8)) {
                n = n * 10L + (long)c - 48L;
                continue;
            }
            throw new NumberFormatException();
        }
        return n;
    }

    static {
        for (int i = 0; i < 256; ++i) {
            Ascii.toLower[i] = (byte)i;
        }
        for (int lc = 97; lc <= 122; ++lc) {
            int uc = lc + 65 - 97;
            Ascii.toLower[uc] = (byte)lc;
        }
        for (int d = 48; d <= 57; ++d) {
            Ascii.isDigit[d] = true;
        }
    }
}

