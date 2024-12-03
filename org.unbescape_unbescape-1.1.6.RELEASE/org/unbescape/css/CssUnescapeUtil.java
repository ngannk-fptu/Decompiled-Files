/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.css;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

final class CssUnescapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private CssUnescapeUtil() {
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
                    case '\n': {
                        codepoint = -2;
                        referenceOffset = i + 1;
                        break;
                    }
                    case ' ': 
                    case '!': 
                    case '\"': 
                    case '#': 
                    case '$': 
                    case '%': 
                    case '&': 
                    case '\'': 
                    case '(': 
                    case ')': 
                    case '*': 
                    case '+': 
                    case ',': 
                    case '-': 
                    case '.': 
                    case '/': 
                    case ':': 
                    case ';': 
                    case '<': 
                    case '=': 
                    case '>': 
                    case '?': 
                    case '@': 
                    case '[': 
                    case '\\': 
                    case ']': 
                    case '^': 
                    case '_': 
                    case '`': 
                    case '{': 
                    case '|': 
                    case '}': 
                    case '~': {
                        codepoint = c1;
                        referenceOffset = i + 1;
                    }
                }
                if (codepoint == -1) {
                    if (c1 >= '0' && c1 <= '9' || c1 >= 'A' && c1 <= 'F' || c1 >= 'a' && c1 <= 'f') {
                        char cf;
                        int f;
                        for (f = i + 2; f < i + 7 && f < max && ((cf = text.charAt(f)) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f'); ++f) {
                        }
                        codepoint = CssUnescapeUtil.parseIntFromReference(text, i + 1, f, 16);
                        referenceOffset = f - 1;
                        if (f < max && text.charAt(f) == ' ') {
                            ++referenceOffset;
                        }
                    } else {
                        if (c1 == '\r' || c1 == '\f') {
                            ++i;
                            continue;
                        }
                        codepoint = c1;
                        referenceOffset = i + 1;
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
            if (codepoint == -2) continue;
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
        int escapei = 0;
        char[] escapes = new char[6];
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
                    case 10: {
                        codepoint = -2;
                        c1 = c2;
                        c2 = reader.read();
                        break;
                    }
                    case 32: 
                    case 33: 
                    case 34: 
                    case 35: 
                    case 36: 
                    case 37: 
                    case 38: 
                    case 39: 
                    case 40: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: 
                    case 45: 
                    case 46: 
                    case 47: 
                    case 58: 
                    case 59: 
                    case 60: 
                    case 61: 
                    case 62: 
                    case 63: 
                    case 64: 
                    case 91: 
                    case 92: 
                    case 93: 
                    case 94: 
                    case 95: 
                    case 96: 
                    case 123: 
                    case 124: 
                    case 125: 
                    case 126: {
                        codepoint = c2;
                        c1 = c2;
                        c2 = reader.read();
                    }
                }
                if (codepoint == -1) {
                    if (c2 >= 48 && c2 <= 57 || c2 >= 65 && c2 <= 70 || c2 >= 97 && c2 <= 102) {
                        int ce = c2;
                        for (escapei = 0; ce >= 0 && escapei < 6 && (ce >= 48 && ce <= 57 || ce >= 65 && ce <= 70 || ce >= 97 && ce <= 102); ++escapei) {
                            escapes[escapei] = (char)ce;
                            ce = reader.read();
                        }
                        c1 = escapes[5];
                        c2 = ce;
                        codepoint = CssUnescapeUtil.parseIntFromReference(escapes, 0, escapei, 16);
                        if (c2 == 32) {
                            c1 = c2;
                            c2 = reader.read();
                        }
                    } else {
                        if (c2 == 13 || c2 == 12) {
                            writer.write(c1);
                            continue;
                        }
                        codepoint = c2;
                        c1 = c2;
                        c2 = reader.read();
                    }
                }
            }
            if (codepoint > 65535) {
                writer.write(Character.toChars(codepoint));
                continue;
            }
            if (codepoint == -2) continue;
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
                int c1 = text[i + 1];
                switch (c1) {
                    case 10: {
                        codepoint = -2;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 32: 
                    case 33: 
                    case 34: 
                    case 35: 
                    case 36: 
                    case 37: 
                    case 38: 
                    case 39: 
                    case 40: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: 
                    case 45: 
                    case 46: 
                    case 47: 
                    case 58: 
                    case 59: 
                    case 60: 
                    case 61: 
                    case 62: 
                    case 63: 
                    case 64: 
                    case 91: 
                    case 92: 
                    case 93: 
                    case 94: 
                    case 95: 
                    case 96: 
                    case 123: 
                    case 124: 
                    case 125: 
                    case 126: {
                        codepoint = c1;
                        referenceOffset = i + 1;
                    }
                }
                if (codepoint == -1) {
                    if (c1 >= 48 && c1 <= 57 || c1 >= 65 && c1 <= 70 || c1 >= 97 && c1 <= 102) {
                        char cf;
                        int f;
                        for (f = i + 2; f < i + 7 && f < max && ((cf = text[f]) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f'); ++f) {
                        }
                        codepoint = CssUnescapeUtil.parseIntFromReference(text, i + 1, f, 16);
                        referenceOffset = f - 1;
                        if (f < max && text[f] == ' ') {
                            ++referenceOffset;
                        }
                    } else {
                        if (c1 == 13 || c1 == 12) {
                            ++i;
                            continue;
                        }
                        codepoint = c1;
                        referenceOffset = i + 1;
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
            if (codepoint == -2) continue;
            writer.write((char)codepoint);
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }
}

