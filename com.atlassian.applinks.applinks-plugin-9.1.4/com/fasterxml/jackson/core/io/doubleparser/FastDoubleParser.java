/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import com.fasterxml.jackson.core.io.doubleparser.DoubleBitsFromCharArray;
import com.fasterxml.jackson.core.io.doubleparser.DoubleBitsFromCharSequence;

public class FastDoubleParser {
    private static final DoubleBitsFromCharArray CHAR_ARRAY_PARSER = new DoubleBitsFromCharArray();
    private static final DoubleBitsFromCharSequence CHAR_SEQ_PARSER = new DoubleBitsFromCharSequence();

    private FastDoubleParser() {
    }

    public static double parseDouble(CharSequence str) throws NumberFormatException {
        return FastDoubleParser.parseDouble(str, 0, str.length());
    }

    public static double parseDouble(CharSequence str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_SEQ_PARSER.parseFloatingPointLiteral(str, offset, length);
        if (bitPattern == -1L) {
            throw new NumberFormatException("Illegal input");
        }
        return Double.longBitsToDouble(bitPattern);
    }

    public static double parseDouble(char[] str) throws NumberFormatException {
        return FastDoubleParser.parseDouble(str, 0, str.length);
    }

    public static double parseDouble(char[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_ARRAY_PARSER.parseFloatingPointLiteral(str, offset, length);
        if (bitPattern == -1L) {
            throw new NumberFormatException("Illegal input");
        }
        return Double.longBitsToDouble(bitPattern);
    }

    public static long parseDoubleBits(CharSequence str, int offset, int length) {
        return CHAR_SEQ_PARSER.parseFloatingPointLiteral(str, offset, length);
    }

    public static long parseDoubleBits(char[] str, int offset, int length) {
        return CHAR_ARRAY_PARSER.parseFloatingPointLiteral(str, offset, length);
    }
}

