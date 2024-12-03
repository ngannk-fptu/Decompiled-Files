/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import java.util.Arrays;

abstract class AbstractNumberParser {
    public static final String ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH = "offset < 0 or length > str.length";
    public static final String SYNTAX_ERROR = "illegal syntax";
    public static final String VALUE_EXCEEDS_LIMITS = "value exceeds limits";
    static final byte DECIMAL_POINT_CLASS = -4;
    static final byte OTHER_CLASS = -1;
    static final byte[] CHAR_TO_HEX_MAP;

    AbstractNumberParser() {
    }

    protected static byte charAt(byte[] str, int i, int endIndex) {
        return i < endIndex ? str[i] : (byte)0;
    }

    protected static char charAt(char[] str, int i, int endIndex) {
        return i < endIndex ? str[i] : (char)'\u0000';
    }

    protected static char charAt(CharSequence str, int i, int endIndex) {
        return i < endIndex ? str.charAt(i) : (char)'\u0000';
    }

    protected static int lookupHex(byte ch) {
        return CHAR_TO_HEX_MAP[ch & 0xFF];
    }

    protected static int lookupHex(char ch) {
        return ch < '\u0080' ? CHAR_TO_HEX_MAP[ch] : -1;
    }

    static {
        int ch;
        CHAR_TO_HEX_MAP = new byte[256];
        Arrays.fill(CHAR_TO_HEX_MAP, (byte)-1);
        for (ch = 48; ch <= 57; ch = (int)((char)(ch + 1))) {
            AbstractNumberParser.CHAR_TO_HEX_MAP[ch] = (byte)(ch - 48);
        }
        for (ch = 65; ch <= 70; ch = (int)((char)(ch + 1))) {
            AbstractNumberParser.CHAR_TO_HEX_MAP[ch] = (byte)(ch - 65 + 10);
        }
        for (ch = 97; ch <= 102; ch = (int)((char)(ch + 1))) {
            AbstractNumberParser.CHAR_TO_HEX_MAP[ch] = (byte)(ch - 97 + 10);
        }
        for (ch = 46; ch <= 46; ch = (int)((char)(ch + 1))) {
            AbstractNumberParser.CHAR_TO_HEX_MAP[ch] = -4;
        }
    }
}

