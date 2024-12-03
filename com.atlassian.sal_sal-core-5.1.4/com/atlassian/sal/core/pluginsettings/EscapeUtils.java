/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.core.pluginsettings;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

class EscapeUtils {
    protected static final char VERTICAL_TAB = '\f';
    protected static final char NEW_LINE = '\n';

    private EscapeUtils() {
    }

    protected static String escape(String str) {
        if (str == null) {
            return null;
        }
        StringWriter writer = new StringWriter(str.length() * 2);
        try {
            EscapeUtils.escape(writer, str);
        }
        catch (IOException e) {
            throw new RuntimeException("exception while writing to StringWriter (should be impossible in this context)", e);
        }
        return writer.toString();
    }

    private static void escape(Writer out, String str) throws IOException {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char ch = str.charAt(i);
            if (ch == '\f') {
                out.write(92);
                out.write(102);
                continue;
            }
            if (ch == '\n') {
                out.write(92);
                out.write(110);
                continue;
            }
            if (ch == '\\') {
                out.write(92);
                out.write(92);
                continue;
            }
            out.write(ch);
        }
    }

    public static String unescape(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            EscapeUtils.unescape(writer, str);
            return writer.toString();
        }
        catch (IOException e) {
            throw new RuntimeException("exception while writing to StringWriter (should be impossible in this context)", e);
        }
    }

    private static void unescape(Writer out, String str) throws IOException {
        int len = str.length();
        boolean hadSlash = false;
        for (int i = 0; i < len; ++i) {
            char ch = str.charAt(i);
            if (hadSlash) {
                switch (ch) {
                    case 'f': {
                        out.write(12);
                        break;
                    }
                    case 'n': {
                        out.write(10);
                        break;
                    }
                    default: {
                        out.write(ch);
                    }
                }
                hadSlash = false;
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
}

