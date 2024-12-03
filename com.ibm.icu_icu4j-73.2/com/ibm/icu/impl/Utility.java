/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.IllegalIcuArgumentException;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeMatcher;
import com.ibm.icu.util.ICUUncheckedIOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Utility {
    private static final char APOSTROPHE = '\'';
    private static final char BACKSLASH = '\\';
    private static final int MAGIC_UNSIGNED = Integer.MIN_VALUE;
    private static final char ESCAPE = '\ua5a5';
    static final byte ESCAPE_BYTE = -91;
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    static final char[] HEX_DIGIT = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char[] UNESCAPE_MAP = new char[]{'a', '\u0007', 'b', '\b', 'e', '\u001b', 'f', '\f', 'n', '\n', 'r', '\r', 't', '\t', 'v', '\u000b'};
    static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static final boolean arrayEquals(Object[] source, Object target) {
        if (source == null) {
            return target == null;
        }
        if (!(target instanceof Object[])) {
            return false;
        }
        Object[] targ = (Object[])target;
        return source.length == targ.length && Utility.arrayRegionMatches(source, 0, targ, 0, source.length);
    }

    public static final boolean arrayEquals(int[] source, Object target) {
        if (source == null) {
            return target == null;
        }
        if (!(target instanceof int[])) {
            return false;
        }
        int[] targ = (int[])target;
        return source.length == targ.length && Utility.arrayRegionMatches(source, 0, targ, 0, source.length);
    }

    public static final boolean arrayEquals(double[] source, Object target) {
        if (source == null) {
            return target == null;
        }
        if (!(target instanceof double[])) {
            return false;
        }
        double[] targ = (double[])target;
        return source.length == targ.length && Utility.arrayRegionMatches(source, 0, targ, 0, source.length);
    }

    public static final boolean arrayEquals(byte[] source, Object target) {
        if (source == null) {
            return target == null;
        }
        if (!(target instanceof byte[])) {
            return false;
        }
        byte[] targ = (byte[])target;
        return source.length == targ.length && Utility.arrayRegionMatches(source, 0, targ, 0, source.length);
    }

    public static final boolean arrayEquals(Object source, Object target) {
        if (source == null) {
            return target == null;
        }
        if (source instanceof Object[]) {
            return Utility.arrayEquals((Object[])source, target);
        }
        if (source instanceof int[]) {
            return Utility.arrayEquals((int[])source, target);
        }
        if (source instanceof double[]) {
            return Utility.arrayEquals((double[])source, target);
        }
        if (source instanceof byte[]) {
            return Utility.arrayEquals((byte[])source, target);
        }
        return source.equals(target);
    }

    public static final boolean arrayRegionMatches(Object[] source, int sourceStart, Object[] target, int targetStart, int len) {
        int sourceEnd = sourceStart + len;
        int delta = targetStart - sourceStart;
        for (int i = sourceStart; i < sourceEnd; ++i) {
            if (Utility.arrayEquals(source[i], target[i + delta])) continue;
            return false;
        }
        return true;
    }

    public static final boolean arrayRegionMatches(char[] source, int sourceStart, char[] target, int targetStart, int len) {
        int sourceEnd = sourceStart + len;
        int delta = targetStart - sourceStart;
        for (int i = sourceStart; i < sourceEnd; ++i) {
            if (source[i] == target[i + delta]) continue;
            return false;
        }
        return true;
    }

    public static final boolean arrayRegionMatches(int[] source, int sourceStart, int[] target, int targetStart, int len) {
        int sourceEnd = sourceStart + len;
        int delta = targetStart - sourceStart;
        for (int i = sourceStart; i < sourceEnd; ++i) {
            if (source[i] == target[i + delta]) continue;
            return false;
        }
        return true;
    }

    public static final boolean arrayRegionMatches(double[] source, int sourceStart, double[] target, int targetStart, int len) {
        int sourceEnd = sourceStart + len;
        int delta = targetStart - sourceStart;
        for (int i = sourceStart; i < sourceEnd; ++i) {
            if (source[i] == target[i + delta]) continue;
            return false;
        }
        return true;
    }

    public static final boolean arrayRegionMatches(byte[] source, int sourceStart, byte[] target, int targetStart, int len) {
        int sourceEnd = sourceStart + len;
        int delta = targetStart - sourceStart;
        for (int i = sourceStart; i < sourceEnd; ++i) {
            if (source[i] == target[i + delta]) continue;
            return false;
        }
        return true;
    }

    public static final boolean sameObjects(Object a, Object b) {
        return a == b;
    }

    public static <T extends Comparable<T>> int checkCompare(T a, T b) {
        return a == null ? (b == null ? 0 : -1) : (b == null ? 1 : a.compareTo(b));
    }

    public static int checkHash(Object a) {
        return a == null ? 0 : a.hashCode();
    }

    public static final String arrayToRLEString(int[] a) {
        StringBuilder buffer = new StringBuilder();
        Utility.appendInt(buffer, a.length);
        int runValue = a[0];
        int runLength = 1;
        for (int i = 1; i < a.length; ++i) {
            int s = a[i];
            if (s == runValue && runLength < 65535) {
                ++runLength;
                continue;
            }
            Utility.encodeRun(buffer, runValue, runLength);
            runValue = s;
            runLength = 1;
        }
        Utility.encodeRun(buffer, runValue, runLength);
        return buffer.toString();
    }

    public static final String arrayToRLEString(short[] a) {
        StringBuilder buffer = new StringBuilder();
        buffer.append((char)(a.length >> 16));
        buffer.append((char)a.length);
        short runValue = a[0];
        int runLength = 1;
        for (int i = 1; i < a.length; ++i) {
            short s = a[i];
            if (s == runValue && runLength < 65535) {
                ++runLength;
                continue;
            }
            Utility.encodeRun(buffer, runValue, runLength);
            runValue = s;
            runLength = 1;
        }
        Utility.encodeRun(buffer, runValue, runLength);
        return buffer.toString();
    }

    public static final String arrayToRLEString(char[] a) {
        StringBuilder buffer = new StringBuilder();
        buffer.append((char)(a.length >> 16));
        buffer.append((char)a.length);
        char runValue = a[0];
        int runLength = 1;
        for (int i = 1; i < a.length; ++i) {
            char s = a[i];
            if (s == runValue && runLength < 65535) {
                ++runLength;
                continue;
            }
            Utility.encodeRun(buffer, (short)runValue, runLength);
            runValue = s;
            runLength = 1;
        }
        Utility.encodeRun(buffer, (short)runValue, runLength);
        return buffer.toString();
    }

    public static final String arrayToRLEString(byte[] a) {
        StringBuilder buffer = new StringBuilder();
        buffer.append((char)(a.length >> 16));
        buffer.append((char)a.length);
        byte runValue = a[0];
        int runLength = 1;
        byte[] state = new byte[2];
        for (int i = 1; i < a.length; ++i) {
            byte b = a[i];
            if (b == runValue && runLength < 255) {
                ++runLength;
                continue;
            }
            Utility.encodeRun(buffer, runValue, runLength, state);
            runValue = b;
            runLength = 1;
        }
        Utility.encodeRun(buffer, runValue, runLength, state);
        if (state[0] != 0) {
            Utility.appendEncodedByte(buffer, (byte)0, state);
        }
        return buffer.toString();
    }

    private static final <T extends Appendable> void encodeRun(T buffer, int value, int length) {
        if (length < 4) {
            for (int j = 0; j < length; ++j) {
                if (value == 42405) {
                    Utility.appendInt(buffer, value);
                }
                Utility.appendInt(buffer, value);
            }
        } else {
            if (length == 42405) {
                if (value == 42405) {
                    Utility.appendInt(buffer, 42405);
                }
                Utility.appendInt(buffer, value);
                --length;
            }
            Utility.appendInt(buffer, 42405);
            Utility.appendInt(buffer, length);
            Utility.appendInt(buffer, value);
        }
    }

    private static final <T extends Appendable> void appendInt(T buffer, int value) {
        try {
            buffer.append((char)(value >>> 16));
            buffer.append((char)(value & 0xFFFF));
        }
        catch (IOException e) {
            throw new IllegalIcuArgumentException(e);
        }
    }

    private static final <T extends Appendable> void encodeRun(T buffer, short value, int length) {
        try {
            char valueChar = (char)value;
            if (length < 4) {
                for (int j = 0; j < length; ++j) {
                    if (valueChar == '\ua5a5') {
                        buffer.append('\ua5a5');
                    }
                    buffer.append(valueChar);
                }
            } else {
                if (length == 42405) {
                    if (valueChar == '\ua5a5') {
                        buffer.append('\ua5a5');
                    }
                    buffer.append(valueChar);
                    --length;
                }
                buffer.append('\ua5a5');
                buffer.append((char)length);
                buffer.append(valueChar);
            }
        }
        catch (IOException e) {
            throw new IllegalIcuArgumentException(e);
        }
    }

    private static final <T extends Appendable> void encodeRun(T buffer, byte value, int length, byte[] state) {
        if (length < 4) {
            for (int j = 0; j < length; ++j) {
                if (value == -91) {
                    Utility.appendEncodedByte(buffer, (byte)-91, state);
                }
                Utility.appendEncodedByte(buffer, value, state);
            }
        } else {
            if ((byte)length == -91) {
                if (value == -91) {
                    Utility.appendEncodedByte(buffer, (byte)-91, state);
                }
                Utility.appendEncodedByte(buffer, value, state);
                --length;
            }
            Utility.appendEncodedByte(buffer, (byte)-91, state);
            Utility.appendEncodedByte(buffer, (byte)length, state);
            Utility.appendEncodedByte(buffer, value, state);
        }
    }

    private static final <T extends Appendable> void appendEncodedByte(T buffer, byte value, byte[] state) {
        try {
            if (state[0] != 0) {
                char c = (char)(state[1] << 8 | value & 0xFF);
                buffer.append(c);
                state[0] = 0;
            } else {
                state[0] = 1;
                state[1] = value;
            }
        }
        catch (IOException e) {
            throw new IllegalIcuArgumentException(e);
        }
    }

    public static final int[] RLEStringToIntArray(String s) {
        int length = Utility.getInt(s, 0);
        int[] array = new int[length];
        int ai = 0;
        int i = 1;
        int maxI = s.length() / 2;
        while (ai < length && i < maxI) {
            int c;
            if ((c = Utility.getInt(s, i++)) == 42405) {
                if ((c = Utility.getInt(s, i++)) == 42405) {
                    array[ai++] = c;
                    continue;
                }
                int runLength = c;
                int runValue = Utility.getInt(s, i++);
                for (int j = 0; j < runLength; ++j) {
                    array[ai++] = runValue;
                }
                continue;
            }
            array[ai++] = c;
        }
        if (ai != length || i != maxI) {
            throw new IllegalStateException("Bad run-length encoded int array");
        }
        return array;
    }

    static final int getInt(String s, int i) {
        return s.charAt(2 * i) << 16 | s.charAt(2 * i + 1);
    }

    public static final short[] RLEStringToShortArray(String s) {
        int length = s.charAt(0) << 16 | s.charAt(1);
        short[] array = new short[length];
        int ai = 0;
        for (int i = 2; i < s.length(); ++i) {
            int c = s.charAt(i);
            if (c == 42405) {
                if ((c = s.charAt(++i)) == 42405) {
                    array[ai++] = (short)c;
                    continue;
                }
                int runLength = c;
                short runValue = (short)s.charAt(++i);
                for (int j = 0; j < runLength; ++j) {
                    array[ai++] = runValue;
                }
                continue;
            }
            array[ai++] = (short)c;
        }
        if (ai != length) {
            throw new IllegalStateException("Bad run-length encoded short array");
        }
        return array;
    }

    public static final char[] RLEStringToCharArray(String s) {
        int length = s.charAt(0) << 16 | s.charAt(1);
        char[] array = new char[length];
        int ai = 0;
        for (int i = 2; i < s.length(); ++i) {
            int c = s.charAt(i);
            if (c == 42405) {
                if ((c = s.charAt(++i)) == 42405) {
                    array[ai++] = c;
                    continue;
                }
                int runLength = c;
                char runValue = s.charAt(++i);
                for (int j = 0; j < runLength; ++j) {
                    array[ai++] = runValue;
                }
                continue;
            }
            array[ai++] = c;
        }
        if (ai != length) {
            throw new IllegalStateException("Bad run-length encoded short array");
        }
        return array;
    }

    public static final byte[] RLEStringToByteArray(String s) {
        int length = s.charAt(0) << 16 | s.charAt(1);
        byte[] array = new byte[length];
        boolean nextChar = true;
        int c = 0;
        int node = 0;
        int runLength = 0;
        int i = 2;
        int ai = 0;
        while (ai < length) {
            int b;
            if (nextChar) {
                c = s.charAt(i++);
                b = (byte)(c >> 8);
                nextChar = false;
            } else {
                b = c & 0xFF;
                nextChar = true;
            }
            switch (node) {
                case 0: {
                    if (b == -91) {
                        node = 1;
                        break;
                    }
                    array[ai++] = b;
                    break;
                }
                case 1: {
                    if (b == -91) {
                        array[ai++] = -91;
                        node = 0;
                        break;
                    }
                    runLength = b;
                    if (runLength < 0) {
                        runLength += 256;
                    }
                    node = 2;
                    break;
                }
                case 2: {
                    for (int j = 0; j < runLength; ++j) {
                        array[ai++] = b;
                    }
                    node = 0;
                }
            }
        }
        if (node != 0) {
            throw new IllegalStateException("Bad run-length encoded byte array");
        }
        if (i != s.length()) {
            throw new IllegalStateException("Excess data in RLE byte array string");
        }
        return array;
    }

    public static final String formatForSource(String s) {
        StringBuilder buffer = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            if (i > 0) {
                buffer.append('+').append(LINE_SEPARATOR);
            }
            buffer.append("        \"");
            int count = 11;
            while (i < s.length() && count < 80) {
                char c;
                if ((c = s.charAt(i++)) < ' ' || c == '\"' || c == '\\') {
                    if (c == '\n') {
                        buffer.append("\\n");
                        count += 2;
                        continue;
                    }
                    if (c == '\t') {
                        buffer.append("\\t");
                        count += 2;
                        continue;
                    }
                    if (c == '\r') {
                        buffer.append("\\r");
                        count += 2;
                        continue;
                    }
                    buffer.append('\\');
                    buffer.append(HEX_DIGIT[(c & 0x1C0) >> 6]);
                    buffer.append(HEX_DIGIT[(c & 0x38) >> 3]);
                    buffer.append(HEX_DIGIT[c & 7]);
                    count += 4;
                    continue;
                }
                if (c <= '~') {
                    buffer.append(c);
                    ++count;
                    continue;
                }
                buffer.append("\\u");
                buffer.append(HEX_DIGIT[(c & 0xF000) >> 12]);
                buffer.append(HEX_DIGIT[(c & 0xF00) >> 8]);
                buffer.append(HEX_DIGIT[(c & 0xF0) >> 4]);
                buffer.append(HEX_DIGIT[c & 0xF]);
                count += 6;
            }
            buffer.append('\"');
        }
        return buffer.toString();
    }

    public static final String format1ForSource(String s) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\"");
        int i = 0;
        while (i < s.length()) {
            char c;
            if ((c = s.charAt(i++)) < ' ' || c == '\"' || c == '\\') {
                if (c == '\n') {
                    buffer.append("\\n");
                    continue;
                }
                if (c == '\t') {
                    buffer.append("\\t");
                    continue;
                }
                if (c == '\r') {
                    buffer.append("\\r");
                    continue;
                }
                buffer.append('\\');
                buffer.append(HEX_DIGIT[(c & 0x1C0) >> 6]);
                buffer.append(HEX_DIGIT[(c & 0x38) >> 3]);
                buffer.append(HEX_DIGIT[c & 7]);
                continue;
            }
            if (c <= '~') {
                buffer.append(c);
                continue;
            }
            buffer.append("\\u");
            buffer.append(HEX_DIGIT[(c & 0xF000) >> 12]);
            buffer.append(HEX_DIGIT[(c & 0xF00) >> 8]);
            buffer.append(HEX_DIGIT[(c & 0xF0) >> 4]);
            buffer.append(HEX_DIGIT[c & 0xF]);
        }
        buffer.append('\"');
        return buffer.toString();
    }

    public static final String escape(String s) {
        int c;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < s.length(); i += UTF16.getCharCount(c)) {
            c = Character.codePointAt(s, i);
            if (c >= 32 && c <= 127) {
                if (c == 92) {
                    buf.append("\\\\");
                    continue;
                }
                buf.append((char)c);
                continue;
            }
            boolean four = c <= 65535;
            buf.append(four ? "\\u" : "\\U");
            buf.append(Utility.hex(c, four ? 4 : 8));
        }
        return buf.toString();
    }

    private static final int _digit8(int c) {
        if (c >= 48 && c <= 55) {
            return c - 48;
        }
        return -1;
    }

    private static final int _digit16(int c) {
        if (c >= 48 && c <= 57) {
            return c - 48;
        }
        if (c >= 65 && c <= 70) {
            return c - 55;
        }
        if (c >= 97 && c <= 102) {
            return c - 87;
        }
        return -1;
    }

    public static int unescapeAndLengthAt(CharSequence s, int offset) {
        return Utility.unescapeAndLengthAt(s, offset, s.length());
    }

    private static int unescapeAndLengthAt(CharSequence s, int offset, int length) {
        char c2;
        int dig;
        int result = 0;
        int n = 0;
        int minDig = 0;
        int maxDig = 0;
        int bitsPerDigit = 4;
        boolean braces = false;
        if (offset < 0 || offset >= length) {
            return -1;
        }
        int start = offset;
        int c = s.charAt(offset++);
        switch (c) {
            case 117: {
                maxDig = 4;
                minDig = 4;
                break;
            }
            case 85: {
                maxDig = 8;
                minDig = 8;
                break;
            }
            case 120: {
                minDig = 1;
                if (offset < length && s.charAt(offset) == '{') {
                    ++offset;
                    braces = true;
                    maxDig = 8;
                    break;
                }
                maxDig = 2;
                break;
            }
            default: {
                dig = Utility._digit8(c);
                if (dig < 0) break;
                minDig = 1;
                maxDig = 3;
                n = 1;
                bitsPerDigit = 3;
                result = dig;
            }
        }
        if (minDig != 0) {
            while (offset < length && n < maxDig) {
                c = s.charAt(offset);
                int n2 = dig = bitsPerDigit == 3 ? Utility._digit8(c) : Utility._digit16(c);
                if (dig < 0) break;
                result = result << bitsPerDigit | dig;
                ++offset;
                ++n;
            }
            if (n < minDig) {
                return -1;
            }
            if (braces) {
                if (c != 125) {
                    return -1;
                }
                ++offset;
            }
            if (result < 0 || result >= 0x110000) {
                return -1;
            }
            if (offset < length && UTF16.isLeadSurrogate(result)) {
                int ahead = offset + 1;
                c = s.charAt(offset);
                if (c == 92 && ahead < length) {
                    int cpAndLength;
                    int tailLimit = ahead + 11;
                    if (tailLimit > length) {
                        tailLimit = length;
                    }
                    if ((cpAndLength = Utility.unescapeAndLengthAt(s, ahead, tailLimit)) >= 0) {
                        c = cpAndLength >> 8;
                        ahead += cpAndLength & 0xFF;
                    }
                }
                if (UTF16.isTrailSurrogate(c)) {
                    offset = ahead;
                    result = UCharacter.toCodePoint(result, c);
                }
            }
            return Utility.codePointAndLength(result, start, offset);
        }
        for (int i = 0; i < UNESCAPE_MAP.length; i += 2) {
            if (c == UNESCAPE_MAP[i]) {
                return Utility.codePointAndLength(UNESCAPE_MAP[i + 1], start, offset);
            }
            if (c < UNESCAPE_MAP[i]) break;
        }
        if (c == 99 && offset < length) {
            c = Character.codePointAt(s, offset);
            return Utility.codePointAndLength(c & 0x1F, start, offset + Character.charCount(c));
        }
        if (UTF16.isLeadSurrogate(c) && offset < length && UTF16.isTrailSurrogate(c2 = s.charAt(offset))) {
            ++offset;
            c = UCharacter.toCodePoint(c, c2);
        }
        return Utility.codePointAndLength(c, start, offset);
    }

    private static int codePointAndLength(int c, int length) {
        assert (0 <= c && c <= 0x10FFFF);
        assert (0 <= length && length <= 255);
        return c << 8 | length;
    }

    private static int codePointAndLength(int c, int start, int limit) {
        return Utility.codePointAndLength(c, limit - start);
    }

    public static int cpFromCodePointAndLength(int cpAndLength) {
        assert (cpAndLength >= 0);
        return cpAndLength >> 8;
    }

    public static int lengthFromCodePointAndLength(int cpAndLength) {
        assert (cpAndLength >= 0);
        return cpAndLength & 0xFF;
    }

    public static String unescape(CharSequence s) {
        StringBuilder buf = null;
        int i = 0;
        while (i < s.length()) {
            char c;
            if ((c = s.charAt(i++)) == '\\') {
                int cpAndLength;
                if (buf == null) {
                    buf = new StringBuilder(s.length()).append(s, 0, i - 1);
                }
                if ((cpAndLength = Utility.unescapeAndLengthAt(s, i)) < 0) {
                    throw new IllegalArgumentException("Invalid escape sequence " + s.subSequence(i - 1, Math.min(i + 9, s.length())));
                }
                buf.appendCodePoint(cpAndLength >> 8);
                i += cpAndLength & 0xFF;
                continue;
            }
            if (buf == null) continue;
            buf.append(c);
        }
        if (buf == null) {
            return s.toString();
        }
        return buf.toString();
    }

    public static String unescapeLeniently(CharSequence s) {
        StringBuilder buf = null;
        int i = 0;
        while (i < s.length()) {
            char c;
            if ((c = s.charAt(i++)) == '\\') {
                int cpAndLength;
                if (buf == null) {
                    buf = new StringBuilder(s.length()).append(s, 0, i - 1);
                }
                if ((cpAndLength = Utility.unescapeAndLengthAt(s, i)) < 0) {
                    buf.append(c);
                    continue;
                }
                buf.appendCodePoint(cpAndLength >> 8);
                i += cpAndLength & 0xFF;
                continue;
            }
            if (buf == null) continue;
            buf.append(c);
        }
        if (buf == null) {
            return s.toString();
        }
        return buf.toString();
    }

    public static String hex(long ch) {
        return Utility.hex(ch, 4);
    }

    public static String hex(long i, int places) {
        String result;
        boolean negative;
        if (i == Long.MIN_VALUE) {
            return "-8000000000000000";
        }
        boolean bl = negative = i < 0L;
        if (negative) {
            i = -i;
        }
        if ((result = Long.toString(i, 16).toUpperCase(Locale.ENGLISH)).length() < places) {
            result = "0000000000000000".substring(result.length(), places) + result;
        }
        if (negative) {
            return '-' + result;
        }
        return result;
    }

    public static String hex(CharSequence s) {
        return Utility.hex(s, 4, ",", true, new StringBuilder()).toString();
    }

    public static <S extends CharSequence, U extends CharSequence, T extends Appendable> T hex(S s, int width, U separator, boolean useCodePoints, T result) {
        try {
            if (useCodePoints) {
                int cp;
                for (int i = 0; i < s.length(); i += UTF16.getCharCount(cp)) {
                    cp = Character.codePointAt(s, i);
                    if (i != 0) {
                        result.append(separator);
                    }
                    result.append(Utility.hex(cp, width));
                }
            } else {
                for (int i = 0; i < s.length(); ++i) {
                    if (i != 0) {
                        result.append(separator);
                    }
                    result.append(Utility.hex(s.charAt(i), width));
                }
            }
            return result;
        }
        catch (IOException e) {
            throw new IllegalIcuArgumentException(e);
        }
    }

    public static String hex(byte[] o, int start, int end, String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = start; i < end; ++i) {
            if (i != 0) {
                result.append(separator);
            }
            result.append(Utility.hex(o[i]));
        }
        return result.toString();
    }

    public static <S extends CharSequence> String hex(S s, int width, S separator) {
        return Utility.hex(s, width, separator, true, new StringBuilder()).toString();
    }

    public static void split(String s, char divider, String[] output) {
        int i;
        int last = 0;
        int current = 0;
        for (i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != divider) continue;
            output[current++] = s.substring(last, i);
            last = i + 1;
        }
        output[current++] = s.substring(last, i);
        while (current < output.length) {
            output[current++] = "";
        }
    }

    public static String[] split(String s, char divider) {
        int i;
        int last = 0;
        ArrayList<String> output = new ArrayList<String>();
        for (i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != divider) continue;
            output.add(s.substring(last, i));
            last = i + 1;
        }
        output.add(s.substring(last, i));
        return output.toArray(new String[output.size()]);
    }

    public static int lookup(String source, String[] target) {
        for (int i = 0; i < target.length; ++i) {
            if (!source.equals(target[i])) continue;
            return i;
        }
        return -1;
    }

    public static boolean parseChar(String id, int[] pos, char ch) {
        int start = pos[0];
        pos[0] = PatternProps.skipWhiteSpace(id, pos[0]);
        if (pos[0] == id.length() || id.charAt(pos[0]) != ch) {
            pos[0] = start;
            return false;
        }
        pos[0] = pos[0] + 1;
        return true;
    }

    public static int parsePattern(String rule, int pos, int limit, String pattern, int[] parsedInts) {
        int[] p = new int[1];
        int intCount = 0;
        block5: for (int i = 0; i < pattern.length(); ++i) {
            char cpat = pattern.charAt(i);
            switch (cpat) {
                case ' ': {
                    char c;
                    if (pos >= limit) {
                        return -1;
                    }
                    if (!PatternProps.isWhiteSpace(c = rule.charAt(pos++))) {
                        return -1;
                    }
                }
                case '~': {
                    pos = PatternProps.skipWhiteSpace(rule, pos);
                    continue block5;
                }
                case '#': {
                    p[0] = pos;
                    parsedInts[intCount++] = Utility.parseInteger(rule, p, limit);
                    if (p[0] == pos) {
                        return -1;
                    }
                    pos = p[0];
                    continue block5;
                }
                default: {
                    char c;
                    if (pos >= limit) {
                        return -1;
                    }
                    if ((c = (char)UCharacter.toLowerCase(rule.charAt(pos++))) == cpat) continue block5;
                    return -1;
                }
            }
        }
        return pos;
    }

    public static int parsePattern(String pat, Replaceable text, int index, int limit) {
        int ipat = 0;
        if (ipat == pat.length()) {
            return index;
        }
        int cpat = Character.codePointAt(pat, ipat);
        while (index < limit) {
            int c = text.char32At(index);
            if (cpat == 126) {
                if (PatternProps.isWhiteSpace(c)) {
                    index += UTF16.getCharCount(c);
                    continue;
                }
                if (++ipat == pat.length()) {
                    return index;
                }
            } else if (c == cpat) {
                int n = UTF16.getCharCount(c);
                index += n;
                if ((ipat += n) == pat.length()) {
                    return index;
                }
            } else {
                return -1;
            }
            cpat = UTF16.charAt(pat, ipat);
        }
        return -1;
    }

    public static int parseInteger(String rule, int[] pos, int limit) {
        int count = 0;
        int value = 0;
        int p = pos[0];
        int radix = 10;
        if (rule.regionMatches(true, p, "0x", 0, 2)) {
            p += 2;
            radix = 16;
        } else if (p < limit && rule.charAt(p) == '0') {
            ++p;
            count = 1;
            radix = 8;
        }
        while (p < limit) {
            int d;
            if ((d = UCharacter.digit(rule.charAt(p++), radix)) < 0) {
                --p;
                break;
            }
            ++count;
            int v = value * radix + d;
            if (v <= value) {
                return 0;
            }
            value = v;
        }
        if (count > 0) {
            pos[0] = p;
        }
        return value;
    }

    public static String parseUnicodeIdentifier(String str, int[] pos) {
        int p;
        int ch;
        StringBuilder buf = new StringBuilder();
        for (p = pos[0]; p < str.length(); p += UTF16.getCharCount(ch)) {
            ch = Character.codePointAt(str, p);
            if (buf.length() == 0) {
                if (UCharacter.isUnicodeIdentifierStart(ch)) {
                    buf.appendCodePoint(ch);
                    continue;
                }
                return null;
            }
            if (!UCharacter.isUnicodeIdentifierPart(ch)) break;
            buf.appendCodePoint(ch);
        }
        pos[0] = p;
        return buf.toString();
    }

    private static <T extends Appendable> void recursiveAppendNumber(T result, int n, int radix, int minDigits) {
        try {
            int digit = n % radix;
            if (n >= radix || minDigits > 1) {
                Utility.recursiveAppendNumber(result, n / radix, radix, minDigits - 1);
            }
            result.append(DIGITS[digit]);
        }
        catch (IOException e) {
            throw new IllegalIcuArgumentException(e);
        }
    }

    public static <T extends Appendable> T appendNumber(T result, int n, int radix, int minDigits) {
        try {
            if (radix < 2 || radix > 36) {
                throw new IllegalArgumentException("Illegal radix " + radix);
            }
            int abs = n;
            if (n < 0) {
                abs = -n;
                result.append("-");
            }
            Utility.recursiveAppendNumber(result, abs, radix, minDigits);
            return result;
        }
        catch (IOException e) {
            throw new IllegalIcuArgumentException(e);
        }
    }

    public static int parseNumber(String text, int[] pos, int radix) {
        int ch;
        int d;
        int p;
        int n = 0;
        for (p = pos[0]; p < text.length() && (d = UCharacter.digit(ch = Character.codePointAt(text, p), radix)) >= 0; ++p) {
            if ((n = radix * n + d) >= 0) continue;
            return -1;
        }
        if (p == pos[0]) {
            return -1;
        }
        pos[0] = p;
        return n;
    }

    public static boolean isUnprintable(int c) {
        return c < 32 || c > 126;
    }

    public static boolean shouldAlwaysBeEscaped(int c) {
        if (c < 32) {
            return true;
        }
        if (c <= 126) {
            return false;
        }
        if (c <= 159) {
            return true;
        }
        if (c < 55296) {
            return false;
        }
        if (c <= 57343 || 64976 <= c && c <= 65007 || (c & 0xFFFE) == 65534) {
            return true;
        }
        return c > 0x10FFFF;
    }

    public static <T extends Appendable> boolean escapeUnprintable(T result, int c) {
        if (Utility.isUnprintable(c)) {
            Utility.escape(result, c);
            return true;
        }
        return false;
    }

    public static <T extends Appendable> T escape(T result, int c) {
        try {
            result.append('\\');
            if ((c & 0xFFFF0000) != 0) {
                result.append('U');
                result.append(DIGITS[0xF & c >> 28]);
                result.append(DIGITS[0xF & c >> 24]);
                result.append(DIGITS[0xF & c >> 20]);
                result.append(DIGITS[0xF & c >> 16]);
            } else {
                result.append('u');
            }
            result.append(DIGITS[0xF & c >> 12]);
            result.append(DIGITS[0xF & c >> 8]);
            result.append(DIGITS[0xF & c >> 4]);
            result.append(DIGITS[0xF & c]);
            return result;
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException(e);
        }
    }

    public static int quotedIndexOf(String text, int start, int limit, String setOfChars) {
        for (int i = start; i < limit; ++i) {
            char c = text.charAt(i);
            if (c == '\\') {
                ++i;
                continue;
            }
            if (c == '\'') {
                while (++i < limit && text.charAt(i) != '\'') {
                }
                continue;
            }
            if (setOfChars.indexOf(c) < 0) continue;
            return i;
        }
        return -1;
    }

    public static void appendToRule(StringBuffer rule, int c, boolean isLiteral, boolean escapeUnprintable, StringBuffer quoteBuf) {
        if (isLiteral || escapeUnprintable && Utility.isUnprintable(c)) {
            if (quoteBuf.length() > 0) {
                while (quoteBuf.length() >= 2 && quoteBuf.charAt(0) == '\'' && quoteBuf.charAt(1) == '\'') {
                    rule.append('\\').append('\'');
                    quoteBuf.delete(0, 2);
                }
                int trailingCount = 0;
                while (quoteBuf.length() >= 2 && quoteBuf.charAt(quoteBuf.length() - 2) == '\'' && quoteBuf.charAt(quoteBuf.length() - 1) == '\'') {
                    quoteBuf.setLength(quoteBuf.length() - 2);
                    ++trailingCount;
                }
                if (quoteBuf.length() > 0) {
                    rule.append('\'');
                    rule.append(quoteBuf);
                    rule.append('\'');
                    quoteBuf.setLength(0);
                }
                while (trailingCount-- > 0) {
                    rule.append('\\').append('\'');
                }
            }
            if (c != -1) {
                if (c == 32) {
                    int len = rule.length();
                    if (len > 0 && rule.charAt(len - 1) != ' ') {
                        rule.append(' ');
                    }
                } else if (!escapeUnprintable || !Utility.escapeUnprintable(rule, c)) {
                    rule.appendCodePoint(c);
                }
            }
        } else if (quoteBuf.length() == 0 && (c == 39 || c == 92)) {
            rule.append('\\').append((char)c);
        } else if (!(quoteBuf.length() <= 0 && (c < 33 || c > 126 || c >= 48 && c <= 57 || c >= 65 && c <= 90 || c >= 97 && c <= 122) && !PatternProps.isWhiteSpace(c))) {
            quoteBuf.appendCodePoint(c);
            if (c == 39) {
                quoteBuf.append((char)c);
            }
        } else {
            rule.appendCodePoint(c);
        }
    }

    public static void appendToRule(StringBuffer rule, String text, boolean isLiteral, boolean escapeUnprintable, StringBuffer quoteBuf) {
        for (int i = 0; i < text.length(); ++i) {
            Utility.appendToRule(rule, text.charAt(i), isLiteral, escapeUnprintable, quoteBuf);
        }
    }

    public static void appendToRule(StringBuffer rule, UnicodeMatcher matcher, boolean escapeUnprintable, StringBuffer quoteBuf) {
        if (matcher != null) {
            Utility.appendToRule(rule, matcher.toPattern(escapeUnprintable), true, escapeUnprintable, quoteBuf);
        }
    }

    public static final int compareUnsigned(int source, int target) {
        if ((source -= Integer.MIN_VALUE) < (target -= Integer.MIN_VALUE)) {
            return -1;
        }
        if (source > target) {
            return 1;
        }
        return 0;
    }

    public static final byte highBit(int n) {
        if (n <= 0) {
            return -1;
        }
        byte bit = 0;
        if (n >= 65536) {
            n >>= 16;
            bit = (byte)(bit + 16);
        }
        if (n >= 256) {
            n >>= 8;
            bit = (byte)(bit + 8);
        }
        if (n >= 16) {
            n >>= 4;
            bit = (byte)(bit + 4);
        }
        if (n >= 4) {
            n >>= 2;
            bit = (byte)(bit + 2);
        }
        if (n >= 2) {
            n >>= 1;
            bit = (byte)(bit + 1);
        }
        return bit;
    }

    public static String valueOf(int[] source) {
        StringBuilder result = new StringBuilder(source.length);
        for (int i = 0; i < source.length; ++i) {
            result.appendCodePoint(source[i]);
        }
        return result.toString();
    }

    public static String repeat(String s, int count) {
        if (count <= 0) {
            return "";
        }
        if (count == 1) {
            return s;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            result.append(s);
        }
        return result.toString();
    }

    public static String[] splitString(String src, String target) {
        return src.split("\\Q" + target + "\\E");
    }

    public static String[] splitWhitespace(String src) {
        return src.split("\\s+");
    }

    public static String fromHex(String string, int minLength, String separator) {
        return Utility.fromHex(string, minLength, Pattern.compile(separator != null ? separator : "\\s+"));
    }

    public static String fromHex(String string, int minLength, Pattern separator) {
        String[] parts;
        StringBuilder buffer = new StringBuilder();
        for (String part : parts = separator.split(string)) {
            if (part.length() < minLength) {
                throw new IllegalArgumentException("code point too short: " + part);
            }
            int cp = Integer.parseInt(part, 16);
            buffer.appendCodePoint(cp);
        }
        return buffer.toString();
    }

    public static int addExact(int x, int y) {
        int r = x + y;
        if (((x ^ r) & (y ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }

    public static boolean charSequenceEquals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.length() != b.length()) {
            return false;
        }
        for (int i = 0; i < a.length(); ++i) {
            if (a.charAt(i) == b.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public static int charSequenceHashCode(CharSequence value) {
        int hash = 0;
        for (int i = 0; i < value.length(); ++i) {
            hash = hash * 31 + value.charAt(i);
        }
        return hash;
    }

    public static <A extends Appendable> A appendTo(CharSequence string, A appendable) {
        try {
            appendable.append(string);
            return appendable;
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException(e);
        }
    }

    public static String joinStrings(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        if (delimiter == null || elements == null) {
            throw new NullPointerException("Delimiter or elements is null");
        }
        StringBuilder buf = new StringBuilder();
        Iterator<? extends CharSequence> itr = elements.iterator();
        boolean isFirstElem = true;
        while (itr.hasNext()) {
            CharSequence element = itr.next();
            if (element == null) continue;
            if (!isFirstElem) {
                buf.append(delimiter);
            } else {
                isFirstElem = false;
            }
            buf.append(element);
        }
        return buf.toString();
    }
}

