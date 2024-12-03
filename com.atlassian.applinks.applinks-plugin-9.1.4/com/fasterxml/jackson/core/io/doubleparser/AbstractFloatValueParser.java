/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

abstract class AbstractFloatValueParser {
    static final long PARSE_ERROR = -1L;
    static final long MINIMAL_NINETEEN_DIGIT_INTEGER = 1000000000000000000L;
    static final int MAX_EXPONENT_NUMBER = 1024;
    static final byte DECIMAL_POINT_CLASS = -4;
    static final byte OTHER_CLASS = -1;
    static final byte[] CHAR_TO_HEX_MAP;

    AbstractFloatValueParser() {
    }

    static {
        int ch;
        CHAR_TO_HEX_MAP = new byte[128];
        for (ch = 0; ch < CHAR_TO_HEX_MAP.length; ch = (int)((char)(ch + 1))) {
            AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch] = -1;
        }
        for (ch = 48; ch <= 57; ch = (int)((char)(ch + 1))) {
            AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch] = (byte)(ch - 48);
        }
        for (ch = 65; ch <= 70; ch = (int)((char)(ch + 1))) {
            AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch] = (byte)(ch - 65 + 10);
        }
        for (ch = 97; ch <= 102; ch = (int)((char)(ch + 1))) {
            AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch] = (byte)(ch - 97 + 10);
        }
        for (ch = 46; ch <= 46; ch = (int)((char)(ch + 1))) {
            AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch] = -4;
        }
    }
}

