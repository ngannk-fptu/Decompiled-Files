/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import org.unbescape.json.JsonEscapeLevel;
import org.unbescape.json.JsonEscapeType;

final class JsonEscapeUtil {
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

    private JsonEscapeUtil() {
    }

    static char[] toUHexa(int codepoint) {
        char[] result = new char[4];
        result[3] = HEXA_CHARS_UPPER[codepoint % 16];
        result[2] = HEXA_CHARS_UPPER[(codepoint >>> 4) % 16];
        result[1] = HEXA_CHARS_UPPER[(codepoint >>> 8) % 16];
        result[0] = HEXA_CHARS_UPPER[(codepoint >>> 12) % 16];
        return result;
    }

    static String escape(String text, JsonEscapeType escapeType, JsonEscapeLevel escapeLevel) {
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useSECs = escapeType.getUseSECs();
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        for (int i = 0; i < max; ++i) {
            char sec;
            int codepoint = Character.codePointAt(text, i);
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint] || codepoint == 47 && level < 3 && (i == 0 || text.charAt(i - 1) != '<')) continue;
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
            if (useSECs && codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
                strBuilder.append('\\');
                strBuilder.append(sec);
                continue;
            }
            if (Character.charCount(codepoint) > 1) {
                char[] codepointChars = Character.toChars(codepoint);
                strBuilder.append(ESCAPE_UHEXA_PREFIX);
                strBuilder.append(JsonEscapeUtil.toUHexa(codepointChars[0]));
                strBuilder.append(ESCAPE_UHEXA_PREFIX);
                strBuilder.append(JsonEscapeUtil.toUHexa(codepointChars[1]));
                continue;
            }
            strBuilder.append(ESCAPE_UHEXA_PREFIX);
            strBuilder.append(JsonEscapeUtil.toUHexa(codepoint));
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void escape(Reader reader, Writer writer, JsonEscapeType escapeType, JsonEscapeLevel escapeLevel) throws IOException {
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useSECs = escapeType.getUseSECs();
        int c1 = -1;
        int c2 = reader.read();
        while (c2 >= 0) {
            char sec;
            int c0 = c1;
            c1 = c2;
            int codepoint = JsonEscapeUtil.codePointAt((char)c1, (char)(c2 = reader.read()));
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint]) {
                writer.write(c1);
                continue;
            }
            if (codepoint == 47 && level < 3 && c0 != 60) {
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
            if (useSECs && codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
                writer.write(92);
                writer.write(sec);
                continue;
            }
            if (Character.charCount(codepoint) > 1) {
                char[] codepointChars = Character.toChars(codepoint);
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(JsonEscapeUtil.toUHexa(codepointChars[0]));
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(JsonEscapeUtil.toUHexa(codepointChars[1]));
                continue;
            }
            writer.write(ESCAPE_UHEXA_PREFIX);
            writer.write(JsonEscapeUtil.toUHexa(codepoint));
        }
    }

    static void escape(char[] text, int offset, int len, Writer writer, JsonEscapeType escapeType, JsonEscapeLevel escapeLevel) throws IOException {
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useSECs = escapeType.getUseSECs();
        int max = offset + len;
        int readOffset = offset;
        for (int i = offset; i < max; ++i) {
            char sec;
            int codepoint = Character.codePointAt(text, i);
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint] || codepoint == 47 && level < 3 && (i == offset || text[i - 1] != '<')) continue;
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
            if (useSECs && codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
                writer.write(92);
                writer.write(sec);
                continue;
            }
            if (Character.charCount(codepoint) > 1) {
                char[] codepointChars = Character.toChars(codepoint);
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(JsonEscapeUtil.toUHexa(codepointChars[0]));
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(JsonEscapeUtil.toUHexa(codepointChars[1]));
                continue;
            }
            writer.write(ESCAPE_UHEXA_PREFIX);
            writer.write(JsonEscapeUtil.toUHexa(codepoint));
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

    static int parseIntFromReference(int[] text, int start, int end, int radix) {
        int result = 0;
        for (int i = start; i < end; ++i) {
            char c = (char)text[i];
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

    static String unescape(String text) {
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
                switch (c1) {
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
                    case '\\': {
                        codepoint = 92;
                        referenceOffset = i + 1;
                        break;
                    }
                    case '/': {
                        codepoint = 47;
                        referenceOffset = i + 1;
                    }
                }
                if (codepoint == -1) {
                    if (c1 == 'u') {
                        char cf;
                        int f;
                        for (f = i + 2; f < i + 6 && f < max && ((cf = text.charAt(f)) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f'); ++f) {
                        }
                        if (f - (i + 2) < 4) {
                            ++i;
                            continue;
                        }
                        codepoint = JsonEscapeUtil.parseIntFromReference(text, i + 2, f, 16);
                        referenceOffset = f - 1;
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

    static void unescape(Reader reader, Writer writer) throws IOException {
        if (reader == null) {
            return;
        }
        int[] escapes = new int[4];
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            if (c1 != 92 || c2 < 0) {
                writer.write(c1);
                continue;
            }
            int codepoint = -1;
            if (c1 == 92) {
                switch (c2) {
                    case 98: {
                        codepoint = 8;
                        c1 = c2;
                        c2 = reader.read();
                        break;
                    }
                    case 116: {
                        codepoint = 9;
                        c1 = c2;
                        c2 = reader.read();
                        break;
                    }
                    case 110: {
                        codepoint = 10;
                        c1 = c2;
                        c2 = reader.read();
                        break;
                    }
                    case 102: {
                        codepoint = 12;
                        c1 = c2;
                        c2 = reader.read();
                        break;
                    }
                    case 114: {
                        codepoint = 13;
                        c1 = c2;
                        c2 = reader.read();
                        break;
                    }
                    case 34: {
                        codepoint = 34;
                        c1 = c2;
                        c2 = reader.read();
                        break;
                    }
                    case 92: {
                        codepoint = 92;
                        c1 = c2;
                        c2 = reader.read();
                        break;
                    }
                    case 47: {
                        codepoint = 47;
                        c1 = c2;
                        c2 = reader.read();
                    }
                }
                if (codepoint == -1) {
                    if (c2 == 117) {
                        int escapei;
                        int ce = reader.read();
                        for (escapei = 0; ce >= 0 && escapei < 4 && (ce >= 48 && ce <= 57 || ce >= 65 && ce <= 70 || ce >= 97 && ce <= 102); ++escapei) {
                            escapes[escapei] = ce;
                            ce = reader.read();
                        }
                        if (escapei < 4) {
                            writer.write(c1);
                            writer.write(c2);
                            for (int i = 0; i < escapei; ++i) {
                                c1 = c2;
                                c2 = escapes[i];
                                writer.write(c2);
                            }
                            c1 = c2;
                            c2 = ce;
                            continue;
                        }
                        c1 = escapes[3];
                        c2 = ce;
                        codepoint = JsonEscapeUtil.parseIntFromReference(escapes, 0, 4, 16);
                    } else {
                        writer.write(c1);
                        writer.write(c2);
                        c1 = c2;
                        c2 = reader.read();
                        continue;
                    }
                }
            }
            if (codepoint > 65535) {
                writer.write(Character.toChars(codepoint));
                continue;
            }
            writer.write((char)codepoint);
        }
    }

    static void unescape(char[] text, int offset, int len, Writer writer) throws IOException {
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
                switch (c1) {
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
                    case '\\': {
                        codepoint = 92;
                        referenceOffset = i + 1;
                        break;
                    }
                    case '/': {
                        codepoint = 47;
                        referenceOffset = i + 1;
                    }
                }
                if (codepoint == -1) {
                    if (c1 == 'u') {
                        char cf;
                        int f;
                        for (f = i + 2; f < i + 6 && f < max && ((cf = text[f]) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f'); ++f) {
                        }
                        if (f - (i + 2) < 4) {
                            ++i;
                            continue;
                        }
                        codepoint = JsonEscapeUtil.parseIntFromReference(text, i + 2, f, 16);
                        referenceOffset = f - 1;
                    } else {
                        ++i;
                        continue;
                    }
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
        JsonEscapeUtil.SEC_CHARS[8] = 98;
        JsonEscapeUtil.SEC_CHARS[9] = 116;
        JsonEscapeUtil.SEC_CHARS[10] = 110;
        JsonEscapeUtil.SEC_CHARS[12] = 102;
        JsonEscapeUtil.SEC_CHARS[13] = 114;
        JsonEscapeUtil.SEC_CHARS[34] = 34;
        JsonEscapeUtil.SEC_CHARS[92] = 92;
        JsonEscapeUtil.SEC_CHARS[47] = 47;
        ESCAPE_LEVELS = new byte[161];
        Arrays.fill(ESCAPE_LEVELS, (byte)3);
        for (c = 128; c < 161; c = (int)((char)(c + 1))) {
            JsonEscapeUtil.ESCAPE_LEVELS[c] = 2;
        }
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            JsonEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            JsonEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            JsonEscapeUtil.ESCAPE_LEVELS[c] = 4;
        }
        JsonEscapeUtil.ESCAPE_LEVELS[8] = 1;
        JsonEscapeUtil.ESCAPE_LEVELS[9] = 1;
        JsonEscapeUtil.ESCAPE_LEVELS[10] = 1;
        JsonEscapeUtil.ESCAPE_LEVELS[12] = 1;
        JsonEscapeUtil.ESCAPE_LEVELS[13] = 1;
        JsonEscapeUtil.ESCAPE_LEVELS[34] = 1;
        JsonEscapeUtil.ESCAPE_LEVELS[92] = 1;
        JsonEscapeUtil.ESCAPE_LEVELS[47] = 1;
        JsonEscapeUtil.ESCAPE_LEVELS[38] = 1;
        for (c = 0; c <= 31; c = (int)((char)(c + 1))) {
            JsonEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
        for (c = 127; c <= 159; c = (int)((char)(c + 1))) {
            JsonEscapeUtil.ESCAPE_LEVELS[c] = 1;
        }
    }
}

