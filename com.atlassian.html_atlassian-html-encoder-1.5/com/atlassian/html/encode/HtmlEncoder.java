/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.html.encode;

import com.atlassian.annotations.PublicApi;
import java.io.IOException;
import java.io.Writer;

@PublicApi
public class HtmlEncoder {
    static final String AMPERSAND = "&amp;";
    static final String DOUBLE_QUOTE = "&quot;";
    static final String GREATER_THAN = "&gt;";
    static final String LESS_THAN = "&lt;";
    static final String SINGLE_QUOTE = "&#39;";

    public static String encode(String text) {
        if (text == null) {
            return "";
        }
        int len = text.length();
        for (int j = 0; j < len; ++j) {
            char c = text.charAt(j);
            switch (c) {
                case '\"': 
                case '&': 
                case '\'': 
                case '<': 
                case '>': {
                    return HtmlEncoder.encodeHeavy(text, j);
                }
            }
        }
        return text;
    }

    private static String encodeHeavy(String text, int j) {
        int len = text.length();
        StringBuilder str = new StringBuilder(len + 64).append(text, 0, j);
        do {
            char c = text.charAt(j);
            switch (c) {
                case '\'': {
                    str.append(SINGLE_QUOTE);
                    break;
                }
                case '\"': {
                    str.append(DOUBLE_QUOTE);
                    break;
                }
                case '&': {
                    str.append(AMPERSAND);
                    break;
                }
                case '<': {
                    str.append(LESS_THAN);
                    break;
                }
                case '>': {
                    str.append(GREATER_THAN);
                    break;
                }
                default: {
                    str.append(c);
                }
            }
        } while (++j < len);
        return str.toString();
    }

    public static void encode(Writer writer, char[] cbuf, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        for (int j = off; j < len; ++j) {
            char c = cbuf[j];
            switch (c) {
                case '\"': 
                case '&': 
                case '\'': 
                case '<': 
                case '>': {
                    HtmlEncoder.writeHeavy(writer, cbuf, off, len, j);
                    return;
                }
            }
        }
        writer.write(cbuf, off, len);
    }

    private static void writeHeavy(Writer writer, char[] cbuf, int off, int len, int from) throws IOException {
        int soFar = from - off;
        if (soFar > 0) {
            writer.write(cbuf, off, soFar);
        }
        block7: for (int j = from; j < len; ++j) {
            char c = cbuf[j];
            switch (c) {
                case '\'': {
                    writer.write(SINGLE_QUOTE);
                    continue block7;
                }
                case '\"': {
                    writer.write(DOUBLE_QUOTE);
                    continue block7;
                }
                case '&': {
                    writer.write(AMPERSAND);
                    continue block7;
                }
                case '<': {
                    writer.write(LESS_THAN);
                    continue block7;
                }
                case '>': {
                    writer.write(GREATER_THAN);
                    continue block7;
                }
                default: {
                    writer.write(c);
                }
            }
        }
    }
}

