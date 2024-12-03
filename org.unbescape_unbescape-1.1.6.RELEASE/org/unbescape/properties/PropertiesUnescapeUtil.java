/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.properties;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

final class PropertiesUnescapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static final char ESCAPE_UHEXA_PREFIX2 = 'u';
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private PropertiesUnescapeUtil() {
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
                    case '\\': {
                        codepoint = 92;
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
                        codepoint = PropertiesUnescapeUtil.parseIntFromReference(text, i + 2, f, 16);
                        referenceOffset = f - 1;
                    } else {
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
        char[] escapes = new char[4];
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
                    case 92: {
                        codepoint = 92;
                        c1 = c2;
                        c2 = reader.read();
                    }
                }
                if (codepoint == -1) {
                    if (c2 == 117) {
                        int escapei;
                        int ce = reader.read();
                        for (escapei = 0; ce >= 0 && escapei < 4 && (ce >= 48 && ce <= 57 || ce >= 65 && ce <= 70 || ce >= 97 && ce <= 102); ++escapei) {
                            escapes[escapei] = (char)ce;
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
                        codepoint = PropertiesUnescapeUtil.parseIntFromReference(escapes, 0, 4, 16);
                    } else {
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
                    case 116: {
                        codepoint = 9;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 110: {
                        codepoint = 10;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 102: {
                        codepoint = 12;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 114: {
                        codepoint = 13;
                        referenceOffset = i + 1;
                        break;
                    }
                    case 92: {
                        codepoint = 92;
                        referenceOffset = i + 1;
                    }
                }
                if (codepoint == -1) {
                    if (c1 == 117) {
                        char cf;
                        int f;
                        for (f = i + 2; f < i + 6 && f < max && ((cf = text[f]) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f'); ++f) {
                        }
                        if (f - (i + 2) < 4) {
                            ++i;
                            continue;
                        }
                        codepoint = PropertiesUnescapeUtil.parseIntFromReference(text, i + 2, f, 16);
                        referenceOffset = f - 1;
                    } else {
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
            writer.write((char)codepoint);
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }
}

