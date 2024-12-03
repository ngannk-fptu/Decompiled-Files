/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.properties;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import org.unbescape.properties.PropertiesKeyEscapeLevel;

final class PropertiesKeyEscapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static final char[] ESCAPE_UHEXA_PREFIX;
    private static char[] HEXA_CHARS_UPPER;
    private static int SEC_CHARS_LEN;
    private static char SEC_CHARS_NO_SEC;
    private static char[] SEC_CHARS;
    private static final char ESCAPE_LEVELS_LEN = '\u00a1';
    private static final byte[] ESCAPE_LEVELS;

    private PropertiesKeyEscapeUtil() {
    }

    static char[] toUHexa(int codepoint) {
        char[] result = new char[4];
        result[3] = HEXA_CHARS_UPPER[codepoint % 16];
        result[2] = HEXA_CHARS_UPPER[(codepoint >>> 4) % 16];
        result[1] = HEXA_CHARS_UPPER[(codepoint >>> 8) % 16];
        result[0] = HEXA_CHARS_UPPER[(codepoint >>> 12) % 16];
        return result;
    }

    static String escape(String text, PropertiesKeyEscapeLevel escapeLevel) {
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        for (int i = 0; i < max; ++i) {
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
            if (codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
                strBuilder.append('\\');
                strBuilder.append(sec);
                continue;
            }
            if (Character.charCount(codepoint) > 1) {
                char[] codepointChars = Character.toChars(codepoint);
                strBuilder.append(ESCAPE_UHEXA_PREFIX);
                strBuilder.append(PropertiesKeyEscapeUtil.toUHexa(codepointChars[0]));
                strBuilder.append(ESCAPE_UHEXA_PREFIX);
                strBuilder.append(PropertiesKeyEscapeUtil.toUHexa(codepointChars[1]));
                continue;
            }
            strBuilder.append(ESCAPE_UHEXA_PREFIX);
            strBuilder.append(PropertiesKeyEscapeUtil.toUHexa(codepoint));
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void escape(Reader reader, Writer writer, PropertiesKeyEscapeLevel escapeLevel) throws IOException {
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        int c2 = reader.read();
        while (c2 >= 0) {
            char sec;
            int c1 = c2;
            int codepoint = PropertiesKeyEscapeUtil.codePointAt((char)c1, (char)(c2 = reader.read()));
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
            if (codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
                writer.write(92);
                writer.write(sec);
                continue;
            }
            if (Character.charCount(codepoint) > 1) {
                char[] codepointChars = Character.toChars(codepoint);
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(PropertiesKeyEscapeUtil.toUHexa(codepointChars[0]));
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(PropertiesKeyEscapeUtil.toUHexa(codepointChars[1]));
                continue;
            }
            writer.write(ESCAPE_UHEXA_PREFIX);
            writer.write(PropertiesKeyEscapeUtil.toUHexa(codepoint));
        }
    }

    static void escape(char[] text, int offset, int len, Writer writer, PropertiesKeyEscapeLevel escapeLevel) throws IOException {
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        int max = offset + len;
        int readOffset = offset;
        for (int i = offset; i < max; ++i) {
            char sec;
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
            if (codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
                writer.write(92);
                writer.write(sec);
                continue;
            }
            if (Character.charCount(codepoint) > 1) {
                char[] codepointChars = Character.toChars(codepoint);
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(PropertiesKeyEscapeUtil.toUHexa(codepointChars[0]));
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(PropertiesKeyEscapeUtil.toUHexa(codepointChars[1]));
                continue;
            }
            writer.write(ESCAPE_UHEXA_PREFIX);
            writer.write(PropertiesKeyEscapeUtil.toUHexa(codepoint));
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
        ESCAPE_UHEXA_PREFIX = "\\u".toCharArray();
        HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
        SEC_CHARS_LEN = 93;
        SEC_CHARS_NO_SEC = (char)42;
        SEC_CHARS = new char[SEC_CHARS_LEN];
        Arrays.fill(SEC_CHARS, SEC_CHARS_NO_SEC);
        PropertiesKeyEscapeUtil.SEC_CHARS[9] = 116;
        PropertiesKeyEscapeUtil.SEC_CHARS[10] = 110;
        PropertiesKeyEscapeUtil.SEC_CHARS[12] = 102;
        PropertiesKeyEscapeUtil.SEC_CHARS[13] = 114;
        PropertiesKeyEscapeUtil.SEC_CHARS[32] = 32;
        PropertiesKeyEscapeUtil.SEC_CHARS[58] = 58;
        PropertiesKeyEscapeUtil.SEC_CHARS[59] = 61;
        PropertiesKeyEscapeUtil.SEC_CHARS[92] = 92;
        ESCAPE_LEVELS = new byte[161];
        Arrays.fill(ESCAPE_LEVELS, (byte)3);
        for (c = 128; c < 161; c = (int)((char)(c + 1))) {
            PropertiesKeyEscapeUtil.ESCAPE_LEVELS[c] = 2;
        }
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            PropertiesKeyEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            PropertiesKeyEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            PropertiesKeyEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        PropertiesKeyEscapeUtil.ESCAPE_LEVELS[9] = 1;
        PropertiesKeyEscapeUtil.ESCAPE_LEVELS[10] = 1;
        PropertiesKeyEscapeUtil.ESCAPE_LEVELS[12] = 1;
        PropertiesKeyEscapeUtil.ESCAPE_LEVELS[13] = 1;
        PropertiesKeyEscapeUtil.ESCAPE_LEVELS[32] = 1;
        PropertiesKeyEscapeUtil.ESCAPE_LEVELS[58] = 1;
        PropertiesKeyEscapeUtil.ESCAPE_LEVELS[59] = 1;
        PropertiesKeyEscapeUtil.ESCAPE_LEVELS[92] = 1;
        for (c = 0; c <= 31; c = (int)((char)(c + 1))) {
            PropertiesKeyEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
        for (c = 127; c <= 159; c = (int)((char)(c + 1))) {
            PropertiesKeyEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
    }
}

