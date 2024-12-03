/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

public class ScannerUtilities {
    protected static final int[] IDENTIFIER_START = new int[]{0, 0, -2013265922, 0x7FFFFFE};
    protected static final int[] NAME = new int[]{0, 67051520, -2013265922, 0x7FFFFFE};
    protected static final int[] HEXADECIMAL = new int[]{0, 0x3FF0000, 126, 126};
    protected static final int[] STRING = new int[]{512, -133, -1, Integer.MAX_VALUE};
    protected static final int[] URI = new int[]{0, -902, -1, Integer.MAX_VALUE};

    protected ScannerUtilities() {
    }

    public static boolean isCSSSpace(char c) {
        return c <= ' ' && (4294981120L >> c & 1L) != 0L;
    }

    public static boolean isCSSIdentifierStartCharacter(char c) {
        return c >= '\u0080' || (IDENTIFIER_START[c >> 5] & 1 << (c & 0x1F)) != 0;
    }

    public static boolean isCSSNameCharacter(char c) {
        return c >= '\u0080' || (NAME[c >> 5] & 1 << (c & 0x1F)) != 0;
    }

    public static boolean isCSSHexadecimalCharacter(char c) {
        return c < '\u0080' && (HEXADECIMAL[c >> 5] & 1 << (c & 0x1F)) != 0;
    }

    public static boolean isCSSStringCharacter(char c) {
        return c >= '\u0080' || (STRING[c >> 5] & 1 << (c & 0x1F)) != 0;
    }

    public static boolean isCSSURICharacter(char c) {
        return c >= '\u0080' || (URI[c >> 5] & 1 << (c & 0x1F)) != 0;
    }
}

