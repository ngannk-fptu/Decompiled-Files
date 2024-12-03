/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import org.unbescape.xml.XmlEscapeLevel;
import org.unbescape.xml.XmlEscapeSymbols;
import org.unbescape.xml.XmlEscapeType;

final class XmlEscapeUtil {
    private static final char REFERENCE_PREFIX = '&';
    private static final char REFERENCE_NUMERIC_PREFIX2 = '#';
    private static final char REFERENCE_HEXA_PREFIX3 = 'x';
    private static final char[] REFERENCE_DECIMAL_PREFIX = "&#".toCharArray();
    private static final char[] REFERENCE_HEXA_PREFIX = "&#x".toCharArray();
    private static final char REFERENCE_SUFFIX = ';';
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private XmlEscapeUtil() {
    }

    static String escape(String text, XmlEscapeSymbols symbols, XmlEscapeType escapeType, XmlEscapeLevel escapeLevel) {
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useCERs = escapeType.getUseCERs();
        boolean useHexa = escapeType.getUseHexa();
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        for (int i = 0; i < max; ++i) {
            int codepointIndex;
            int codepoint = Character.codePointAt(text, i);
            boolean codepointValid = symbols.CODEPOINT_VALIDATOR.isValid(codepoint);
            if (codepoint <= 159 && level < symbols.ESCAPE_LEVELS[codepoint] && codepointValid) continue;
            if (codepoint > 159 && level < symbols.ESCAPE_LEVELS[160] && codepointValid) {
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
            if (!codepointValid) continue;
            if (useCERs && (codepointIndex = Arrays.binarySearch(symbols.SORTED_CODEPOINTS, codepoint)) >= 0) {
                strBuilder.append(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex]);
                continue;
            }
            if (useHexa) {
                strBuilder.append(REFERENCE_HEXA_PREFIX);
                strBuilder.append(Integer.toHexString(codepoint));
            } else {
                strBuilder.append(REFERENCE_DECIMAL_PREFIX);
                strBuilder.append(String.valueOf(codepoint));
            }
            strBuilder.append(';');
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void escape(Reader reader, Writer writer, XmlEscapeSymbols symbols, XmlEscapeType escapeType, XmlEscapeLevel escapeLevel) throws IOException {
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useCERs = escapeType.getUseCERs();
        boolean useHexa = escapeType.getUseHexa();
        int c2 = reader.read();
        while (c2 >= 0) {
            int codepointIndex;
            int c1 = c2;
            c2 = reader.read();
            int codepoint = XmlEscapeUtil.codePointAt((char)c1, (char)c2);
            boolean codepointValid = symbols.CODEPOINT_VALIDATOR.isValid(codepoint);
            if (codepoint <= 159 && level < symbols.ESCAPE_LEVELS[codepoint] && codepointValid) {
                writer.write(c1);
                continue;
            }
            if (codepoint > 159 && level < symbols.ESCAPE_LEVELS[160] && codepointValid) {
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
            if (!codepointValid) continue;
            if (useCERs && (codepointIndex = Arrays.binarySearch(symbols.SORTED_CODEPOINTS, codepoint)) >= 0) {
                writer.write(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex]);
                continue;
            }
            if (useHexa) {
                writer.write(REFERENCE_HEXA_PREFIX);
                writer.write(Integer.toHexString(codepoint));
            } else {
                writer.write(REFERENCE_DECIMAL_PREFIX);
                writer.write(String.valueOf(codepoint));
            }
            writer.write(59);
        }
    }

    static void escape(char[] text, int offset, int len, Writer writer, XmlEscapeSymbols symbols, XmlEscapeType escapeType, XmlEscapeLevel escapeLevel) throws IOException {
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useCERs = escapeType.getUseCERs();
        boolean useHexa = escapeType.getUseHexa();
        int max = offset + len;
        int readOffset = offset;
        for (int i = offset; i < max; ++i) {
            int codepointIndex;
            int codepoint = Character.codePointAt(text, i);
            boolean codepointValid = symbols.CODEPOINT_VALIDATOR.isValid(codepoint);
            if (codepoint <= 159 && level < symbols.ESCAPE_LEVELS[codepoint] && codepointValid) continue;
            if (codepoint > 159 && level < symbols.ESCAPE_LEVELS[160] && codepointValid) {
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
            if (!codepointValid) continue;
            if (useCERs && (codepointIndex = Arrays.binarySearch(symbols.SORTED_CODEPOINTS, codepoint)) >= 0) {
                writer.write(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex]);
                continue;
            }
            if (useHexa) {
                writer.write(REFERENCE_HEXA_PREFIX);
                writer.write(Integer.toHexString(codepoint));
            } else {
                writer.write(REFERENCE_DECIMAL_PREFIX);
                writer.write(String.valueOf(codepoint));
            }
            writer.write(59);
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

    static String unescape(String text, XmlEscapeSymbols symbols) {
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
            if (c != '&' || i + 1 >= max) continue;
            int codepoint = 0;
            if (c == '&') {
                char c1 = text.charAt(i + 1);
                if (c1 == ' ' || c1 == '\n' || c1 == '\t' || c1 == '\f' || c1 == '<' || c1 == '&') continue;
                if (c1 == '#') {
                    char cf;
                    int f;
                    if (i + 2 >= max) continue;
                    char c2 = text.charAt(i + 2);
                    if (c2 == 'x' && i + 3 < max) {
                        for (f = i + 3; f < max && ((cf = text.charAt(f)) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f'); ++f) {
                        }
                        if (f - (i + 3) <= 0 || f >= max || text.charAt(f) != ';') continue;
                        codepoint = XmlEscapeUtil.parseIntFromReference(text, i + 3, ++f - 1, 16);
                        referenceOffset = f - 1;
                    } else {
                        if (c2 < '0' || c2 > '9') continue;
                        for (f = i + 2; f < max && (cf = text.charAt(f)) >= '0' && cf <= '9'; ++f) {
                        }
                        if (f - (i + 2) <= 0 || f >= max || text.charAt(f) != ';') continue;
                        codepoint = XmlEscapeUtil.parseIntFromReference(text, i + 2, ++f - 1, 10);
                        referenceOffset = f - 1;
                    }
                } else {
                    int ncrPosition;
                    char cf;
                    int f;
                    for (f = i + 1; f < max && ((cf = text.charAt(f)) >= 'a' && cf <= 'z' || cf >= 'A' && cf <= 'Z' || cf >= '0' && cf <= '9'); ++f) {
                    }
                    if (f - (i + 1) <= 0) continue;
                    if (f < max && text.charAt(f) == ';') {
                        ++f;
                    }
                    if ((ncrPosition = XmlEscapeSymbols.binarySearch(symbols.SORTED_CERS, text, i, f)) < 0) continue;
                    codepoint = symbols.SORTED_CODEPOINTS_BY_CER[ncrPosition];
                    referenceOffset = f - 1;
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

    /*
     * Enabled aggressive block sorting
     */
    static void unescape(Reader reader, Writer writer, XmlEscapeSymbols symbols) throws IOException {
        if (reader == null) {
            return;
        }
        char[] escapes = new char[10];
        int escapei = 0;
        int c2 = reader.read();
        while (true) {
            int codepoint;
            block27: {
                int ce;
                int c1;
                block30: {
                    char[] newEscapes;
                    int c3;
                    block26: {
                        block29: {
                            block28: {
                                if (c2 < 0) {
                                    return;
                                }
                                c1 = c2;
                                c2 = reader.read();
                                escapei = 0;
                                if (c1 != 38 || c2 < 0) {
                                    writer.write(c1);
                                    continue;
                                }
                                codepoint = 0;
                                if (c1 != 38) break block27;
                                if (c2 == 32 || c2 == 10 || c2 == 9 || c2 == 12 || c2 == 60 || c2 == 38) {
                                    writer.write(c1);
                                    continue;
                                }
                                if (c2 != 35) break block28;
                                c3 = reader.read();
                                if (c3 < 0) {
                                    writer.write(c1);
                                    writer.write(c2);
                                    c1 = c2;
                                    c2 = c3;
                                    continue;
                                }
                                if (c3 == 120) {
                                    ce = reader.read();
                                    break block29;
                                } else if (c3 >= 48 && c3 <= 57) {
                                    ce = c3;
                                    break block26;
                                } else {
                                    writer.write(c1);
                                    writer.write(c2);
                                    c1 = c2;
                                    c2 = c3;
                                    continue;
                                }
                            }
                            ce = c2;
                            break block30;
                        }
                        while (ce >= 0 && (ce >= 48 && ce <= 57 || ce >= 65 && ce <= 70 || ce >= 97 && ce <= 102)) {
                            if (escapei == escapes.length) {
                                newEscapes = new char[escapes.length + 4];
                                System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                                escapes = newEscapes;
                            }
                            escapes[escapei] = (char)ce;
                            ce = reader.read();
                            ++escapei;
                        }
                        if (escapei == 0) {
                            writer.write(c1);
                            writer.write(c2);
                            writer.write(c3);
                            c1 = c3;
                            c2 = ce;
                            continue;
                        }
                        if (ce != 59) {
                            writer.write(c1);
                            writer.write(c2);
                            writer.write(c3);
                            writer.write(escapes, 0, escapei);
                            c1 = escapes[escapei - 1];
                            c2 = ce;
                            continue;
                        }
                        c1 = ce;
                        c2 = reader.read();
                        codepoint = XmlEscapeUtil.parseIntFromReference(escapes, 0, escapei, 16);
                        break block27;
                    }
                    while (ce >= 0 && ce >= 48 && ce <= 57) {
                        if (escapei == escapes.length) {
                            newEscapes = new char[escapes.length + 4];
                            System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                            escapes = newEscapes;
                        }
                        escapes[escapei] = (char)ce;
                        ce = reader.read();
                        ++escapei;
                    }
                    if (escapei == 0) {
                        writer.write(c1);
                        writer.write(c2);
                        c1 = c2;
                        c2 = c3;
                        continue;
                    }
                    if (ce != 59) {
                        writer.write(c1);
                        writer.write(c2);
                        writer.write(escapes, 0, escapei);
                        c1 = escapes[escapei - 1];
                        c2 = ce;
                        continue;
                    }
                    c1 = ce;
                    c2 = reader.read();
                    codepoint = XmlEscapeUtil.parseIntFromReference(escapes, 0, escapei, 10);
                    break block27;
                }
                while (ce >= 0 && (ce >= 48 && ce <= 57 || ce >= 65 && ce <= 90 || ce >= 97 && ce <= 122)) {
                    if (escapei == escapes.length) {
                        char[] newEscapes = new char[escapes.length + 4];
                        System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                        escapes = newEscapes;
                    }
                    escapes[escapei] = (char)ce;
                    ce = reader.read();
                    ++escapei;
                }
                if (escapei == 0) {
                    writer.write(c1);
                    continue;
                }
                if (escapei + 2 >= escapes.length) {
                    char[] newEscapes = new char[escapes.length + 4];
                    System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                    escapes = newEscapes;
                }
                System.arraycopy(escapes, 0, escapes, 1, escapei);
                escapes[0] = (char)c1;
                ++escapei;
                if (ce == 59) {
                    escapes[escapei++] = (char)ce;
                    ce = reader.read();
                }
                c1 = escapes[escapei - 1];
                c2 = ce;
                int ncrPosition = XmlEscapeSymbols.binarySearch(symbols.SORTED_CERS, escapes, 0, escapei);
                if (ncrPosition >= 0) {
                    codepoint = symbols.SORTED_CODEPOINTS_BY_CER[ncrPosition];
                } else {
                    writer.write(escapes, 0, escapei);
                    continue;
                }
            }
            if (codepoint > 65535) {
                writer.write(Character.toChars(codepoint));
                continue;
            }
            writer.write((char)codepoint);
        }
    }

    static void unescape(char[] text, int offset, int len, Writer writer, XmlEscapeSymbols symbols) throws IOException {
        if (text == null) {
            return;
        }
        int max = offset + len;
        int readOffset = offset;
        int referenceOffset = offset;
        for (int i = offset; i < max; ++i) {
            char c = text[i];
            if (c != '&' || i + 1 >= max) continue;
            int codepoint = 0;
            if (c == '&') {
                char c1 = text[i + 1];
                if (c1 == ' ' || c1 == '\n' || c1 == '\t' || c1 == '\f' || c1 == '<' || c1 == '&') continue;
                if (c1 == '#') {
                    char cf;
                    int f;
                    if (i + 2 >= max) continue;
                    char c2 = text[i + 2];
                    if (c2 == 'x' && i + 3 < max) {
                        for (f = i + 3; f < max && ((cf = text[f]) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f'); ++f) {
                        }
                        if (f - (i + 3) <= 0 || f >= max || text[f] != ';') continue;
                        codepoint = XmlEscapeUtil.parseIntFromReference(text, i + 3, ++f - 1, 16);
                        referenceOffset = f - 1;
                    } else {
                        if (c2 < '0' || c2 > '9') continue;
                        for (f = i + 2; f < max && (cf = text[f]) >= '0' && cf <= '9'; ++f) {
                        }
                        if (f - (i + 2) <= 0 || f >= max || text[f] != ';') continue;
                        codepoint = XmlEscapeUtil.parseIntFromReference(text, i + 2, ++f - 1, 10);
                        referenceOffset = f - 1;
                    }
                } else {
                    int ncrPosition;
                    char cf;
                    int f;
                    for (f = i + 1; f < max && ((cf = text[f]) >= 'a' && cf <= 'z' || cf >= 'A' && cf <= 'Z' || cf >= '0' && cf <= '9'); ++f) {
                    }
                    if (f - (i + 1) <= 0) continue;
                    if (f < max && text[f] == ';') {
                        ++f;
                    }
                    if ((ncrPosition = XmlEscapeSymbols.binarySearch(symbols.SORTED_CERS, text, i, f)) < 0) continue;
                    codepoint = symbols.SORTED_CODEPOINTS_BY_CER[ncrPosition];
                    referenceOffset = f - 1;
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
}

