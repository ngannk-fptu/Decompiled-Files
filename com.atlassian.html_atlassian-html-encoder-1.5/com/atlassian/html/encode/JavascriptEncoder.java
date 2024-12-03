/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.html.encode;

import com.atlassian.annotations.PublicApi;
import com.atlassian.html.encode.Util;
import java.io.IOException;
import java.io.Writer;

@PublicApi
public class JavascriptEncoder {
    public static void escape(Writer out, String str) throws IOException {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            JavascriptEncoder.escape(out, str.charAt(i));
        }
    }

    public static void escape(Writer out, char[] chars, int off, int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            JavascriptEncoder.escape(out, chars[off + i]);
        }
    }

    private static void escape(Writer out, char c) throws IOException {
        if (c == '\"') {
            out.write("\\u0022");
        } else if (c == '\'') {
            out.write("\\u0027");
        } else if (c == '\\') {
            out.write("\\u005C");
        } else if (c == '\n') {
            out.write("\\n");
        } else if (c == '\r') {
            out.write("\\r");
        } else if (c == '\u2028') {
            out.write("\\u2028");
        } else if (c == '\u2029') {
            out.write("\\u2029");
        } else if (!Util.isPrintableAscii(c) || JavascriptEncoder.escapeAnyway(c)) {
            String hex = Integer.toHexString(c);
            out.write("\\u");
            for (int pad = 4; pad > hex.length(); --pad) {
                out.write(48);
            }
            out.write(hex);
        } else {
            out.write(c);
        }
    }

    private static boolean escapeAnyway(int c) {
        return "<>".indexOf(c) != -1;
    }
}

