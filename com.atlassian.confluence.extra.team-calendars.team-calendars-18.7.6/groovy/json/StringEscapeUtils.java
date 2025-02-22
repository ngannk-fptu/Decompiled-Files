/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

public class StringEscapeUtils {
    public static String escapeJava(String str) {
        return StringEscapeUtils.escapeJavaStyleString(str, false, false);
    }

    public static void escapeJava(Writer out, String str) throws IOException {
        StringEscapeUtils.escapeJavaStyleString(out, str, false, false);
    }

    public static String escapeJavaScript(String str) {
        return StringEscapeUtils.escapeJavaStyleString(str, true, true);
    }

    public static void escapeJavaScript(Writer out, String str) throws IOException {
        StringEscapeUtils.escapeJavaStyleString(out, str, true, true);
    }

    private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes, boolean escapeForwardSlash) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length() * 2);
            StringEscapeUtils.escapeJavaStyleString(writer, str, escapeSingleQuotes, escapeForwardSlash);
            return writer.toString();
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote, boolean escapeForwardSlash) throws IOException {
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
                    if (escapeForwardSlash) {
                        out.write(92);
                    }
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
        return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
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
            throw new RuntimeException(ioe);
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
        StringBuilder unicode = new StringBuilder(4);
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
                    throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
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
}

