/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.css;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import org.unbescape.css.CssIdentifierEscapeLevel;
import org.unbescape.css.CssIdentifierEscapeType;

final class CssIdentifierEscapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static char[] HEXA_CHARS_UPPER;
    private static int BACKSLASH_CHARS_LEN;
    private static char BACKSLASH_CHARS_NO_ESCAPE;
    private static char[] BACKSLASH_CHARS;
    private static final char ESCAPE_LEVELS_LEN = '\u00a1';
    private static final byte[] ESCAPE_LEVELS;

    private CssIdentifierEscapeUtil() {
    }

    static char[] toCompactHexa(int codepoint, char next, int level) {
        int i;
        int div;
        boolean needTrailingSpace;
        boolean bl = needTrailingSpace = level < 4 && (next >= '0' && next <= '9' || next >= 'A' && next <= 'F' || next >= 'a' && next <= 'f');
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
        boolean needTrailingSpace = false;
        char[] result = new char[6];
        result[5] = HEXA_CHARS_UPPER[codepoint % 16];
        result[4] = HEXA_CHARS_UPPER[(codepoint >>> 4) % 16];
        result[3] = HEXA_CHARS_UPPER[(codepoint >>> 8) % 16];
        result[2] = HEXA_CHARS_UPPER[(codepoint >>> 12) % 16];
        result[1] = HEXA_CHARS_UPPER[(codepoint >>> 16) % 16];
        result[0] = HEXA_CHARS_UPPER[(codepoint >>> 20) % 16];
        return result;
    }

    static String escape(String text, CssIdentifierEscapeType escapeType, CssIdentifierEscapeLevel escapeLevel) {
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
            char escape;
            char c1;
            int codepoint = Character.codePointAt(text, i);
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint] && (i > 0 || codepoint < 48 || codepoint > 57) || codepoint == 45 && level < 3 && (i > 0 || i + 1 >= max || (c1 = text.charAt(i + 1)) != '-' && (c1 < '0' || c1 > '9')) || codepoint == 95 && level < 3 && i > 0) continue;
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
            if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN && (escape = BACKSLASH_CHARS[codepoint]) != BACKSLASH_CHARS_NO_ESCAPE) {
                strBuilder.append('\\');
                strBuilder.append(escape);
                continue;
            }
            char c = next = i + 1 < max ? text.charAt(i + 1) : (char)'\u0000';
            if (useCompactHexa) {
                strBuilder.append('\\');
                strBuilder.append(CssIdentifierEscapeUtil.toCompactHexa(codepoint, next, level));
                continue;
            }
            strBuilder.append('\\');
            strBuilder.append(CssIdentifierEscapeUtil.toSixDigitHexa(codepoint, next, level));
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void escape(Reader reader, Writer writer, CssIdentifierEscapeType escapeType, CssIdentifierEscapeLevel escapeLevel) throws IOException {
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useBackslashEscapes = escapeType.getUseBackslashEscapes();
        boolean useCompactHexa = escapeType.getUseCompactHexa();
        int c1 = -1;
        int c2 = reader.read();
        while (c2 >= 0) {
            char next;
            char escape;
            int c0 = c1;
            c1 = c2;
            int codepoint = CssIdentifierEscapeUtil.codePointAt((char)c1, (char)(c2 = reader.read()));
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint] && (c0 >= 0 || codepoint < 48 || codepoint > 57)) {
                writer.write(c1);
                continue;
            }
            if (codepoint == 45 && level < 3) {
                if (c0 >= 0 || c2 < 0) {
                    writer.write(c1);
                    continue;
                }
                if (c2 != 45 && (c2 < 48 || c2 > 57)) {
                    writer.write(c1);
                    continue;
                }
            }
            if (codepoint == 95 && level < 3 && c0 >= 0) {
                writer.write(c1);
                continue;
            }
            if (codepoint > 159 && level < ESCAPE_LEVELS[160]) {
                writer.write(c1);
                if (Character.charCount(codepoint) <= 1) continue;
                writer.write(c2);
                c0 = c1;
                c1 = c2;
                c2 = reader.read();
                continue;
            }
            if (Character.charCount(codepoint) > 1) {
                c0 = c1;
                c1 = c2;
                c2 = reader.read();
            }
            if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN && (escape = BACKSLASH_CHARS[codepoint]) != BACKSLASH_CHARS_NO_ESCAPE) {
                writer.write(92);
                writer.write(escape);
                continue;
            }
            char c = next = c2 >= 0 ? (char)c2 : (char)'\u0000';
            if (useCompactHexa) {
                writer.write(92);
                writer.write(CssIdentifierEscapeUtil.toCompactHexa(codepoint, next, level));
                continue;
            }
            writer.write(92);
            writer.write(CssIdentifierEscapeUtil.toSixDigitHexa(codepoint, next, level));
        }
    }

    static void escape(char[] text, int offset, int len, Writer writer, CssIdentifierEscapeType escapeType, CssIdentifierEscapeLevel escapeLevel) throws IOException {
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
            char c1;
            int codepoint = Character.codePointAt(text, i);
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint] && (i > offset || codepoint < 48 || codepoint > 57) || codepoint == 45 && level < 3 && (i > offset || i + 1 >= max || (c1 = text[i + 1]) != '-' && (c1 < '0' || c1 > '9')) || codepoint == 95 && level < 3 && i > offset) continue;
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
                writer.write(CssIdentifierEscapeUtil.toCompactHexa(codepoint, next, level));
                continue;
            }
            writer.write(92);
            writer.write(CssIdentifierEscapeUtil.toSixDigitHexa(codepoint, next, level));
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
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[32] = 32;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[33] = 33;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[34] = 34;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[35] = 35;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[36] = 36;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[37] = 37;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[38] = 38;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[39] = 39;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[40] = 40;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[41] = 41;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[42] = 42;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[43] = 43;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[44] = 44;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[45] = 45;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[46] = 46;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[47] = 47;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[59] = 59;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[60] = 60;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[61] = 61;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[62] = 62;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[63] = 63;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[64] = 64;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[91] = 91;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[92] = 92;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[93] = 93;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[94] = 94;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[95] = 95;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[96] = 96;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[123] = 123;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[124] = 124;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[125] = 125;
        CssIdentifierEscapeUtil.BACKSLASH_CHARS[126] = 126;
        ESCAPE_LEVELS = new byte[161];
        Arrays.fill(ESCAPE_LEVELS, (byte)3);
        for (c = 128; c < 161; c = (int)((char)(c + 1))) {
            CssIdentifierEscapeUtil.ESCAPE_LEVELS[c] = 2;
        }
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            CssIdentifierEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            CssIdentifierEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            CssIdentifierEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[32] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[33] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[34] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[35] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[36] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[37] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[38] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[39] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[40] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[41] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[42] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[43] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[44] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[45] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[46] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[47] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[58] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[59] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[60] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[61] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[62] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[63] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[64] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[91] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[92] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[93] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[94] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[95] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[96] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[123] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[124] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[125] = 1;
        CssIdentifierEscapeUtil.ESCAPE_LEVELS[126] = 1;
        for (c = 0; c <= 31; c = (int)((char)(c + 1))) {
            CssIdentifierEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
        for (c = 127; c <= 159; c = (int)((char)(c + 1))) {
            CssIdentifierEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
    }
}

