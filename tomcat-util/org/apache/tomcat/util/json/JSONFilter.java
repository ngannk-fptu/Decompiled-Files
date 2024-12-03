/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.json;

public class JSONFilter {
    public static char[] escape(char c) {
        if (c < ' ' || c == '\"' || c == '\\' || Character.isHighSurrogate(c) || Character.isLowSurrogate(c)) {
            char popular = JSONFilter.getPopularChar(c);
            if (popular > '\u0000') {
                return new char[]{'\\', popular};
            }
            StringBuilder escaped = new StringBuilder(6);
            escaped.append("\\u");
            escaped.append(String.format("%04X", c));
            return escaped.toString().toCharArray();
        }
        char[] result = new char[]{c};
        return result;
    }

    public static String escape(String input) {
        return JSONFilter.escape(input, 0, input.length()).toString();
    }

    public static CharSequence escape(CharSequence input) {
        return JSONFilter.escape(input, 0, input.length());
    }

    public static CharSequence escape(CharSequence input, int off, int length) {
        StringBuilder escaped = null;
        int lastUnescapedStart = off;
        for (int i = off; i < length; ++i) {
            char c = input.charAt(i);
            if (c >= ' ' && c != '\"' && c != '\\' && !Character.isHighSurrogate(c) && !Character.isLowSurrogate(c)) continue;
            if (escaped == null) {
                escaped = new StringBuilder(length + 20);
            }
            if (lastUnescapedStart < i) {
                escaped.append(input.subSequence(lastUnescapedStart, i));
            }
            lastUnescapedStart = i + 1;
            char popular = JSONFilter.getPopularChar(c);
            if (popular > '\u0000') {
                escaped.append('\\').append(popular);
                continue;
            }
            escaped.append("\\u");
            escaped.append(String.format("%04X", c));
        }
        if (escaped == null) {
            if (off == 0 && length == input.length()) {
                return input;
            }
            return input.subSequence(off, length - off);
        }
        if (lastUnescapedStart < length) {
            escaped.append(input.subSequence(lastUnescapedStart, length));
        }
        return escaped.toString();
    }

    private JSONFilter() {
    }

    private static char getPopularChar(char c) {
        switch (c) {
            case '\"': 
            case '/': 
            case '\\': {
                return c;
            }
            case '\b': {
                return 'b';
            }
            case '\f': {
                return 'f';
            }
            case '\n': {
                return 'n';
            }
            case '\r': {
                return 'r';
            }
            case '\t': {
                return 't';
            }
        }
        return '\u0000';
    }
}

