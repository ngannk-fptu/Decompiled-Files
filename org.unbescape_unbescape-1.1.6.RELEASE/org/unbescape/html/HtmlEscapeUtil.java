/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.html;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.unbescape.html.HtmlEscapeLevel;
import org.unbescape.html.HtmlEscapeSymbols;
import org.unbescape.html.HtmlEscapeType;

final class HtmlEscapeUtil {
    private static final char REFERENCE_PREFIX = '&';
    private static final char REFERENCE_NUMERIC_PREFIX2 = '#';
    private static final char REFERENCE_HEXA_PREFIX3_UPPER = 'X';
    private static final char REFERENCE_HEXA_PREFIX3_LOWER = 'x';
    private static final char[] REFERENCE_DECIMAL_PREFIX = "&#".toCharArray();
    private static final char[] REFERENCE_HEXA_PREFIX = "&#x".toCharArray();
    private static final char REFERENCE_SUFFIX = ';';
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private HtmlEscapeUtil() {
    }

    static String escape(String text, HtmlEscapeType escapeType, HtmlEscapeLevel escapeLevel) {
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useHtml5 = escapeType.getUseHtml5();
        boolean useNCRs = escapeType.getUseNCRs();
        boolean useHexa = escapeType.getUseHexa();
        HtmlEscapeSymbols symbols = useHtml5 ? HtmlEscapeSymbols.HTML5_SYMBOLS : HtmlEscapeSymbols.HTML4_SYMBOLS;
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        for (int i = 0; i < max; ++i) {
            char c = text.charAt(i);
            if (c <= '\u007f' && level < symbols.ESCAPE_LEVELS[c] || c > '\u007f' && level < symbols.ESCAPE_LEVELS[128]) continue;
            int codepoint = Character.codePointAt(text, i);
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
            if (useNCRs) {
                Short ncrIndex;
                if (codepoint < 12287) {
                    short ncrIndex2 = symbols.NCRS_BY_CODEPOINT[codepoint];
                    if (ncrIndex2 != 0) {
                        strBuilder.append(symbols.SORTED_NCRS[ncrIndex2]);
                        continue;
                    }
                } else if (symbols.NCRS_BY_CODEPOINT_OVERFLOW != null && (ncrIndex = symbols.NCRS_BY_CODEPOINT_OVERFLOW.get(codepoint)) != null) {
                    strBuilder.append(symbols.SORTED_NCRS[ncrIndex]);
                    continue;
                }
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

    static void escape(Reader reader, Writer writer, HtmlEscapeType escapeType, HtmlEscapeLevel escapeLevel) throws IOException {
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useHtml5 = escapeType.getUseHtml5();
        boolean useNCRs = escapeType.getUseNCRs();
        boolean useHexa = escapeType.getUseHexa();
        HtmlEscapeSymbols symbols = useHtml5 ? HtmlEscapeSymbols.HTML5_SYMBOLS : HtmlEscapeSymbols.HTML4_SYMBOLS;
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            if (c1 <= 127 && level < symbols.ESCAPE_LEVELS[c1]) {
                writer.write(c1);
                continue;
            }
            if (c1 > 127 && level < symbols.ESCAPE_LEVELS[128]) {
                writer.write(c1);
                continue;
            }
            int codepoint = HtmlEscapeUtil.codePointAt((char)c1, (char)c2);
            if (Character.charCount(codepoint) > 1) {
                c1 = c2;
                c2 = reader.read();
            }
            if (useNCRs) {
                Short ncrIndex;
                if (codepoint < 12287) {
                    short ncrIndex2 = symbols.NCRS_BY_CODEPOINT[codepoint];
                    if (ncrIndex2 != 0) {
                        writer.write(symbols.SORTED_NCRS[ncrIndex2]);
                        continue;
                    }
                } else if (symbols.NCRS_BY_CODEPOINT_OVERFLOW != null && (ncrIndex = symbols.NCRS_BY_CODEPOINT_OVERFLOW.get(codepoint)) != null) {
                    writer.write(symbols.SORTED_NCRS[ncrIndex]);
                    continue;
                }
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

    static void escape(char[] text, int offset, int len, Writer writer, HtmlEscapeType escapeType, HtmlEscapeLevel escapeLevel) throws IOException {
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useHtml5 = escapeType.getUseHtml5();
        boolean useNCRs = escapeType.getUseNCRs();
        boolean useHexa = escapeType.getUseHexa();
        HtmlEscapeSymbols symbols = useHtml5 ? HtmlEscapeSymbols.HTML5_SYMBOLS : HtmlEscapeSymbols.HTML4_SYMBOLS;
        int max = offset + len;
        int readOffset = offset;
        for (int i = offset; i < max; ++i) {
            char c = text[i];
            if (c <= '\u007f' && level < symbols.ESCAPE_LEVELS[c]) continue;
            if (c > '\u007f') {
                if (level < symbols.ESCAPE_LEVELS[127 + 1]) continue;
            }
            int codepoint = Character.codePointAt(text, i);
            if (i - readOffset > 0) {
                writer.write(text, readOffset, i - readOffset);
            }
            if (Character.charCount(codepoint) > 1) {
                ++i;
            }
            readOffset = i + 1;
            if (useNCRs) {
                Short ncrIndex;
                if (codepoint < 12287) {
                    short ncrIndex2 = symbols.NCRS_BY_CODEPOINT[codepoint];
                    if (ncrIndex2 != 0) {
                        writer.write(symbols.SORTED_NCRS[ncrIndex2]);
                        continue;
                    }
                } else if (symbols.NCRS_BY_CODEPOINT_OVERFLOW != null && (ncrIndex = symbols.NCRS_BY_CODEPOINT_OVERFLOW.get(codepoint)) != null) {
                    writer.write(symbols.SORTED_NCRS[ncrIndex]);
                    continue;
                }
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

    static int translateIllFormedCodepoint(int codepoint) {
        switch (codepoint) {
            case 0: {
                return 65533;
            }
            case 128: {
                return 8364;
            }
            case 130: {
                return 8218;
            }
            case 131: {
                return 402;
            }
            case 132: {
                return 8222;
            }
            case 133: {
                return 8230;
            }
            case 134: {
                return 8224;
            }
            case 135: {
                return 8225;
            }
            case 136: {
                return 710;
            }
            case 137: {
                return 8240;
            }
            case 138: {
                return 352;
            }
            case 139: {
                return 8249;
            }
            case 140: {
                return 338;
            }
            case 142: {
                return 381;
            }
            case 145: {
                return 8216;
            }
            case 146: {
                return 8217;
            }
            case 147: {
                return 8220;
            }
            case 148: {
                return 8221;
            }
            case 149: {
                return 8226;
            }
            case 150: {
                return 8211;
            }
            case 151: {
                return 8212;
            }
            case 152: {
                return 732;
            }
            case 153: {
                return 8482;
            }
            case 154: {
                return 353;
            }
            case 155: {
                return 8250;
            }
            case 156: {
                return 339;
            }
            case 158: {
                return 382;
            }
            case 159: {
                return 376;
            }
        }
        if (codepoint >= 55296 && codepoint <= 57343) {
            return 65533;
        }
        if (codepoint > 0x10FFFF) {
            return 65533;
        }
        return codepoint;
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
            if ((result *= radix) < 0) {
                return 65533;
            }
            if ((result += n) >= 0) continue;
            return 65533;
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
            if ((result *= radix) < 0) {
                return 65533;
            }
            if ((result += n) >= 0) continue;
            return 65533;
        }
        return result;
    }

    static String unescape(String text) {
        if (text == null) {
            return null;
        }
        HtmlEscapeSymbols symbols = HtmlEscapeSymbols.HTML5_SYMBOLS;
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
                    if ((c2 == 'x' || c2 == 'X') && i + 3 < max) {
                        for (f = i + 3; f < max && ((cf = text.charAt(f)) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f'); ++f) {
                        }
                        if (f - (i + 3) <= 0) continue;
                        codepoint = HtmlEscapeUtil.parseIntFromReference(text, i + 3, f, 16);
                        referenceOffset = f - 1;
                        if (f < max && text.charAt(f) == ';') {
                            ++referenceOffset;
                        }
                        codepoint = HtmlEscapeUtil.translateIllFormedCodepoint(codepoint);
                    } else {
                        if (c2 < '0' || c2 > '9') continue;
                        for (f = i + 2; f < max && (cf = text.charAt(f)) >= '0' && cf <= '9'; ++f) {
                        }
                        if (f - (i + 2) <= 0) continue;
                        codepoint = HtmlEscapeUtil.parseIntFromReference(text, i + 2, f, 10);
                        referenceOffset = f - 1;
                        if (f < max && text.charAt(f) == ';') {
                            ++referenceOffset;
                        }
                        codepoint = HtmlEscapeUtil.translateIllFormedCodepoint(codepoint);
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
                    if ((ncrPosition = HtmlEscapeSymbols.binarySearch(symbols.SORTED_NCRS, text, i, f)) >= 0) {
                        codepoint = symbols.SORTED_CODEPOINTS[ncrPosition];
                    } else {
                        if (ncrPosition == Integer.MIN_VALUE) continue;
                        if (ncrPosition < -10) {
                            int partialIndex = -1 * (ncrPosition + 10);
                            char[] partialMatch = symbols.SORTED_NCRS[partialIndex];
                            codepoint = symbols.SORTED_CODEPOINTS[partialIndex];
                            f -= f - i - partialMatch.length;
                        } else {
                            throw new RuntimeException("Invalid unescape codepoint after search: " + ncrPosition);
                        }
                    }
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
            if (codepoint < 0) {
                int[] codepoints = symbols.DOUBLE_CODEPOINTS[-1 * codepoint - 1];
                if (codepoints[0] > 65535) {
                    strBuilder.append(Character.toChars(codepoints[0]));
                } else {
                    strBuilder.append((char)codepoints[0]);
                }
                if (codepoints[1] > 65535) {
                    strBuilder.append(Character.toChars(codepoints[1]));
                    continue;
                }
                strBuilder.append((char)codepoints[1]);
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
    static void unescape(Reader reader, Writer writer) throws IOException {
        if (reader == null) {
            return;
        }
        HtmlEscapeSymbols symbols = HtmlEscapeSymbols.HTML5_SYMBOLS;
        char[] escapes = new char[10];
        int escapei = 0;
        int c2 = reader.read();
        while (true) {
            int codepoint;
            block37: {
                int ce;
                int c1;
                block40: {
                    int c3;
                    block35: {
                        block39: {
                            block38: {
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
                                if (c1 != 38) break block37;
                                if (c2 == 32 || c2 == 10 || c2 == 9 || c2 == 12 || c2 == 60 || c2 == 38) {
                                    writer.write(c1);
                                    continue;
                                }
                                if (c2 != 35) break block38;
                                c3 = reader.read();
                                if (c3 < 0) {
                                    writer.write(c1);
                                    writer.write(c2);
                                    c1 = c2;
                                    c2 = c3;
                                    continue;
                                }
                                if (c3 == 120 || c3 == 88) {
                                    ce = reader.read();
                                    break block39;
                                } else if (c3 >= 48 && c3 <= 57) {
                                    ce = c3;
                                    break block35;
                                } else {
                                    writer.write(c1);
                                    writer.write(c2);
                                    c1 = c2;
                                    c2 = c3;
                                    continue;
                                }
                            }
                            ce = c2;
                            break block40;
                        }
                        while (ce >= 0 && (ce >= 48 && ce <= 57 || ce >= 65 && ce <= 70 || ce >= 97 && ce <= 102)) {
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
                            writer.write(c2);
                            writer.write(c3);
                            c1 = c3;
                            c2 = ce;
                            continue;
                        }
                        c1 = escapes[escapei - 1];
                        c2 = ce;
                        codepoint = HtmlEscapeUtil.parseIntFromReference(escapes, 0, escapei, 16);
                        if (c2 == 59) {
                            c1 = c2;
                            c2 = reader.read();
                        }
                        codepoint = HtmlEscapeUtil.translateIllFormedCodepoint(codepoint);
                        escapei = 0;
                        break block37;
                    }
                    while (ce >= 0 && ce >= 48 && ce <= 57) {
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
                        writer.write(c2);
                        c1 = c2;
                        c2 = c3;
                        continue;
                    }
                    c1 = escapes[escapei - 1];
                    c2 = ce;
                    codepoint = HtmlEscapeUtil.parseIntFromReference(escapes, 0, escapei, 10);
                    if (c2 == 59) {
                        c1 = c2;
                        c2 = reader.read();
                    }
                    codepoint = HtmlEscapeUtil.translateIllFormedCodepoint(codepoint);
                    escapei = 0;
                    break block37;
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
                int ncrPosition = HtmlEscapeSymbols.binarySearch(symbols.SORTED_NCRS, escapes, 0, escapei);
                if (ncrPosition >= 0) {
                    codepoint = symbols.SORTED_CODEPOINTS[ncrPosition];
                    escapei = 0;
                } else {
                    if (ncrPosition == Integer.MIN_VALUE) {
                        writer.write(escapes, 0, escapei);
                        continue;
                    }
                    if (ncrPosition >= -10) {
                        throw new RuntimeException("Invalid unescape codepoint after search: " + ncrPosition);
                    }
                    int partialIndex = -1 * (ncrPosition + 10);
                    char[] partialMatch = symbols.SORTED_NCRS[partialIndex];
                    codepoint = symbols.SORTED_CODEPOINTS[partialIndex];
                    System.arraycopy(escapes, partialMatch.length, escapes, 0, escapei - partialMatch.length);
                    escapei -= partialMatch.length;
                }
            }
            if (codepoint > 65535) {
                writer.write(Character.toChars(codepoint));
            } else if (codepoint < 0) {
                int[] codepoints = symbols.DOUBLE_CODEPOINTS[-1 * codepoint - 1];
                if (codepoints[0] > 65535) {
                    writer.write(Character.toChars(codepoints[0]));
                } else {
                    writer.write((char)codepoints[0]);
                }
                if (codepoints[1] > 65535) {
                    writer.write(Character.toChars(codepoints[1]));
                } else {
                    writer.write((char)codepoints[1]);
                }
            } else {
                writer.write((char)codepoint);
            }
            if (escapei <= 0) continue;
            writer.write(escapes, 0, escapei);
            escapei = 0;
        }
    }

    static void unescape(char[] text, int offset, int len, Writer writer) throws IOException {
        if (text == null) {
            return;
        }
        HtmlEscapeSymbols symbols = HtmlEscapeSymbols.HTML5_SYMBOLS;
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
                    if ((c2 == 'x' || c2 == 'X') && i + 3 < max) {
                        for (f = i + 3; f < max && ((cf = text[f]) >= '0' && cf <= '9' || cf >= 'A' && cf <= 'F' || cf >= 'a' && cf <= 'f'); ++f) {
                        }
                        if (f - (i + 3) <= 0) continue;
                        codepoint = HtmlEscapeUtil.parseIntFromReference(text, i + 3, f, 16);
                        referenceOffset = f - 1;
                        if (f < max && text[f] == ';') {
                            ++referenceOffset;
                        }
                        codepoint = HtmlEscapeUtil.translateIllFormedCodepoint(codepoint);
                    } else {
                        if (c2 < '0' || c2 > '9') continue;
                        for (f = i + 2; f < max && (cf = text[f]) >= '0' && cf <= '9'; ++f) {
                        }
                        if (f - (i + 2) <= 0) continue;
                        codepoint = HtmlEscapeUtil.parseIntFromReference(text, i + 2, f, 10);
                        referenceOffset = f - 1;
                        if (f < max && text[f] == ';') {
                            ++referenceOffset;
                        }
                        codepoint = HtmlEscapeUtil.translateIllFormedCodepoint(codepoint);
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
                    if ((ncrPosition = HtmlEscapeSymbols.binarySearch(symbols.SORTED_NCRS, text, i, f)) >= 0) {
                        codepoint = symbols.SORTED_CODEPOINTS[ncrPosition];
                    } else {
                        if (ncrPosition == Integer.MIN_VALUE) continue;
                        if (ncrPosition < -10) {
                            int partialIndex = -1 * (ncrPosition + 10);
                            char[] partialMatch = symbols.SORTED_NCRS[partialIndex];
                            codepoint = symbols.SORTED_CODEPOINTS[partialIndex];
                            f -= f - i - partialMatch.length;
                        } else {
                            throw new RuntimeException("Invalid unescape codepoint after search: " + ncrPosition);
                        }
                    }
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
            if (codepoint < 0) {
                int[] codepoints = symbols.DOUBLE_CODEPOINTS[-1 * codepoint - 1];
                if (codepoints[0] > 65535) {
                    writer.write(Character.toChars(codepoints[0]));
                } else {
                    writer.write((char)codepoints[0]);
                }
                if (codepoints[1] > 65535) {
                    writer.write(Character.toChars(codepoints[1]));
                    continue;
                }
                writer.write((char)codepoints[1]);
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

