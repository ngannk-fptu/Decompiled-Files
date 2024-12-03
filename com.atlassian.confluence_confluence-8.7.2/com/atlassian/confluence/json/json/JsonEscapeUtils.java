/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.json;

public class JsonEscapeUtils {
    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }
        char c = '\u0000';
        int len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        sb.append('\"');
        block9: for (int i = 0; i < len; ++i) {
            char b = c;
            c = string.charAt(i);
            switch (c) {
                case '\"': 
                case '\\': {
                    sb.append('\\');
                    sb.append(c);
                    continue block9;
                }
                case '/': {
                    if (b == '<') {
                        sb.append('\\');
                    }
                    sb.append(c);
                    continue block9;
                }
                case '\b': {
                    sb.append("\\b");
                    continue block9;
                }
                case '\t': {
                    sb.append("\\t");
                    continue block9;
                }
                case '\n': {
                    sb.append("\\n");
                    continue block9;
                }
                case '\f': {
                    sb.append("\\f");
                    continue block9;
                }
                case '\r': {
                    sb.append("\\r");
                    continue block9;
                }
                default: {
                    if (c < ' ' || c >= '\u0080' && c < '\u00a0' || c >= '\u2000' && c < '\u2100') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                        continue block9;
                    }
                    sb.append(c);
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }
}

