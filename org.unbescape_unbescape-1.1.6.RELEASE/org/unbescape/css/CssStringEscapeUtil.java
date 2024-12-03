/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.css;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import org.unbescape.css.CssStringEscapeLevel;
import org.unbescape.css.CssStringEscapeType;

final class CssStringEscapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static char[] HEXA_CHARS_UPPER;
    private static int BACKSLASH_CHARS_LEN;
    private static char BACKSLASH_CHARS_NO_ESCAPE;
    private static char[] BACKSLASH_CHARS;
    private static final char ESCAPE_LEVELS_LEN = '\u00a1';
    private static final byte[] ESCAPE_LEVELS;

    private CssStringEscapeUtil() {
    }

    static char[] toCompactHexa(int codepoint, char next, int level) {
        int i;
        int div;
        boolean needTrailingSpace;
        boolean bl = needTrailingSpace = level < 4 && (next >= '0' && next <= '9' || next >= 'A' && next <= 'F' || next >= 'a' && next <= 'f') || level < 3 && next == ' ';
        if (codepoint == 0) {
            char[] cArray;
            if (needTrailingSpace) {
                char[] cArray2 = new char[2];
                cArray2[0] = 48;
                cArray = cArray2;
                cArray2[1] = 32;
            } else {
                char[] cArray3 = new char[1];
                cArray = cArray3;
                cArray3[0] = 48;
            }
            return cArray;
        }
        char[] result = null;
        for (div = 20; result == null && div >= 0; div -= 4) {
            if ((codepoint >>> div) % 16 <= 0) continue;
            result = new char[div / 4 + (needTrailingSpace ? 2 : 1)];
        }
        div = 0;
        int n = i = needTrailingSpace ? result.length - 2 : result.length - 1;
        while (i >= 0) {
            result[i] = HEXA_CHARS_UPPER[(codepoint >>> div) % 16];
            div += 4;
            --i;
        }
        if (needTrailingSpace) {
            result[result.length - 1] = 32;
        }
        return result;
    }

    static char[] toSixDigitHexa(int codepoint, char next, int level) {
        boolean needTrailingSpace = level < 3 && next == ' ';
        char[] result = new char[6 + (needTrailingSpace ? 1 : 0)];
        if (needTrailingSpace) {
            result[6] = 32;
        }
        result[5] = HEXA_CHARS_UPPER[codepoint % 16];
        result[4] = HEXA_CHARS_UPPER[(codepoint >>> 4) % 16];
        result[3] = HEXA_CHARS_UPPER[(codepoint >>> 8) % 16];
        result[2] = HEXA_CHARS_UPPER[(codepoint >>> 12) % 16];
        result[1] = HEXA_CHARS_UPPER[(codepoint >>> 16) % 16];
        result[0] = HEXA_CHARS_UPPER[(codepoint >>> 20) % 16];
        return result;
    }

    static String escape(String text, CssStringEscapeType escapeType, CssStringEscapeLevel escapeLevel) {
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useBackslashEscapes = escapeType.getUseBackslashEscapes();
        boolean useCompactHexa = escapeType.getUseCompactHexa();
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        for (int i = 0; i < max; ++i) {
            char next;
            char sec;
            int codepoint = Character.codePointAt(text, i);
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint]) continue;
            if (codepoint > 159 && level < ESCAPE_LEVELS[160]) {
                if (Character.charCount(codepoint) <= 1) continue;
                ++i;
                continue;
            }
            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 20);
            }
            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }
            if (Character.charCount(codepoint) > 1) {
                ++i;
            }
            readOffset = i + 1;
            if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN && (sec = BACKSLASH_CHARS[codepoint]) != BACKSLASH_CHARS_NO_ESCAPE) {
                strBuilder.append('\\');
                strBuilder.append(sec);
                continue;
            }
            char c = next = i + 1 < max ? text.charAt(i + 1) : (char)'\u0000';
            if (useCompactHexa) {
                strBuilder.append('\\');
                strBuilder.append(CssStringEscapeUtil.toCompactHexa(codepoint, next, level));
                continue;
            }
            strBuilder.append('\\');
            strBuilder.append(CssStringEscapeUtil.toSixDigitHexa(codepoint, next, level));
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void escape(Reader reader, Writer writer, CssStringEscapeType escapeType, CssStringEscapeLevel escapeLevel) throws IOException {
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useBackslashEscapes = escapeType.getUseBackslashEscapes();
        boolean useCompactHexa = escapeType.getUseCompactHexa();
        int c2 = reader.read();
        while (c2 >= 0) {
            char next;
            char sec;
            int c1 = c2;
            int codepoint = CssStringEscapeUtil.codePointAt((char)c1, (char)(c2 = reader.read()));
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint]) {
                writer.write(c1);
                continue;
            }
            if (codepoint > 159 && level < ESCAPE_LEVELS[160]) {
                writer.write(c1);
                if (Character.charCount(codepoint) <= 1) continue;
                writer.write(c2);
                c1 = c2;
                c2 = reader.read();
                continue;
            }
            if (Character.charCount(codepoint) > 1) {
                c1 = c2;
                c2 = reader.read();
            }
            if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN && (sec = BACKSLASH_CHARS[codepoint]) != BACKSLASH_CHARS_NO_ESCAPE) {
                writer.write(92);
                writer.write(sec);
                continue;
            }
            char c = next = c2 >= 0 ? (char)c2 : (char)'\u0000';
            if (useCompactHexa) {
                writer.write(92);
                writer.write(CssStringEscapeUtil.toCompactHexa(codepoint, next, level));
                continue;
            }
            writer.write(92);
            writer.write(CssStringEscapeUtil.toSixDigitHexa(codepoint, next, level));
        }
    }

    static void escape(char[] text, int offset, int len, Writer writer, CssStringEscapeType escapeType, CssStringEscapeLevel escapeLevel) throws IOException {
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useBackslashEscapes = escapeType.getUseBackslashEscapes();
        boolean useCompactHexa = escapeType.getUseCompactHexa();
        int max = offset + len;
        int readOffset = offset;
        for (int i = offset; i < max; ++i) {
            char next;
            char escape;
            int codepoint = Character.codePointAt(text, i);
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint]) continue;
            if (codepoint > 159 && level < ESCAPE_LEVELS[160]) {
                if (Character.charCount(codepoint) <= 1) continue;
                ++i;
                continue;
            }
            if (i - readOffset > 0) {
                writer.write(text, readOffset, i - readOffset);
            }
            if (Character.charCount(codepoint) > 1) {
                ++i;
            }
            readOffset = i + 1;
            if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN && (escape = BACKSLASH_CHARS[codepoint]) != BACKSLASH_CHARS_NO_ESCAPE) {
                writer.write(92);
                writer.write(escape);
                continue;
            }
            char c = next = i + 1 < max ? text[i + 1] : (char)'\u0000';
            if (useCompactHexa) {
                writer.write(92);
                writer.write(CssStringEscapeUtil.toCompactHexa(codepoint, next, level));
                continue;
            }
            writer.write(92);
            writer.write(CssStringEscapeUtil.toSixDigitHexa(codepoint, next, level));
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }

    private static int codePointAt(char c1, char c2) {
        if (Character.isHighSurrogate(c1) && c2 >= '\u0000' && Character.isLowSurrogate(c2)) {
            return Character.toCodePoint(c1, c2);
        }
        return c1;
    }

    static {
        int c;
        HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
        BACKSLASH_CHARS_LEN = 127;
        BACKSLASH_CHARS_NO_ESCAPE = '\u0000';
        BACKSLASH_CHARS = new char[BACKSLASH_CHARS_LEN];
        Arrays.fill(BACKSLASH_CHARS, BACKSLASH_CHARS_NO_ESCAPE);
        CssStringEscapeUtil.BACKSLASH_CHARS[32] = 32;
        CssStringEscapeUtil.BACKSLASH_CHARS[33] = 33;
        CssStringEscapeUtil.BACKSLASH_CHARS[34] = 34;
        CssStringEscapeUtil.BACKSLASH_CHARS[35] = 35;
        CssStringEscapeUtil.BACKSLASH_CHARS[36] = 36;
        CssStringEscapeUtil.BACKSLASH_CHARS[37] = 37;
        CssStringEscapeUtil.BACKSLASH_CHARS[38] = 38;
        CssStringEscapeUtil.BACKSLASH_CHARS[39] = 39;
        CssStringEscapeUtil.BACKSLASH_CHARS[40] = 40;
        CssStringEscapeUtil.BACKSLASH_CHARS[41] = 41;
        CssStringEscapeUtil.BACKSLASH_CHARS[42] = 42;
        CssStringEscapeUtil.BACKSLASH_CHARS[43] = 43;
        CssStringEscapeUtil.BACKSLASH_CHARS[44] = 44;
        CssStringEscapeUtil.BACKSLASH_CHARS[45] = 45;
        CssStringEscapeUtil.BACKSLASH_CHARS[46] = 46;
        CssStringEscapeUtil.BACKSLASH_CHARS[47] = 47;
        CssStringEscapeUtil.BACKSLASH_CHARS[59] = 59;
        CssStringEscapeUtil.BACKSLASH_CHARS[60] = 60;
        CssStringEscapeUtil.BACKSLASH_CHARS[61] = 61;
        CssStringEscapeUtil.BACKSLASH_CHARS[62] = 62;
        CssStringEscapeUtil.BACKSLASH_CHARS[63] = 63;
        CssStringEscapeUtil.BACKSLASH_CHARS[64] = 64;
        CssStringEscapeUtil.BACKSLASH_CHARS[91] = 91;
        CssStringEscapeUtil.BACKSLASH_CHARS[92] = 92;
        CssStringEscapeUtil.BACKSLASH_CHARS[93] = 93;
        CssStringEscapeUtil.BACKSLASH_CHARS[94] = 94;
        CssStringEscapeUtil.BACKSLASH_CHARS[95] = 95;
        CssStringEscapeUtil.BACKSLASH_CHARS[96] = 96;
        CssStringEscapeUtil.BACKSLASH_CHARS[123] = 123;
        CssStringEscapeUtil.BACKSLASH_CHARS[124] = 124;
        CssStringEscapeUtil.BACKSLASH_CHARS[125] = 125;
        CssStringEscapeUtil.BACKSLASH_CHARS[126] = 126;
        ESCAPE_LEVELS = new byte[161];
        Arrays.fill(ESCAPE_LEVELS, (byte)3);
        for (c = 128; c < 161; c = (int)((char)(c + 1))) {
            CssStringEscapeUtil.ESCAPE_LEVELS[c] = 2;
        }
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            CssStringEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            CssStringEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            CssStringEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        CssStringEscapeUtil.ESCAPE_LEVELS[34] = 1;
        CssStringEscapeUtil.ESCAPE_LEVELS[39] = 1;
        CssStringEscapeUtil.ESCAPE_LEVELS[92] = 1;
        CssStringEscapeUtil.ESCAPE_LEVELS[47] = 1;
        CssStringEscapeUtil.ESCAPE_LEVELS[38] = 1;
        CssStringEscapeUtil.ESCAPE_LEVELS[59] = 1;
        for (c = 0; c <= 31; c = (int)((char)(c + 1))) {
            CssStringEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
        for (c = 127; c <= 159; c = (int)((char)(c + 1))) {
            CssStringEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
    }
}

