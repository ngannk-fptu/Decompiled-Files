/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.fpx;

import java.text.DecimalFormat;

public class FPXUtils {
    public static final short getShortLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xFF;
        int b1 = data[offset + 1] & 0xFF;
        return (short)(b1 << 8 | b0);
    }

    public static final int getUnsignedShortLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xFF;
        int b1 = data[offset + 1] & 0xFF;
        return b1 << 8 | b0;
    }

    public static final int getIntLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xFF;
        int b1 = data[offset + 1] & 0xFF;
        int b2 = data[offset + 2] & 0xFF;
        int b3 = data[offset + 3] & 0xFF;
        return b3 << 24 | b2 << 16 | b1 << 8 | b0;
    }

    public static final long getUnsignedIntLE(byte[] data, int offset) {
        long b0 = data[offset] & 0xFF;
        long b1 = data[offset + 1] & 0xFF;
        long b2 = data[offset + 2] & 0xFF;
        long b3 = data[offset + 3] & 0xFF;
        return b3 << 24 | b2 << 16 | b1 << 8 | b0;
    }

    public static final String getString(byte[] data, int offset, int length) {
        if (length == 0) {
            return "<none>";
        }
        length = length / 2 - 1;
        StringBuffer b = new StringBuffer(length);
        for (int i = 0; i < length; ++i) {
            int c = FPXUtils.getUnsignedShortLE(data, offset);
            b.append((char)c);
            offset += 2;
        }
        return b.toString();
    }

    private static void printDecimal(int i) {
        DecimalFormat d = new DecimalFormat("00000");
        System.out.print(d.format(i));
    }

    private static void printHex(byte b) {
        int i = b & 0xFF;
        int hi = i / 16;
        int lo = i % 16;
        if (hi < 10) {
            System.out.print((char)(48 + hi));
        } else {
            System.out.print((char)(97 + hi - 10));
        }
        if (lo < 10) {
            System.out.print((char)(48 + lo));
        } else {
            System.out.print((char)(97 + lo - 10));
        }
    }

    private static void printChar(byte b) {
        char c = (char)(b & 0xFF);
        if (c >= '!' && c <= '~') {
            System.out.print(' ');
            System.out.print(c);
        } else if (c == '\u0000') {
            System.out.print("^@");
        } else if (c < ' ') {
            System.out.print('^');
            System.out.print((char)(65 + c - 1));
        } else if (c == ' ') {
            System.out.print("__");
        } else {
            System.out.print("??");
        }
    }

    public static void dumpBuffer(byte[] buf, int offset, int length, int printOffset) {
        int lines = length / 8;
        for (int j = 0; j < lines; ++j) {
            int i;
            FPXUtils.printDecimal(printOffset);
            System.out.print(": ");
            for (i = 0; i < 8; ++i) {
                FPXUtils.printHex(buf[offset + i]);
                System.out.print("  ");
            }
            for (i = 0; i < 8; ++i) {
                FPXUtils.printChar(buf[offset + i]);
                System.out.print("  ");
            }
            offset += 8;
            printOffset += 8;
            System.out.println();
        }
    }
}

