/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.util;

public class HTMLEntityEncoder {
    public static String htmlEntityEncode(String value) {
        StringBuffer buff = new StringBuffer();
        if (value == null) {
            return null;
        }
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            if (ch == '&') {
                buff.append("&amp;");
                continue;
            }
            if (ch == '<') {
                buff.append("&lt;");
                continue;
            }
            if (ch == '>') {
                buff.append("&gt;");
                continue;
            }
            if (Character.isWhitespace(ch)) {
                buff.append(ch);
                continue;
            }
            if (Character.isLetterOrDigit(ch)) {
                buff.append(ch);
                continue;
            }
            if (ch < ' ' || ch > '~') continue;
            buff.append("&#" + ch + ";");
        }
        return buff.toString();
    }
}

