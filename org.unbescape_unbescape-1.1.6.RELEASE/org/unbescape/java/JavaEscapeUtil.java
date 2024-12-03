/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.java;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import org.unbescape.java.JavaEscapeLevel;

final class JavaEscapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static final char ESCAPE_UHEXA_PREFIX2 = 'u';
    private static final char[] ESCAPE_UHEXA_PREFIX;
    private static char[] HEXA_CHARS_UPPER;
    private static char[] HEXA_CHARS_LOWER;
    private static int SEC_CHARS_LEN;
    private static char SEC_CHARS_NO_SEC;
    private static char[] SEC_CHARS;
    private static final char ESCAPE_LEVELS_LEN = '\u00a1';
    private static final byte[] ESCAPE_LEVELS;

    private JavaEscapeUtil() {
    }

    static char[] toUHexa(int codepoint) {
        char[] result = new char[4];
        result[3] = HEXA_CHARS_UPPER[codepoint % 16];
        result[2] = HEXA_CHARS_UPPER[(codepoint >>> 4) % 16];
        result[1] = HEXA_CHARS_UPPER[(codepoint >>> 8) % 16];
        result[0] = HEXA_CHARS_UPPER[(codepoint >>> 12) % 16];
        return result;
    }

    static String escape(String text, JavaEscapeLevel escapeLevel) {
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
                strBuilder.append(JavaEscapeUtil.toUHexa(codepointChars[0]));
                strBuilder.append(ESCAPE_UHEXA_PREFIX);
                strBuilder.append(JavaEscapeUtil.toUHexa(codepointChars[1]));
                continue;
            }
            strBuilder.append(ESCAPE_UHEXA_PREFIX);
            strBuilder.append(JavaEscapeUtil.toUHexa(codepoint));
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void escape(Reader reader, Writer writer, JavaEscapeLevel escapeLevel) throws IOException {
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        int c2 = reader.read();
        while (c2 >= 0) {
            char sec;
            int c1 = c2;
            int codepoint = JavaEscapeUtil.codePointAt((char)c1, (char)(c2 = reader.read()));
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
                writer.write(JavaEscapeUtil.toUHexa(codepointChars[0]));
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(JavaEscapeUtil.toUHexa(codepointChars[1]));
                continue;
            }
            writer.write(ESCAPE_UHEXA_PREFIX);
            writer.write(JavaEscapeUtil.toUHexa(codepoint));
        }
    }

    static void escape(char[] text, int offset, int len, Writer writer, JavaEscapeLevel escapeLevel) throws IOException {
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
                writer.write(JavaEscapeUtil.toUHexa(codepointChars[0]));
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(JavaEscapeUtil.toUHexa(codepointChars[1]));
                continue;
            }
            writer.write(ESCAPE_UHEXA_PREFIX);
            writer.write(JavaEscapeUtil.toUHexa(codepoint));
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }

    static int parseIntFromReference(String text, int start, int end, int radix) {
        int result = 0;
        for (int i = start; i < end; ++i) {
            char c = text.charAt(i);
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; ++j) {
                if (c != HEXA_CHARS_UPPER[j] && c != HEXA_CHARS_LOWER[j]) continue;
                n = j;
                break;
            }
            result = radix * result + n;
        }
        return result;
    }

    static int parseIntFromReference(char[] text, int start, int end, int radix) {
        int result = 0;
        for (int i = start; i < end; ++i) {
            char c = text[i];
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; ++j) {
                if (c != HEXA_CHARS_UPPER[j] && c != HEXA_CHARS_LOWER[j]) continue;
                n = j;
                break;
            }
            result = radix * result + n;
        }
        return result;
    }

    static boolean isOctalEscape(String text, int start, int end) {
        if (start >= end) {
            return false;
        }
        char c1 = text.charAt(start);
        if (c1 < '0' || c1 > '7') {
            return false;
        }
        if (start + 1 >= end) {
            return c1 != '0';
        }
        char c2 = text.charAt(start + 1);
        if (c2 < '0' || c2 > '7') {
            return c1 != '0';
        }
        if (start + 2 >= end) {
            return c1 != '0' || c2 != '0';
        }
        char c3 = text.charAt(start + 2);
        if (c3 < '0' || c3 > '7') {
            return c1 != '0' || c2 != '0';
        }
        return c1 != '0' || c2 != '0' || c3 != '0';
    }

    static boolean isOctalEscape(char[] text, int start, int end) {
        if (start >= end) {
            return false;
        }
        char c1 = text[start];
        if (c1 < '0' || c1 > '7') {
            return false;
        }
        if (start + 1 >= end) {
            return c1 != '0';
        }
        char c2 = text[start + 1];
        if (c2 < '0' || c2 > '7') {
            return c1 != '0';
        }
        if (start + 2 >= end) {
            return c1 != '0' || c2 != '0';
        }
        char c3 = text[start + 2];
        if (c3 < '0' || c3 > '7') {
            return c1 != '0' || c2 != '0';
        }
        return c1 != '0' || c2 != '0' || c3 != '0';
    }

    static String unicodeUnescape(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        int referenceOffset = 0;
        for (int i = 0; i < max; ++i) {
            char c = text.charAt(i);
            if (c != '\\' || i + 1 >= max) continue;
            int codepoint = -1;
            if (c == '\\') {
                char c1 = text.charAt(i + 1);
                if (c1 == 'u') {
                    char cf;
                    char cf2;
                    int f;
                    for (f = i + 2; f < max && (cf2 = text.charAt(f)) == 'u'; ++f) {
                    }
                    int s = f;
                    while (f < s + 4 && f < max && ((cf = text.charAt(f)) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f')) {
                        ++f;
                    }
                    if (f - s < 4) {
                        ++i;
                        continue;
                    }
                    codepoint = JavaEscapeUtil.parseIntFromReference(text, s, f, 16);
                    referenceOffset = f - 1;
                } else {
                    ++i;
                    continue;
                }
            }
            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 5);
            }
            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }
            i = referenceOffset;
            readOffset = i + 1;
            if (codepoint > 65535) {
                strBuilder.append(Character.toChars(codepoint));
                continue;
            }
            strBuilder.append((char)codepoint);
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static boolean requiresUnicodeUnescape(char[] text, int offset, int len) {
        if (text == null) {
            return false;
        }
        int max = offset + len;
        for (int i = offset; i < max; ++i) {
            char c1;
            char c = text[i];
            if (c != '\\' || i + 1 >= max || c != '\\' || (c1 = text[i + 1]) != 'u') continue;
            return true;
        }
        return false;
    }

    static void unicodeUnescape(char[] text, int offset, int len, Writer writer) throws IOException {
        if (text == null) {
            return;
        }
        int max = offset + len;
        int readOffset = offset;
        int referenceOffset = offset;
        for (int i = offset; i < max; ++i) {
            char c = text[i];
            if (c != '\\' || i + 1 >= max) continue;
            int codepoint = -1;
            if (c == '\\') {
                char c1 = text[i + 1];
                if (c1 == 'u') {
                    char cf;
                    char cf2;
                    int f;
                    for (f = i + 2; f < max && (cf2 = text[f]) == 'u'; ++f) {
                    }
                    int s = f;
                    while (f < s + 4 && f < max && ((cf = text[f]) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f')) {
                        ++f;
                    }
                    if (f - s < 4) {
                        ++i;
                        continue;
                    }
                    codepoint = JavaEscapeUtil.parseIntFromReference(text, s, f, 16);
                    referenceOffset = f - 1;
                } else {
                    ++i;
                    continue;
                }
            }
            if (i - readOffset > 0) {
                writer.write(text, readOffset, i - readOffset);
            }
            i = referenceOffset;
            readOffset = i + 1;
            if (codepoint > 65535) {
                writer.write(Character.toChars(codepoint));
                continue;
            }
            writer.write((char)codepoint);
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }

    static String unescape(String text) {
        if (text == null) {
            return null;
        }
        String unicodeEscapedText = JavaEscapeUtil.unicodeUnescape(text);
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = unicodeEscapedText.length();
        int readOffset = 0;
        int referenceOffset = 0;
        for (int i = 0; i < max; ++i) {
            char c = unicodeEscapedText.charAt(i);
            if (c != '\\' || i + 1 >= max) continue;
            int codepoint = -1;
            if (c == '\\') {
                char c1 = unicodeEscapedText.charAt(i + 1);
                switch (c1) {
                    case '0': {
                        if (JavaEscapeUtil.isOctalEscape(unicodeEscapedText, i + 1, max)) break;
                        codepoint = 0;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 'b': {
                        codepoint = 8;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 't': {
                        codepoint = 9;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 'n': {
                        codepoint = 10;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 'f': {
                        codepoint = 12;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 'r': {
                        codepoint = 13;
                        referenceOffset = i + 1;
                        break;
                    }
                    case '\"': {
                        codepoint = 34;
                        referenceOffset = i + 1;
                        break;
                    }
                    case '\'': {
                        codepoint = 39;
                        referenceOffset = i + 1;
                        break;
                    }
                    case '\\': {
                        codepoint = 92;
                        referenceOffset = i + 1;
                    }
                }
                if (codepoint == -1) {
                    if (c1 >= '0' && c1 <= '7') {
                        char cf;
                        int f;
                        for (f = i + 2; f < i + 4 && f < max && (cf = unicodeEscapedText.charAt(f)) >= '0' && cf <= '7'; ++f) {
                        }
                        codepoint = JavaEscapeUtil.parseIntFromReference(unicodeEscapedText, i + 1, f, 8);
                        if (codepoint > 255) {
                            codepoint = JavaEscapeUtil.parseIntFromReference(unicodeEscapedText, i + 1, f - 1, 8);
                            referenceOffset = f - 2;
                        } else {
                            referenceOffset = f - 1;
                        }
                    } else {
                        ++i;
                        continue;
                    }
                }
            }
            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 5);
            }
            if (i - readOffset > 0) {
                strBuilder.append(unicodeEscapedText, readOffset, i);
            }
            i = referenceOffset;
            readOffset = i + 1;
            if (codepoint > 65535) {
                strBuilder.append(Character.toChars(codepoint));
                continue;
            }
            strBuilder.append((char)codepoint);
        }
        if (strBuilder == null) {
            return unicodeEscapedText;
        }
        if (max - readOffset > 0) {
            strBuilder.append(unicodeEscapedText, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void unescape(Reader reader, Writer writer) throws IOException {
        if (reader == null) {
            return;
        }
        char[] buffer = new char[20];
        int read = reader.read(buffer, 0, buffer.length);
        if (read < 0) {
            return;
        }
        int bufferSize = read;
        while (bufferSize > 0 || read >= 0) {
            int nonEscCounter = 0;
            int n = bufferSize;
            while (nonEscCounter < 8 && n-- != 0) {
                if (buffer[n] == '\\') {
                    nonEscCounter = 0;
                    continue;
                }
                ++nonEscCounter;
            }
            if (nonEscCounter < 8 && read >= 0) {
                if (bufferSize == buffer.length) {
                    char[] newBuffer = new char[buffer.length + buffer.length / 2];
                    System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                    buffer = newBuffer;
                }
                if ((read = reader.read(buffer, bufferSize, buffer.length - bufferSize)) < 0) continue;
                bufferSize += read;
                continue;
            }
            n = n < 0 ? bufferSize : n + nonEscCounter;
            JavaEscapeUtil.unescape(buffer, 0, n, writer);
            System.arraycopy(buffer, n, buffer, 0, bufferSize - n);
            if ((read = reader.read(buffer, bufferSize -= n, buffer.length - bufferSize)) < 0) continue;
            bufferSize += read;
        }
    }

    static void unescape(char[] text, int offset, int len, Writer writer) throws IOException {
        if (text == null) {
            return;
        }
        char[] unicodeEscapedText = text;
        int unicodeEscapedOffset = offset;
        int unicodeEscapedLen = len;
        if (JavaEscapeUtil.requiresUnicodeUnescape(text, offset, len)) {
            CharArrayWriter charArrayWriter = new CharArrayWriter(len + 2);
            JavaEscapeUtil.unicodeUnescape(text, offset, len, charArrayWriter);
            unicodeEscapedText = charArrayWriter.toCharArray();
            unicodeEscapedOffset = 0;
            unicodeEscapedLen = unicodeEscapedText.length;
        }
        int max = unicodeEscapedOffset + unicodeEscapedLen;
        int readOffset = unicodeEscapedOffset;
        int referenceOffset = unicodeEscapedOffset;
        for (int i = unicodeEscapedOffset; i < max; ++i) {
            char c = unicodeEscapedText[i];
            if (c != '\\' || i + 1 >= max) continue;
            int codepoint = -1;
            if (c == '\\') {
                char c1 = unicodeEscapedText[i + 1];
                switch (c1) {
                    case '0': {
                        if (JavaEscapeUtil.isOctalEscape(unicodeEscapedText, i + 1, max)) break;
                        codepoint = 0;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 'b': {
                        codepoint = 8;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 't': {
                        codepoint = 9;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 'n': {
                        codepoint = 10;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 'f': {
                        codepoint = 12;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 'r': {
                        codepoint = 13;
                        referenceOffset = i + 1;
                        break;
                    }
                    case '\"': {
                        codepoint = 34;
                        referenceOffset = i + 1;
                        break;
                    }
                    case '\'': {
                        codepoint = 39;
                        referenceOffset = i + 1;
                        break;
                    }
                    case '\\': {
                        codepoint = 92;
                        referenceOffset = i + 1;
                    }
                }
                if (codepoint == -1) {
                    if (c1 >= '0' && c1 <= '7') {
                        char cf;
                        int f;
                        for (f = i + 2; f < i + 4 && f < max && (cf = unicodeEscapedText[f]) >= '0' && cf <= '7'; ++f) {
                        }
                        codepoint = JavaEscapeUtil.parseIntFromReference(unicodeEscapedText, i + 1, f, 8);
                        if (codepoint > 255) {
                            codepoint = JavaEscapeUtil.parseIntFromReference(unicodeEscapedText, i + 1, f - 1, 8);
                            referenceOffset = f - 2;
                        } else {
                            referenceOffset = f - 1;
                        }
                    } else {
                        ++i;
                        continue;
                    }
                }
            }
            if (i - readOffset > 0) {
                writer.write(unicodeEscapedText, readOffset, i - readOffset);
            }
            i = referenceOffset;
            readOffset = i + 1;
            if (codepoint > 65535) {
                writer.write(Character.toChars(codepoint));
                continue;
            }
            writer.write((char)codepoint);
        }
        if (max - readOffset > 0) {
            writer.write(unicodeEscapedText, readOffset, max - readOffset);
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
        HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();
        SEC_CHARS_LEN = 93;
        SEC_CHARS_NO_SEC = (char)42;
        SEC_CHARS = new char[SEC_CHARS_LEN];
        Arrays.fill(SEC_CHARS, SEC_CHARS_NO_SEC);
        JavaEscapeUtil.SEC_CHARS[8] = 98;
        JavaEscapeUtil.SEC_CHARS[9] = 116;
        JavaEscapeUtil.SEC_CHARS[10] = 110;
        JavaEscapeUtil.SEC_CHARS[12] = 102;
        JavaEscapeUtil.SEC_CHARS[13] = 114;
        JavaEscapeUtil.SEC_CHARS[34] = 34;
        JavaEscapeUtil.SEC_CHARS[39] = 39;
        JavaEscapeUtil.SEC_CHARS[92] = 92;
        ESCAPE_LEVELS = new byte[161];
        Arrays.fill(ESCAPE_LEVELS, (byte)3);
        for (c = 128; c < 161; c = (int)((char)(c + 1))) {
            JavaEscapeUtil.ESCAPE_LEVELS[c] = 2;
        }
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            JavaEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            JavaEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            JavaEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        JavaEscapeUtil.ESCAPE_LEVELS[8] = 1;
        JavaEscapeUtil.ESCAPE_LEVELS[9] = 1;
        JavaEscapeUtil.ESCAPE_LEVELS[10] = 1;
        JavaEscapeUtil.ESCAPE_LEVELS[12] = 1;
        JavaEscapeUtil.ESCAPE_LEVELS[13] = 1;
        JavaEscapeUtil.ESCAPE_LEVELS[34] = 1;
        JavaEscapeUtil.ESCAPE_LEVELS[39] = 3;
        JavaEscapeUtil.ESCAPE_LEVELS[92] = 1;
        for (c = 0; c <= 31; c = (int)((char)(c + 1))) {
            JavaEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
        for (c = 127; c <= 159; c = (int)((char)(c + 1))) {
            JavaEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
    }
}

