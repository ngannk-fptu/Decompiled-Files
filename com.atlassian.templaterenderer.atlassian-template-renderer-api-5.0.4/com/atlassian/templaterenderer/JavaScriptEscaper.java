/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.templaterenderer;

import com.atlassian.templaterenderer.annotations.HtmlSafe;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class JavaScriptEscaper {
    @HtmlSafe
    public static String escape(String str) {
        return JavaScriptEscaper.escapeJavaStyleString(str, true);
    }

    @HtmlSafe
    public static void escape(Writer out, String str) throws IOException {
        JavaScriptEscaper.escapeJavaStyleString(out, str, true);
    }

    private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length() * 2);
            JavaScriptEscaper.escapeJavaStyleString(writer, str, escapeSingleQuotes);
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
        block14: for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (ch > '\u0fff') {
                out.write("\\u" + JavaScriptEscaper.hex(ch));
                continue;
            }
            if (ch > '\u00ff') {
                out.write("\\u0" + JavaScriptEscaper.hex(ch));
                continue;
            }
            if (ch > '\u007f') {
                out.write("\\u00" + JavaScriptEscaper.hex(ch));
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
                            out.write("\\u00" + JavaScriptEscaper.hex(ch));
                            break;
                        }
                        out.write("\\u000" + JavaScriptEscaper.hex(ch));
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
                    continue block14;
                }
                case '\"': {
                    out.write(92);
                    out.write(34);
                    continue block14;
                }
                case '\\': {
                    out.write(92);
                    out.write(92);
                    continue block14;
                }
                case '/': {
                    out.write(92);
                    out.write(47);
                    continue block14;
                }
                case '<': {
                    out.write("\\u003c");
                    continue block14;
                }
                default: {
                    out.write(ch);
                }
            }
        }
    }

    public static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }
}

