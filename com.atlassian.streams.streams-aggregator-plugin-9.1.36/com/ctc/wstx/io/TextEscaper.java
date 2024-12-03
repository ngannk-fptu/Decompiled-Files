/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import java.io.IOException;
import java.io.Writer;

public final class TextEscaper {
    private TextEscaper() {
    }

    public static void writeEscapedAttrValue(Writer w, String value) throws IOException {
        int i = 0;
        int len = value.length();
        do {
            int start = i;
            char c = '\u0000';
            while (i < len && (c = value.charAt(i)) != '<' && c != '&' && c != '\"') {
                ++i;
            }
            int outLen = i - start;
            if (outLen > 0) {
                w.write(value, start, outLen);
            }
            if (i >= len) continue;
            if (c == '<') {
                w.write("&lt;");
                continue;
            }
            if (c == '&') {
                w.write("&amp;");
                continue;
            }
            if (c != '\"') continue;
            w.write("&quot;");
        } while (++i < len);
    }

    public static void outputDTDText(Writer w, char[] ch, int offset, int len) throws IOException {
        int i = offset;
        len += offset;
        do {
            int start = i;
            char c = '\u0000';
            while (i < len && (c = ch[i]) != '&' && c != '%' && c != '\"') {
                ++i;
            }
            int outLen = i - start;
            if (outLen > 0) {
                w.write(ch, start, outLen);
            }
            if (i >= len) continue;
            if (c == '&') {
                w.write("&amp;");
                continue;
            }
            if (c == '%') {
                w.write("&#37;");
                continue;
            }
            if (c != '\"') continue;
            w.write("&#34;");
        } while (++i < len);
    }
}

