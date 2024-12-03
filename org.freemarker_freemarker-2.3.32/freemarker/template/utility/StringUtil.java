/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.ext.dom._ExtDomApi;
import freemarker.template.Version;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.NullArgumentException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringUtil {
    private static final char[] ESCAPES = StringUtil.createEscapes();
    private static final char[] LT = new char[]{'&', 'l', 't', ';'};
    private static final char[] GT = new char[]{'&', 'g', 't', ';'};
    private static final char[] AMP = new char[]{'&', 'a', 'm', 'p', ';'};
    private static final char[] QUOT = new char[]{'&', 'q', 'u', 'o', 't', ';'};
    private static final char[] HTML_APOS = new char[]{'&', '#', '3', '9', ';'};
    private static final char[] XML_APOS = new char[]{'&', 'a', 'p', 'o', 's', ';'};
    private static final int NO_ESC = 0;
    private static final int ESC_HEXA = 1;
    private static final int ESC_BACKSLASH = 3;

    @Deprecated
    public static String HTMLEnc(String s) {
        return StringUtil.XMLEncNA(s);
    }

    public static String XMLEnc(String s) {
        return StringUtil.XMLOrHTMLEnc(s, true, true, XML_APOS);
    }

    public static void XMLEnc(String s, Writer out) throws IOException {
        StringUtil.XMLOrHTMLEnc(s, XML_APOS, out);
    }

    public static String XHTMLEnc(String s) {
        return StringUtil.XMLOrHTMLEnc(s, true, true, HTML_APOS);
    }

    public static void XHTMLEnc(String s, Writer out) throws IOException {
        StringUtil.XMLOrHTMLEnc(s, HTML_APOS, out);
    }

    /*
     * Enabled aggressive block sorting
     */
    private static String XMLOrHTMLEnc(String s, boolean escGT, boolean escQuot, char[] apos) {
        int ln = s.length();
        int firstEscIdx = -1;
        int lastEscIdx = 0;
        int plusOutLn = 0;
        block14: for (int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '<': {
                    plusOutLn += LT.length - 1;
                    break;
                }
                case '>': {
                    if (!escGT && !StringUtil.maybeCDataEndGT(s, i)) continue block14;
                    plusOutLn += GT.length - 1;
                    break;
                }
                case '&': {
                    plusOutLn += AMP.length - 1;
                    break;
                }
                case '\"': {
                    if (!escQuot) continue block14;
                    plusOutLn += QUOT.length - 1;
                    break;
                }
                case '\'': {
                    if (apos == null) continue block14;
                    plusOutLn += apos.length - 1;
                    break;
                }
                default: {
                    continue block14;
                }
            }
            if (firstEscIdx == -1) {
                firstEscIdx = i;
            }
            lastEscIdx = i;
        }
        if (firstEscIdx == -1) {
            return s;
        }
        char[] esced = new char[ln + plusOutLn];
        if (firstEscIdx != 0) {
            s.getChars(0, firstEscIdx, esced, 0);
        }
        int dst = firstEscIdx;
        block15: for (int i = firstEscIdx; i <= lastEscIdx; ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '<': {
                    dst = StringUtil.shortArrayCopy(LT, esced, dst);
                    continue block15;
                }
                case '>': {
                    if (!escGT && !StringUtil.maybeCDataEndGT(s, i)) break;
                    dst = StringUtil.shortArrayCopy(GT, esced, dst);
                    continue block15;
                }
                case '&': {
                    dst = StringUtil.shortArrayCopy(AMP, esced, dst);
                    continue block15;
                }
                case '\"': {
                    if (!escQuot) break;
                    dst = StringUtil.shortArrayCopy(QUOT, esced, dst);
                    continue block15;
                }
                case '\'': {
                    if (apos == null) break;
                    dst = StringUtil.shortArrayCopy(apos, esced, dst);
                    continue block15;
                }
            }
            esced[dst++] = c;
        }
        if (lastEscIdx != ln - 1) {
            s.getChars(lastEscIdx + 1, ln, esced, dst);
        }
        return String.valueOf(esced);
    }

    private static boolean maybeCDataEndGT(String s, int i) {
        if (i == 0) {
            return true;
        }
        if (s.charAt(i - 1) != ']') {
            return false;
        }
        return i == 1 || s.charAt(i - 2) == ']';
    }

    private static void XMLOrHTMLEnc(String s, char[] apos, Writer out) throws IOException {
        int writtenEnd = 0;
        int ln = s.length();
        block6: for (int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            if (c != '<' && c != '>' && c != '&' && c != '\"' && c != '\'') continue;
            int flushLn = i - writtenEnd;
            if (flushLn != 0) {
                out.write(s, writtenEnd, flushLn);
            }
            writtenEnd = i + 1;
            switch (c) {
                case '<': {
                    out.write(LT);
                    continue block6;
                }
                case '>': {
                    out.write(GT);
                    continue block6;
                }
                case '&': {
                    out.write(AMP);
                    continue block6;
                }
                case '\"': {
                    out.write(QUOT);
                    continue block6;
                }
                default: {
                    out.write(apos);
                }
            }
        }
        if (writtenEnd < ln) {
            out.write(s, writtenEnd, ln - writtenEnd);
        }
    }

    private static int shortArrayCopy(char[] src, char[] dst, int dstOffset) {
        for (char dst[dstOffset++] : src) {
        }
        return dstOffset;
    }

    public static String XMLEncNA(String s) {
        return StringUtil.XMLOrHTMLEnc(s, true, true, null);
    }

    public static String XMLEncQAttr(String s) {
        return StringUtil.XMLOrHTMLEnc(s, false, true, null);
    }

    public static String XMLEncNQG(String s) {
        return StringUtil.XMLOrHTMLEnc(s, false, false, null);
    }

    public static String RTFEnc(String s) {
        int ln = s.length();
        int firstEscIdx = -1;
        int lastEscIdx = 0;
        int plusOutLn = 0;
        for (int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            if (c != '{' && c != '}' && c != '\\') continue;
            if (firstEscIdx == -1) {
                firstEscIdx = i;
            }
            lastEscIdx = i;
            ++plusOutLn;
        }
        if (firstEscIdx == -1) {
            return s;
        }
        char[] esced = new char[ln + plusOutLn];
        if (firstEscIdx != 0) {
            s.getChars(0, firstEscIdx, esced, 0);
        }
        int dst = firstEscIdx;
        for (int i = firstEscIdx; i <= lastEscIdx; ++i) {
            char c = s.charAt(i);
            if (c == '{' || c == '}' || c == '\\') {
                esced[dst++] = 92;
            }
            esced[dst++] = c;
        }
        if (lastEscIdx != ln - 1) {
            s.getChars(lastEscIdx + 1, ln, esced, dst);
        }
        return String.valueOf(esced);
    }

    public static void RTFEnc(String s, Writer out) throws IOException {
        int writtenEnd = 0;
        int ln = s.length();
        for (int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            if (c != '{' && c != '}' && c != '\\') continue;
            int flushLn = i - writtenEnd;
            if (flushLn != 0) {
                out.write(s, writtenEnd, flushLn);
            }
            out.write(92);
            writtenEnd = i;
        }
        if (writtenEnd < ln) {
            out.write(s, writtenEnd, ln - writtenEnd);
        }
    }

    public static String URLEnc(String s, String charset) throws UnsupportedEncodingException {
        return StringUtil.URLEnc(s, charset, false);
    }

    public static String URLPathEnc(String s, String charset) throws UnsupportedEncodingException {
        return StringUtil.URLEnc(s, charset, true);
    }

    private static String URLEnc(String s, String charset, boolean keepSlash) throws UnsupportedEncodingException {
        char c;
        int i;
        int ln = s.length();
        for (i = 0; i < ln && StringUtil.safeInURL(c = s.charAt(i), keepSlash); ++i) {
        }
        if (i == ln) {
            return s;
        }
        StringBuilder b = new StringBuilder(ln + ln / 3 + 2);
        b.append(s.substring(0, i));
        int encStart = i++;
        while (i < ln) {
            char c2 = s.charAt(i);
            if (StringUtil.safeInURL(c2, keepSlash)) {
                if (encStart != -1) {
                    byte[] o = s.substring(encStart, i).getBytes(charset);
                    for (int j = 0; j < o.length; ++j) {
                        b.append('%');
                        byte bc = o[j];
                        int c1 = bc & 0xF;
                        int c22 = bc >> 4 & 0xF;
                        b.append((char)(c22 < 10 ? c22 + 48 : c22 - 10 + 65));
                        b.append((char)(c1 < 10 ? c1 + 48 : c1 - 10 + 65));
                    }
                    encStart = -1;
                }
                b.append(c2);
            } else if (encStart == -1) {
                encStart = i;
            }
            ++i;
        }
        if (encStart != -1) {
            byte[] o = s.substring(encStart, i).getBytes(charset);
            for (int j = 0; j < o.length; ++j) {
                b.append('%');
                byte bc = o[j];
                int c1 = bc & 0xF;
                int c2 = bc >> 4 & 0xF;
                b.append((char)(c2 < 10 ? c2 + 48 : c2 - 10 + 65));
                b.append((char)(c1 < 10 ? c1 + 48 : c1 - 10 + 65));
            }
        }
        return b.toString();
    }

    private static boolean safeInURL(char c, boolean keepSlash) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == '.' || c == '!' || c == '~' || c >= '\'' && c <= '*' || keepSlash && c == '/';
    }

    private static char[] createEscapes() {
        char[] escapes = new char[93];
        for (int i = 0; i < 32; ++i) {
            escapes[i] = '\u0001';
        }
        escapes[92] = 92;
        escapes[39] = 39;
        escapes[34] = 34;
        escapes[60] = 108;
        escapes[62] = 103;
        escapes[38] = 97;
        escapes[8] = 98;
        escapes[9] = 116;
        escapes[10] = 110;
        escapes[12] = 102;
        escapes[13] = 114;
        return escapes;
    }

    public static String FTLStringLiteralEnc(String s, char quotation) {
        return StringUtil.FTLStringLiteralEnc(s, quotation, false);
    }

    public static String FTLStringLiteralEnc(String s) {
        return StringUtil.FTLStringLiteralEnc(s, '\u0000', false);
    }

    private static String FTLStringLiteralEnc(String s, char quotation, boolean addQuotation) {
        int otherQuotation;
        int ln = s.length();
        if (quotation == '\u0000') {
            otherQuotation = 0;
        } else if (quotation == '\"') {
            otherQuotation = 39;
        } else if (quotation == '\'') {
            otherQuotation = 34;
        } else {
            throw new IllegalArgumentException("Unsupported quotation character: " + quotation);
        }
        int escLn = ESCAPES.length;
        StringBuilder buf = null;
        for (int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            char escape = c == '=' ? (i > 0 && s.charAt(i - 1) == '[' ? (char)'=' : '\u0000') : (c < escLn ? ESCAPES[c] : (c == '{' && i > 0 && StringUtil.isInterpolationStart(s.charAt(i - 1)) ? (char)'{' : '\u0000'));
            if (escape == '\u0000' || escape == otherQuotation) {
                if (buf == null) continue;
                buf.append(c);
                continue;
            }
            if (buf == null) {
                buf = new StringBuilder(s.length() + 4 + (addQuotation ? 2 : 0));
                if (addQuotation) {
                    buf.append(quotation);
                }
                buf.append(s.substring(0, i));
            }
            if (escape == '\u0001') {
                buf.append("\\x00");
                int c2 = c >> 4 & 0xF;
                c = (char)(c & 0xF);
                buf.append((char)(c2 < 10 ? c2 + 48 : c2 - 10 + 65));
                buf.append((char)(c < '\n' ? c + 48 : c - 10 + 65));
                continue;
            }
            buf.append('\\');
            buf.append(escape);
        }
        if (buf == null) {
            return addQuotation ? quotation + s + quotation : s;
        }
        if (addQuotation) {
            buf.append(quotation);
        }
        return buf.toString();
    }

    private static boolean isInterpolationStart(char c) {
        return c == '$' || c == '#';
    }

    public static String FTLStringLiteralDec(String s) throws ParseException {
        int idx = s.indexOf(92);
        if (idx == -1) {
            return s;
        }
        int lidx = s.length() - 1;
        int bidx = 0;
        StringBuilder buf = new StringBuilder(lidx);
        do {
            buf.append(s.substring(bidx, idx));
            if (idx >= lidx) {
                throw new ParseException("The last character of string literal is backslash", 0, 0);
            }
            char c = s.charAt(idx + 1);
            switch (c) {
                case '\"': {
                    buf.append('\"');
                    bidx = idx + 2;
                    break;
                }
                case '\'': {
                    buf.append('\'');
                    bidx = idx + 2;
                    break;
                }
                case '\\': {
                    buf.append('\\');
                    bidx = idx + 2;
                    break;
                }
                case 'n': {
                    buf.append('\n');
                    bidx = idx + 2;
                    break;
                }
                case 'r': {
                    buf.append('\r');
                    bidx = idx + 2;
                    break;
                }
                case 't': {
                    buf.append('\t');
                    bidx = idx + 2;
                    break;
                }
                case 'f': {
                    buf.append('\f');
                    bidx = idx + 2;
                    break;
                }
                case 'b': {
                    buf.append('\b');
                    bidx = idx + 2;
                    break;
                }
                case 'g': {
                    buf.append('>');
                    bidx = idx + 2;
                    break;
                }
                case 'l': {
                    buf.append('<');
                    bidx = idx + 2;
                    break;
                }
                case 'a': {
                    buf.append('&');
                    bidx = idx + 2;
                    break;
                }
                case '=': 
                case '{': {
                    buf.append(c);
                    bidx = idx + 2;
                    break;
                }
                case 'x': {
                    int z;
                    int x = idx += 2;
                    int y = 0;
                    int n = z = lidx > idx + 3 ? idx + 3 : lidx;
                    while (idx <= z) {
                        char b = s.charAt(idx);
                        if (b >= '0' && b <= '9') {
                            y <<= 4;
                            y += b - 48;
                        } else if (b >= 'a' && b <= 'f') {
                            y <<= 4;
                            y += b - 97 + 10;
                        } else {
                            if (b < 'A' || b > 'F') break;
                            y <<= 4;
                            y += b - 65 + 10;
                        }
                        ++idx;
                    }
                    if (x >= idx) {
                        throw new ParseException("Invalid \\x escape in a string literal", 0, 0);
                    }
                    buf.append((char)y);
                    bidx = idx;
                    break;
                }
                default: {
                    throw new ParseException("Invalid escape sequence (\\" + c + ") in a string literal", 0, 0);
                }
            }
        } while ((idx = s.indexOf(92, bidx)) != -1);
        buf.append(s.substring(bidx));
        return buf.toString();
    }

    public static Locale deduceLocale(String input) {
        if (input == null) {
            return null;
        }
        Locale locale = Locale.getDefault();
        if (input.length() > 0 && input.charAt(0) == '\"') {
            input = input.substring(1, input.length() - 1);
        }
        StringTokenizer st = new StringTokenizer(input, ",_ ");
        String lang = "";
        String country = "";
        if (st.hasMoreTokens()) {
            lang = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            country = st.nextToken();
        }
        locale = !st.hasMoreTokens() ? new Locale(lang, country) : new Locale(lang, country, st.nextToken());
        return locale;
    }

    public static String capitalize(String s) {
        StringTokenizer st = new StringTokenizer(s, " \t\r\n", true);
        StringBuilder buf = new StringBuilder(s.length());
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            buf.append(tok.substring(0, 1).toUpperCase());
            buf.append(tok.substring(1).toLowerCase());
        }
        return buf.toString();
    }

    public static boolean getYesNo(String s) {
        if (s.startsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }
        if (s.equalsIgnoreCase("n") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase("f") || s.equalsIgnoreCase("false")) {
            return false;
        }
        if (s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("t") || s.equalsIgnoreCase("true")) {
            return true;
        }
        throw new IllegalArgumentException("Illegal boolean value: " + s);
    }

    public static String[] split(String s, char c) {
        int ln = s.length();
        int i = 0;
        int cnt = 1;
        while ((i = s.indexOf(c, i)) != -1) {
            ++cnt;
            ++i;
        }
        String[] res = new String[cnt];
        i = 0;
        int b = 0;
        while (b <= ln) {
            int e = s.indexOf(c, b);
            if (e == -1) {
                e = ln;
            }
            res[i++] = s.substring(b, e);
            b = e + 1;
        }
        return res;
    }

    public static String[] split(String s, String sep, boolean caseInsensitive) {
        int sepLn = sep.length();
        String convertedS = caseInsensitive ? s.toLowerCase() : s;
        int sLn = s.length();
        if (sepLn == 0) {
            String[] res = new String[sLn];
            for (int i = 0; i < sLn; ++i) {
                res[i] = String.valueOf(s.charAt(i));
            }
            return res;
        }
        String splitString = caseInsensitive ? sep.toLowerCase() : sep;
        int next = 0;
        int count = 1;
        while ((next = convertedS.indexOf(splitString, next)) != -1) {
            ++count;
            next += sepLn;
        }
        String[] res = new String[count];
        int dst = 0;
        int next2 = 0;
        while (next2 <= sLn) {
            int end = convertedS.indexOf(splitString, next2);
            if (end == -1) {
                end = sLn;
            }
            res[dst++] = s.substring(next2, end);
            next2 = end + sepLn;
        }
        return res;
    }

    public static String replace(String text, String oldSub, String newSub) {
        return StringUtil.replace(text, oldSub, newSub, false, false);
    }

    public static String replace(String text, String oldsub, String newsub, boolean caseInsensitive, boolean firstOnly) {
        int oln = oldsub.length();
        if (oln == 0) {
            int nln = newsub.length();
            if (nln == 0) {
                return text;
            }
            if (firstOnly) {
                return newsub + text;
            }
            int tln = text.length();
            StringBuilder buf = new StringBuilder(tln + (tln + 1) * nln);
            buf.append(newsub);
            for (int i = 0; i < tln; ++i) {
                buf.append(text.charAt(i));
                buf.append(newsub);
            }
            return buf.toString();
        }
        String input = caseInsensitive ? text.toLowerCase() : text;
        int e = input.indexOf(oldsub = caseInsensitive ? oldsub.toLowerCase() : oldsub);
        if (e == -1) {
            return text;
        }
        int b = 0;
        int tln = text.length();
        StringBuilder buf = new StringBuilder(tln + Math.max(newsub.length() - oln, 0) * 3);
        do {
            buf.append(text.substring(b, e));
            buf.append(newsub);
        } while ((e = input.indexOf(oldsub, b = e + oln)) != -1 && !firstOnly);
        buf.append(text.substring(b));
        return buf.toString();
    }

    public static String chomp(String s) {
        if (s.endsWith("\r\n")) {
            return s.substring(0, s.length() - 2);
        }
        if (s.endsWith("\r") || s.endsWith("\n")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String emptyToNull(String s) {
        if (s == null) {
            return null;
        }
        return s.length() == 0 ? null : s;
    }

    public static String jQuote(Object obj) {
        return StringUtil.jQuote(obj != null ? obj.toString() : null);
    }

    public static String jQuote(String s) {
        if (s == null) {
            return "null";
        }
        return StringUtil.javaStringEnc(s, true);
    }

    public static String jQuoteNoXSS(Object obj) {
        return StringUtil.jQuoteNoXSS(obj != null ? obj.toString() : null);
    }

    public static String jQuoteNoXSS(String s) {
        if (s == null) {
            return "null";
        }
        int ln = s.length();
        StringBuilder b = new StringBuilder(ln + 6);
        b.append('\"');
        for (int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            if (c == '\"') {
                b.append("\\\"");
                continue;
            }
            if (c == '\\') {
                b.append("\\\\");
                continue;
            }
            if (c == '<') {
                b.append("\\u003C");
                continue;
            }
            if (c < ' ') {
                if (c == '\n') {
                    b.append("\\n");
                    continue;
                }
                if (c == '\r') {
                    b.append("\\r");
                    continue;
                }
                if (c == '\f') {
                    b.append("\\f");
                    continue;
                }
                if (c == '\b') {
                    b.append("\\b");
                    continue;
                }
                if (c == '\t') {
                    b.append("\\t");
                    continue;
                }
                b.append("\\u00");
                b.append(StringUtil.toHexDigitLowerCase(c / 16));
                b.append(StringUtil.toHexDigitLowerCase(c & 0xF));
                continue;
            }
            b.append(c);
        }
        b.append('\"');
        return b.toString();
    }

    public static String ftlQuote(String s) {
        int quotation = s.indexOf(34) != -1 && s.indexOf(39) == -1 ? 39 : 34;
        return StringUtil.FTLStringLiteralEnc(s, (char)quotation, true);
    }

    public static boolean isFTLIdentifierStart(char c) {
        if (c < '\u00aa') {
            if (c >= 'a' && c <= 'z' || c >= '@' && c <= 'Z') {
                return true;
            }
            return c == '$' || c == '_';
        }
        if (c < '\ua7f8') {
            if (c < '\u2d6f') {
                if (c < '\u2128') {
                    if (c < '\u2090') {
                        if (c < '\u00d8') {
                            if (c < '\u00ba') {
                                return c == '\u00aa' || c == '\u00b5';
                            }
                            return c == '\u00ba' || c >= '\u00c0' && c <= '\u00d6';
                        }
                        if (c < '\u2071') {
                            return c >= '\u00d8' && c <= '\u00f6' || c >= '\u00f8' && c <= '\u1fff';
                        }
                        return c == '\u2071' || c == '\u207f';
                    }
                    if (c < '\u2115') {
                        if (c < '\u2107') {
                            return c >= '\u2090' && c <= '\u209c' || c == '\u2102';
                        }
                        return c == '\u2107' || c >= '\u210a' && c <= '\u2113';
                    }
                    if (c < '\u2124') {
                        return c == '\u2115' || c >= '\u2119' && c <= '\u211d';
                    }
                    return c == '\u2124' || c == '\u2126';
                }
                if (c < '\u2c30') {
                    if (c < '\u2145') {
                        if (c < '\u212f') {
                            return c == '\u2128' || c >= '\u212a' && c <= '\u212d';
                        }
                        return c >= '\u212f' && c <= '\u2139' || c >= '\u213c' && c <= '\u213f';
                    }
                    if (c < '\u2183') {
                        return c >= '\u2145' && c <= '\u2149' || c == '\u214e';
                    }
                    return c >= '\u2183' && c <= '\u2184' || c >= '\u2c00' && c <= '\u2c2e';
                }
                if (c < '\u2d00') {
                    if (c < '\u2ceb') {
                        return c >= '\u2c30' && c <= '\u2c5e' || c >= '\u2c60' && c <= '\u2ce4';
                    }
                    return c >= '\u2ceb' && c <= '\u2cee' || c >= '\u2cf2' && c <= '\u2cf3';
                }
                if (c < '\u2d2d') {
                    return c >= '\u2d00' && c <= '\u2d25' || c == '\u2d27';
                }
                return c == '\u2d2d' || c >= '\u2d30' && c <= '\u2d67';
            }
            if (c < '\u31f0') {
                if (c < '\u2dd0') {
                    if (c < '\u2db0') {
                        if (c < '\u2da0') {
                            return c == '\u2d6f' || c >= '\u2d80' && c <= '\u2d96';
                        }
                        return c >= '\u2da0' && c <= '\u2da6' || c >= '\u2da8' && c <= '\u2dae';
                    }
                    if (c < '\u2dc0') {
                        return c >= '\u2db0' && c <= '\u2db6' || c >= '\u2db8' && c <= '\u2dbe';
                    }
                    return c >= '\u2dc0' && c <= '\u2dc6' || c >= '\u2dc8' && c <= '\u2dce';
                }
                if (c < '\u3031') {
                    if (c < '\u2e2f') {
                        return c >= '\u2dd0' && c <= '\u2dd6' || c >= '\u2dd8' && c <= '\u2dde';
                    }
                    return c == '\u2e2f' || c >= '\u3005' && c <= '\u3006';
                }
                if (c < '\u3040') {
                    return c >= '\u3031' && c <= '\u3035' || c >= '\u303b' && c <= '\u303c';
                }
                return c >= '\u3040' && c <= '\u318f' || c >= '\u31a0' && c <= '\u31ba';
            }
            if (c < '\ua67f') {
                if (c < '\ua4d0') {
                    if (c < '\u3400') {
                        return c >= '\u31f0' && c <= '\u31ff' || c >= '\u3300' && c <= '\u337f';
                    }
                    return c >= '\u3400' && c <= '\u4db5' || c >= '\u4e00' && c <= '\ua48c';
                }
                if (c < '\ua610') {
                    return c >= '\ua4d0' && c <= '\ua4fd' || c >= '\ua500' && c <= '\ua60c';
                }
                return c >= '\ua610' && c <= '\ua62b' || c >= '\ua640' && c <= '\ua66e';
            }
            if (c < '\ua78b') {
                if (c < '\ua717') {
                    return c >= '\ua67f' && c <= '\ua697' || c >= '\ua6a0' && c <= '\ua6e5';
                }
                return c >= '\ua717' && c <= '\ua71f' || c >= '\ua722' && c <= '\ua788';
            }
            if (c < '\ua7a0') {
                return c >= '\ua78b' && c <= '\ua78e' || c >= '\ua790' && c <= '\ua793';
            }
            return c >= '\ua7a0' && c <= '\ua7aa';
        }
        if (c < '\uab20') {
            if (c < '\uaa44') {
                if (c < '\ua8fb') {
                    if (c < '\ua840') {
                        if (c < '\ua807') {
                            return c >= '\ua7f8' && c <= '\ua801' || c >= '\ua803' && c <= '\ua805';
                        }
                        return c >= '\ua807' && c <= '\ua80a' || c >= '\ua80c' && c <= '\ua822';
                    }
                    if (c < '\ua8d0') {
                        return c >= '\ua840' && c <= '\ua873' || c >= '\ua882' && c <= '\ua8b3';
                    }
                    return c >= '\ua8d0' && c <= '\ua8d9' || c >= '\ua8f2' && c <= '\ua8f7';
                }
                if (c < '\ua984') {
                    if (c < '\ua930') {
                        return c == '\ua8fb' || c >= '\ua900' && c <= '\ua925';
                    }
                    return c >= '\ua930' && c <= '\ua946' || c >= '\ua960' && c <= '\ua97c';
                }
                if (c < '\uaa00') {
                    return c >= '\ua984' && c <= '\ua9b2' || c >= '\ua9cf' && c <= '\ua9d9';
                }
                return c >= '\uaa00' && c <= '\uaa28' || c >= '\uaa40' && c <= '\uaa42';
            }
            if (c < '\uaac0') {
                if (c < '\uaa80') {
                    if (c < '\uaa60') {
                        return c >= '\uaa44' && c <= '\uaa4b' || c >= '\uaa50' && c <= '\uaa59';
                    }
                    return c >= '\uaa60' && c <= '\uaa76' || c == '\uaa7a';
                }
                if (c < '\uaab5') {
                    return c >= '\uaa80' && c <= '\uaaaf' || c == '\uaab1';
                }
                return c >= '\uaab5' && c <= '\uaab6' || c >= '\uaab9' && c <= '\uaabd';
            }
            if (c < '\uaaf2') {
                if (c < '\uaadb') {
                    return c == '\uaac0' || c == '\uaac2';
                }
                return c >= '\uaadb' && c <= '\uaadd' || c >= '\uaae0' && c <= '\uaaea';
            }
            if (c < '\uab09') {
                return c >= '\uaaf2' && c <= '\uaaf4' || c >= '\uab01' && c <= '\uab06';
            }
            return c >= '\uab09' && c <= '\uab0e' || c >= '\uab11' && c <= '\uab16';
        }
        if (c < '\ufb46') {
            if (c < '\ufb13') {
                if (c < '\uac00') {
                    if (c < '\uabc0') {
                        return c >= '\uab20' && c <= '\uab26' || c >= '\uab28' && c <= '\uab2e';
                    }
                    return c >= '\uabc0' && c <= '\uabe2' || c >= '\uabf0' && c <= '\uabf9';
                }
                if (c < '\ud7cb') {
                    return c >= '\uac00' && c <= '\ud7a3' || c >= '\ud7b0' && c <= '\ud7c6';
                }
                return c >= '\ud7cb' && c <= '\ud7fb' || c >= '\uf900' && c <= '\ufb06';
            }
            if (c < '\ufb38') {
                if (c < '\ufb1f') {
                    return c >= '\ufb13' && c <= '\ufb17' || c == '\ufb1d';
                }
                return c >= '\ufb1f' && c <= '\ufb28' || c >= '\ufb2a' && c <= '\ufb36';
            }
            if (c < '\ufb40') {
                return c >= '\ufb38' && c <= '\ufb3c' || c == '\ufb3e';
            }
            return c >= '\ufb40' && c <= '\ufb41' || c >= '\ufb43' && c <= '\ufb44';
        }
        if (c < '\uff21') {
            if (c < '\ufdf0') {
                if (c < '\ufd50') {
                    return c >= '\ufb46' && c <= '\ufbb1' || c >= '\ufbd3' && c <= '\ufd3d';
                }
                return c >= '\ufd50' && c <= '\ufd8f' || c >= '\ufd92' && c <= '\ufdc7';
            }
            if (c < '\ufe76') {
                return c >= '\ufdf0' && c <= '\ufdfb' || c >= '\ufe70' && c <= '\ufe74';
            }
            return c >= '\ufe76' && c <= '\ufefc' || c >= '\uff10' && c <= '\uff19';
        }
        if (c < '\uffca') {
            if (c < '\uff66') {
                return c >= '\uff21' && c <= '\uff3a' || c >= '\uff41' && c <= '\uff5a';
            }
            return c >= '\uff66' && c <= '\uffbe' || c >= '\uffc2' && c <= '\uffc7';
        }
        if (c < '\uffda') {
            return c >= '\uffca' && c <= '\uffcf' || c >= '\uffd2' && c <= '\uffd7';
        }
        return c >= '\uffda' && c <= '\uffdc';
    }

    public static boolean isFTLIdentifierPart(char c) {
        return StringUtil.isFTLIdentifierStart(c) || c >= '0' && c <= '9';
    }

    public static boolean isBackslashEscapedFTLIdentifierCharacter(char c) {
        return c == '-' || c == '.' || c == ':' || c == '#';
    }

    public static String javaStringEnc(String s) {
        return StringUtil.javaStringEnc(s, false);
    }

    public static String javaStringEnc(String s, boolean quote) {
        int ln = s.length();
        for (int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            if (c != '\"' && c != '\\' && c >= ' ') continue;
            StringBuilder b = new StringBuilder(ln + (quote ? 6 : 4));
            if (quote) {
                b.append("\"");
            }
            b.append(s, 0, i);
            while (true) {
                if (c == '\"') {
                    b.append("\\\"");
                } else if (c == '\\') {
                    b.append("\\\\");
                } else if (c < ' ') {
                    if (c == '\n') {
                        b.append("\\n");
                    } else if (c == '\r') {
                        b.append("\\r");
                    } else if (c == '\f') {
                        b.append("\\f");
                    } else if (c == '\b') {
                        b.append("\\b");
                    } else if (c == '\t') {
                        b.append("\\t");
                    } else {
                        b.append("\\u00");
                        b.append(StringUtil.toHexDigitLowerCase(c / 16));
                        b.append(StringUtil.toHexDigitLowerCase(c & 0xF));
                    }
                } else {
                    b.append(c);
                }
                if (++i >= ln) {
                    if (quote) {
                        b.append("\"");
                    }
                    return b.toString();
                }
                c = s.charAt(i);
            }
        }
        return quote ? '\"' + s + '\"' : s;
    }

    public static String javaScriptStringEnc(String s) {
        return StringUtil.jsStringEnc(s, JsStringEncCompatibility.JAVA_SCRIPT);
    }

    public static String jsonStringEnc(String s) {
        return StringUtil.jsStringEnc(s, JsStringEncCompatibility.JSON);
    }

    @Deprecated
    public static String jsStringEnc(String s, boolean json) {
        return StringUtil.jsStringEnc(s, json ? JsStringEncCompatibility.JSON : JsStringEncCompatibility.JAVA_SCRIPT, null);
    }

    public static String jsStringEnc(String s, JsStringEncCompatibility compatibility) {
        return StringUtil.jsStringEnc(s, compatibility, null);
    }

    public static String jsStringEnc(String s, JsStringEncCompatibility compatibility, JsStringEncQuotation quotation) {
        StringBuilder sb;
        NullArgumentException.check("s", s);
        int ln = s.length();
        if (quotation == null) {
            sb = null;
        } else {
            if (quotation == JsStringEncQuotation.APOSTROPHE && compatibility.jsonCompatible) {
                throw new IllegalArgumentException("JSON compatible mode doesn't allow quotationMode=" + (Object)((Object)quotation));
            }
            sb = new StringBuilder(ln + 8);
            sb.append(quotation.getSymbol());
        }
        for (int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            if (!(c > '>' && c < '\u007f' && c != '\\' || c == ' ' || c >= '\u00a0' && c < '\u2028')) {
                boolean dangerous;
                int escapeType;
                if (c <= '\u001f') {
                    escapeType = c == '\n' ? 110 : (c == '\r' ? 114 : (c == '\f' ? 102 : (c == '\b' ? 98 : (c == '\t' ? 116 : 1))));
                } else if (c == '\"') {
                    escapeType = quotation == JsStringEncQuotation.APOSTROPHE ? 0 : 3;
                } else if (c == '\'') {
                    escapeType = !compatibility.javaScriptCompatible || quotation == JsStringEncQuotation.QUOTATION_MARK ? 0 : (compatibility.jsonCompatible ? 1 : 3);
                } else if (c == '\\') {
                    escapeType = 3;
                } else if (c == '/' && (i == 0 && quotation == null || i != 0 && s.charAt(i - 1) == '<')) {
                    escapeType = 3;
                } else if (c == '>') {
                    char prevPrevC;
                    char prevC;
                    dangerous = quotation != null && i < 2 ? false : (i == 0 ? true : ((prevC = s.charAt(i - 1)) == ']' || prevC == '-' ? (i == 1 ? true : (prevPrevC = s.charAt(i - 2)) == prevC) : false));
                    escapeType = dangerous ? (compatibility.jsonCompatible ? 1 : 3) : 0;
                } else if (c == '<') {
                    char nextC;
                    dangerous = i == ln - 1 ? quotation == null : (nextC = s.charAt(i + 1)) == '!' || nextC == '?';
                    escapeType = dangerous ? 1 : 0;
                } else {
                    escapeType = c >= '\u007f' && c <= '\u009f' || c == '\u2028' || c == '\u2029' ? 1 : 0;
                }
                if (escapeType != 0) {
                    if (sb == null) {
                        sb = new StringBuilder(ln + 6);
                        sb.append(s, 0, i);
                    }
                    sb.append('\\');
                    if (escapeType > 32) {
                        sb.append((char)escapeType);
                        continue;
                    }
                    if (escapeType == 1) {
                        if (!compatibility.jsonCompatible && c < '\u0100') {
                            sb.append('x');
                            sb.append(StringUtil.toHexDigitUpperCase(c >> 4));
                            sb.append(StringUtil.toHexDigitUpperCase(c & 0xF));
                            continue;
                        }
                        sb.append('u');
                        char cp = c;
                        sb.append(StringUtil.toHexDigitUpperCase(cp >> 12 & 0xF));
                        sb.append(StringUtil.toHexDigitUpperCase(cp >> 8 & 0xF));
                        sb.append(StringUtil.toHexDigitUpperCase(cp >> 4 & 0xF));
                        sb.append(StringUtil.toHexDigitUpperCase(cp & 0xF));
                        continue;
                    }
                    sb.append(c);
                    continue;
                }
            }
            if (sb == null) continue;
            sb.append(c);
        }
        if (quotation != null) {
            sb.append(quotation.getSymbol());
        }
        return sb == null ? s : sb.toString();
    }

    private static char toHexDigitLowerCase(int d) {
        return (char)(d < 10 ? d + 48 : d - 10 + 97);
    }

    private static char toHexDigitUpperCase(int d) {
        return (char)(d < 10 ? d + 48 : d - 10 + 65);
    }

    public static Map parseNameValuePairList(String s, String defaultValue) throws java.text.ParseException {
        HashMap<String, String> map;
        block18: {
            String key;
            int keyStart;
            map = new HashMap<String, String>();
            char c = ' ';
            int ln = s.length();
            int p = 0;
            while (true) {
                String value;
                if (p < ln && Character.isWhitespace(c = (char)s.charAt(p))) {
                    ++p;
                    continue;
                }
                if (p == ln) break block18;
                keyStart = p;
                while (p < ln && (Character.isLetterOrDigit(c = s.charAt(p)) || c == '_')) {
                    ++p;
                }
                if (keyStart == p) {
                    throw new java.text.ParseException("Expecting letter, digit or \"_\" here, (the first character of the key) but found " + StringUtil.jQuote(String.valueOf(c)) + " at position " + p + ".", p);
                }
                key = s.substring(keyStart, p);
                while (p < ln && Character.isWhitespace(c = s.charAt(p))) {
                    ++p;
                }
                if (p == ln) {
                    if (defaultValue == null) {
                        throw new java.text.ParseException("Expecting \":\", but reached the end of the string  at position " + p + ".", p);
                    }
                    value = defaultValue;
                } else if (c != ':') {
                    if (defaultValue == null || c != ',') {
                        throw new java.text.ParseException("Expecting \":\" here, but found " + StringUtil.jQuote(String.valueOf(c)) + " at position " + p + ".", p);
                    }
                    ++p;
                    value = defaultValue;
                } else {
                    ++p;
                    while (p < ln && Character.isWhitespace(c = s.charAt(p))) {
                        ++p;
                    }
                    if (p == ln) {
                        throw new java.text.ParseException("Expecting the value of the key here, but reached the end of the string  at position " + p + ".", p);
                    }
                    int valueStart = p;
                    while (p < ln && (Character.isLetterOrDigit(c = s.charAt(p)) || c == '_')) {
                        ++p;
                    }
                    if (valueStart == p) {
                        throw new java.text.ParseException("Expecting letter, digit or \"_\" here, (the first character of the value) but found " + StringUtil.jQuote(String.valueOf(c)) + " at position " + p + ".", p);
                    }
                    value = s.substring(valueStart, p);
                    while (p < ln && Character.isWhitespace(c = s.charAt(p))) {
                        ++p;
                    }
                    if (p < ln) {
                        if (c != ',') {
                            throw new java.text.ParseException("Excpecting \",\" or the end of the string here, but found " + StringUtil.jQuote(String.valueOf(c)) + " at position " + p + ".", p);
                        }
                        ++p;
                    }
                }
                if (map.put(key, value) != null) break;
            }
            throw new java.text.ParseException("Dublicated key: " + StringUtil.jQuote(key), keyStart);
        }
        return map;
    }

    @Deprecated
    public static boolean isXMLID(String name) {
        return _ExtDomApi.isXMLNameLike(name);
    }

    public static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
        return _ExtDomApi.matchesName(qname, nodeName, nsURI, env);
    }

    public static String leftPad(String s, int minLength) {
        return StringUtil.leftPad(s, minLength, ' ');
    }

    public static String leftPad(String s, int minLength, char filling) {
        int ln = s.length();
        if (minLength <= ln) {
            return s;
        }
        StringBuilder res = new StringBuilder(minLength);
        int dif = minLength - ln;
        for (int i = 0; i < dif; ++i) {
            res.append(filling);
        }
        res.append(s);
        return res.toString();
    }

    public static String leftPad(String s, int minLength, String filling) {
        int i;
        int ln = s.length();
        if (minLength <= ln) {
            return s;
        }
        StringBuilder res = new StringBuilder(minLength);
        int dif = minLength - ln;
        int fln = filling.length();
        if (fln == 0) {
            throw new IllegalArgumentException("The \"filling\" argument can't be 0 length string.");
        }
        int cnt = dif / fln;
        for (i = 0; i < cnt; ++i) {
            res.append(filling);
        }
        cnt = dif % fln;
        for (i = 0; i < cnt; ++i) {
            res.append(filling.charAt(i));
        }
        res.append(s);
        return res.toString();
    }

    public static String rightPad(String s, int minLength) {
        return StringUtil.rightPad(s, minLength, ' ');
    }

    public static String rightPad(String s, int minLength, char filling) {
        int ln = s.length();
        if (minLength <= ln) {
            return s;
        }
        StringBuilder res = new StringBuilder(minLength);
        res.append(s);
        int dif = minLength - ln;
        for (int i = 0; i < dif; ++i) {
            res.append(filling);
        }
        return res.toString();
    }

    public static String rightPad(String s, int minLength, String filling) {
        int i;
        int ln = s.length();
        if (minLength <= ln) {
            return s;
        }
        StringBuilder res = new StringBuilder(minLength);
        res.append(s);
        int dif = minLength - ln;
        int fln = filling.length();
        if (fln == 0) {
            throw new IllegalArgumentException("The \"filling\" argument can't be 0 length string.");
        }
        int start = ln % fln;
        int end = fln - start <= dif ? fln : start + dif;
        for (int i2 = start; i2 < end; ++i2) {
            res.append(filling.charAt(i2));
        }
        int cnt = (dif -= end - start) / fln;
        for (i = 0; i < cnt; ++i) {
            res.append(filling);
        }
        cnt = dif % fln;
        for (i = 0; i < cnt; ++i) {
            res.append(filling.charAt(i));
        }
        return res.toString();
    }

    public static int versionStringToInt(String version) {
        return new Version(version).intValue();
    }

    public static String tryToString(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return object.toString();
        }
        catch (Throwable e) {
            return StringUtil.failedToStringSubstitute(object, e);
        }
    }

    private static String failedToStringSubstitute(Object object, Throwable e) {
        String eStr;
        try {
            eStr = e.toString();
        }
        catch (Throwable e2) {
            eStr = ClassUtil.getShortClassNameOfObject(e);
        }
        return "[" + ClassUtil.getShortClassNameOfObject(object) + ".toString() failed: " + eStr + "]";
    }

    public static String toUpperABC(int n) {
        return StringUtil.toABC(n, 'A');
    }

    public static String toLowerABC(int n) {
        return StringUtil.toABC(n, 'a');
    }

    private static String toABC(int n, char oneDigit) {
        int nextWeight;
        int nextReached;
        if (n < 1) {
            throw new IllegalArgumentException("Can't convert 0 or negative numbers to latin-number: " + n);
        }
        int reached = 1;
        int weight = 1;
        while ((nextReached = reached + (nextWeight = weight * 26)) <= n) {
            weight = nextWeight;
            reached = nextReached;
        }
        StringBuilder sb = new StringBuilder();
        while (weight != 0) {
            int digitIncrease = (n - reached) / weight;
            sb.append((char)(oneDigit + digitIncrease));
            reached += digitIncrease * weight;
            weight /= 26;
        }
        return sb.toString();
    }

    public static char[] trim(char[] cs) {
        int start;
        if (cs.length == 0) {
            return cs;
        }
        int end = cs.length;
        for (start = 0; start < end && cs[start] <= ' '; ++start) {
        }
        while (start < end && cs[end - 1] <= ' ') {
            --end;
        }
        if (start == 0 && end == cs.length) {
            return cs;
        }
        if (start == end) {
            return CollectionUtils.EMPTY_CHAR_ARRAY;
        }
        char[] newCs = new char[end - start];
        System.arraycopy(cs, start, newCs, 0, end - start);
        return newCs;
    }

    public static boolean isTrimmableToEmpty(char[] text) {
        return StringUtil.isTrimmableToEmpty(text, 0, text.length);
    }

    public static boolean isTrimmableToEmpty(char[] text, int start) {
        return StringUtil.isTrimmableToEmpty(text, start, text.length);
    }

    public static boolean isTrimmableToEmpty(char[] text, int start, int end) {
        for (int i = start; i < end; ++i) {
            if (text[i] <= ' ') continue;
            return false;
        }
        return true;
    }

    public static Pattern globToRegularExpression(String glob) {
        return StringUtil.globToRegularExpression(glob, false);
    }

    public static Pattern globToRegularExpression(String glob, boolean caseInsensitive) {
        StringBuilder regex = new StringBuilder();
        int nextStart = 0;
        boolean escaped = false;
        int ln = glob.length();
        for (int idx = 0; idx < ln; ++idx) {
            char c = glob.charAt(idx);
            if (!escaped) {
                if (c == '?') {
                    StringUtil.appendLiteralGlobSection(regex, glob, nextStart, idx);
                    regex.append("[^/]");
                    nextStart = idx + 1;
                    continue;
                }
                if (c == '*') {
                    StringUtil.appendLiteralGlobSection(regex, glob, nextStart, idx);
                    if (idx + 1 < ln && glob.charAt(idx + 1) == '*') {
                        if (idx != 0 && glob.charAt(idx - 1) != '/') {
                            throw new IllegalArgumentException("The \"**\" wildcard must be directly after a \"/\" or it must be at the beginning, in this glob: " + glob);
                        }
                        if (idx + 2 == ln) {
                            regex.append(".*");
                            ++idx;
                        } else {
                            if (idx + 2 >= ln || glob.charAt(idx + 2) != '/') {
                                throw new IllegalArgumentException("The \"**\" wildcard must be followed by \"/\", or must be at tehe end, in this glob: " + glob);
                            }
                            regex.append("(.*?/)*");
                            idx += 2;
                        }
                    } else {
                        regex.append("[^/]*");
                    }
                    nextStart = idx + 1;
                    continue;
                }
                if (c == '\\') {
                    escaped = true;
                    continue;
                }
                if (c != '[' && c != '{') continue;
                throw new IllegalArgumentException("The \"" + c + "\" glob operator is currently unsupported (precede it with \\ for literal matching), in this glob: " + glob);
            }
            escaped = false;
        }
        StringUtil.appendLiteralGlobSection(regex, glob, nextStart, glob.length());
        return Pattern.compile(regex.toString(), caseInsensitive ? 66 : 0);
    }

    private static void appendLiteralGlobSection(StringBuilder regex, String glob, int start, int end) {
        if (start == end) {
            return;
        }
        String part = StringUtil.unescapeLiteralGlobSection(glob.substring(start, end));
        regex.append(Pattern.quote(part));
    }

    private static String unescapeLiteralGlobSection(String s) {
        int backslashIdx = s.indexOf(92);
        if (backslashIdx == -1) {
            return s;
        }
        int ln = s.length();
        StringBuilder sb = new StringBuilder(ln - 1);
        int nextStart = 0;
        do {
            sb.append(s, nextStart, backslashIdx);
        } while ((backslashIdx = s.indexOf(92, (nextStart = backslashIdx + 1) + 1)) != -1);
        if (nextStart < ln) {
            sb.append(s, nextStart, ln);
        }
        return sb.toString();
    }

    public static enum JsStringEncQuotation {
        QUOTATION_MARK('\"'),
        APOSTROPHE('\'');

        private final char symbol;

        private JsStringEncQuotation(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return this.symbol;
        }
    }

    public static enum JsStringEncCompatibility {
        JAVA_SCRIPT(true, false),
        JSON(false, true),
        JAVA_SCRIPT_OR_JSON(true, true);

        private final boolean javaScriptCompatible;
        private final boolean jsonCompatible;

        private JsStringEncCompatibility(boolean javaScriptCompatible, boolean jsonCompatible) {
            this.javaScriptCompatible = javaScriptCompatible;
            this.jsonCompatible = jsonCompatible;
        }

        boolean isJSONCompatible() {
            return this.jsonCompatible;
        }
    }
}

