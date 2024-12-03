/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.lang.Entities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.NestableRuntimeException;

public class StringEscapeUtils {
    private static final char CSV_DELIMITER = ',';
    private static final char CSV_QUOTE = '\"';
    private static final String CSV_QUOTE_STR = String.valueOf('\"');
    private static final char[] CSV_SEARCH_CHARS = new char[]{',', '\"', '\r', '\n'};

    public static String escapeJava(String str) {
        return StringEscapeUtils.escapeJavaStyleString(str, false);
    }

    public static void escapeJava(Writer out, String str) throws IOException {
        StringEscapeUtils.escapeJavaStyleString(out, str, false);
    }

    public static String escapeJavaScript(String str) {
        return StringEscapeUtils.escapeJavaStyleString(str, true);
    }

    public static void escapeJavaScript(Writer out, String str) throws IOException {
        StringEscapeUtils.escapeJavaStyleString(out, str, true);
    }

    private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length() * 2);
            StringEscapeUtils.escapeJavaStyleString(writer, str, escapeSingleQuotes);
            return writer.toString();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        block13: for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (ch > '\u0fff') {
                out.write("\\u" + StringEscapeUtils.hex(ch));
                continue;
            }
            if (ch > '\u00ff') {
                out.write("\\u0" + StringEscapeUtils.hex(ch));
                continue;
            }
            if (ch > '\u007f') {
                out.write("\\u00" + StringEscapeUtils.hex(ch));
                continue;
            }
            if (ch < ' ') {
                switch (ch) {
                    case '\b': {
                        out.write(92);
                        out.write(98);
                        break;
                    }
                    case '\n': {
                        out.write(92);
                        out.write(110);
                        break;
                    }
                    case '\t': {
                        out.write(92);
                        out.write(116);
                        break;
                    }
                    case '\f': {
                        out.write(92);
                        out.write(102);
                        break;
                    }
                    case '\r': {
                        out.write(92);
                        out.write(114);
                        break;
                    }
                    default: {
                        if (ch > '\u000f') {
                            out.write("\\u00" + StringEscapeUtils.hex(ch));
                            break;
                        }
                        out.write("\\u000" + StringEscapeUtils.hex(ch));
                        break;
                    }
                }
                continue;
            }
            switch (ch) {
                case '\'': {
                    if (escapeSingleQuote) {
                        out.write(92);
                    }
                    out.write(39);
                    continue block13;
                }
                case '\"': {
                    out.write(92);
                    out.write(34);
                    continue block13;
                }
                case '\\': {
                    out.write(92);
                    out.write(92);
                    continue block13;
                }
                case '/': {
                    out.write(92);
                    out.write(47);
                    continue block13;
                }
                default: {
                    out.write(ch);
                }
            }
        }
    }

    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }

    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            StringEscapeUtils.unescapeJava(writer, str);
            return writer.toString();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() != 4) continue;
                try {
                    int value = Integer.parseInt(unicode.toString(), 16);
                    out.write((char)value);
                    unicode.setLength(0);
                    inUnicode = false;
                    hadSlash = false;
                    continue;
                }
                catch (NumberFormatException nfe) {
                    throw new NestableRuntimeException("Unable to parse unicode value: " + unicode, nfe);
                }
            }
            if (hadSlash) {
                hadSlash = false;
                switch (ch) {
                    case '\\': {
                        out.write(92);
                        break;
                    }
                    case '\'': {
                        out.write(39);
                        break;
                    }
                    case '\"': {
                        out.write(34);
                        break;
                    }
                    case 'r': {
                        out.write(13);
                        break;
                    }
                    case 'f': {
                        out.write(12);
                        break;
                    }
                    case 't': {
                        out.write(9);
                        break;
                    }
                    case 'n': {
                        out.write(10);
                        break;
                    }
                    case 'b': {
                        out.write(8);
                        break;
                    }
                    case 'u': {
                        inUnicode = true;
                        break;
                    }
                    default: {
                        out.write(ch);
                        break;
                    }
                }
                continue;
            }
            if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            out.write(92);
        }
    }

    public static String unescapeJavaScript(String str) {
        return StringEscapeUtils.unescapeJava(str);
    }

    public static void unescapeJavaScript(Writer out, String str) throws IOException {
        StringEscapeUtils.unescapeJava(out, str);
    }

    public static String escapeHtml(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter((int)((double)str.length() * 1.5));
            StringEscapeUtils.escapeHtml(writer, str);
            return writer.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void escapeHtml(Writer writer, String string) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        }
        if (string == null) {
            return;
        }
        Entities.HTML40.escape(writer, string);
    }

    public static String unescapeHtml(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter((int)((double)str.length() * 1.5));
            StringEscapeUtils.unescapeHtml(writer, str);
            return writer.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void unescapeHtml(Writer writer, String string) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        }
        if (string == null) {
            return;
        }
        Entities.HTML40.unescape(writer, string);
    }

    public static void escapeXml(Writer writer, String str) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        }
        if (str == null) {
            return;
        }
        Entities.XML.escape(writer, str);
    }

    public static String escapeXml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.XML.escape(str);
    }

    public static void unescapeXml(Writer writer, String str) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        }
        if (str == null) {
            return;
        }
        Entities.XML.unescape(writer, str);
    }

    public static String unescapeXml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.XML.unescape(str);
    }

    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.replace(str, "'", "''");
    }

    public static String escapeCsv(String str) {
        if (StringUtils.containsNone(str, CSV_SEARCH_CHARS)) {
            return str;
        }
        try {
            StringWriter writer = new StringWriter();
            StringEscapeUtils.escapeCsv(writer, str);
            return writer.toString();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static void escapeCsv(Writer out, String str) throws IOException {
        if (StringUtils.containsNone(str, CSV_SEARCH_CHARS)) {
            if (str != null) {
                out.write(str);
            }
            return;
        }
        out.write(34);
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == '\"') {
                out.write(34);
            }
            out.write(c);
        }
        out.write(34);
    }

    public static String unescapeCsv(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter();
            StringEscapeUtils.unescapeCsv(writer, str);
            return writer.toString();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static void unescapeCsv(Writer out, String str) throws IOException {
        if (str == null) {
            return;
        }
        if (str.length() < 2) {
            out.write(str);
            return;
        }
        if (str.charAt(0) != '\"' || str.charAt(str.length() - 1) != '\"') {
            out.write(str);
            return;
        }
        String quoteless = str.substring(1, str.length() - 1);
        if (StringUtils.containsAny(quoteless, CSV_SEARCH_CHARS)) {
            str = StringUtils.replace(quoteless, CSV_QUOTE_STR + CSV_QUOTE_STR, CSV_QUOTE_STR);
        }
        out.write(str);
    }
}

