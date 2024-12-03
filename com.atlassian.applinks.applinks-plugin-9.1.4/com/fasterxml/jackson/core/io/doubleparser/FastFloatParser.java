/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import com.fasterxml.jackson.core.io.doubleparser.FloatBitsFromCharArray;
import com.fasterxml.jackson.core.io.doubleparser.FloatBitsFromCharSequence;

public class FastFloatParser {
    private static final FloatBitsFromCharArray CHAR_ARRAY_PARSER = new FloatBitsFromCharArray();
    private static final FloatBitsFromCharSequence CHAR_SEQ_PARSER = new FloatBitsFromCharSequence();

    private FastFloatParser() {
    }

    public static float parseFloat(CharSequence str) throws NumberFormatException {
        return FastFloatParser.parseFloat(str, 0, str.length());
    }

    public static float parseFloat(CharSequence str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_SEQ_PARSER.parseFloatingPointLiteral(str, offset, length);
        if (bitPattern == -1L) {
            throw new NumberFormatException("Illegal input");
        }
        return Float.intBitsToFloat((int)bitPattern);
    }

    public static float parseFloat(char[] str) throws NumberFormatException {
        return FastFloatParser.parseFloat(str, 0, str.length);
    }

    public static float parseFloat(char[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_ARRAY_PARSER.parseFloatingPointLiteral(str, offset, length);
        if (bitPattern == -1L) {
            throw new NumberFormatException("Illegal input");
        }
        return Float.intBitsToFloat((int)bitPattern);
    }

    public static long parseFloatBits(CharSequence str, int offset, int length) {
        return CHAR_SEQ_PARSER.parseFloatingPointLiteral(str, offset, length);
    }

    public static long parseFloatBits(char[] str, int offset, int length) {
        return CHAR_ARRAY_PARSER.parseFloatingPointLiteral(str, offset, length);
    }
}

