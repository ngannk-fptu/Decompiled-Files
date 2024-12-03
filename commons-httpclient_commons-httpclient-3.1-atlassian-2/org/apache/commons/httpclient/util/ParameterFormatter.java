/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.util;

import org.apache.commons.httpclient.NameValuePair;

public class ParameterFormatter {
    private static final char[] SEPARATORS = new char[]{'(', ')', '<', '>', '@', ',', ';', ':', '\\', '\"', '/', '[', ']', '?', '=', '{', '}', ' ', '\t'};
    private static final char[] UNSAFE_CHARS = new char[]{'\"', '\\'};
    private boolean alwaysUseQuotes = true;

    private static boolean isOneOf(char[] chars, char ch) {
        for (int i = 0; i < chars.length; ++i) {
            if (ch != chars[i]) continue;
            return true;
        }
        return false;
    }

    private static boolean isUnsafeChar(char ch) {
        return ParameterFormatter.isOneOf(UNSAFE_CHARS, ch);
    }

    private static boolean isSeparator(char ch) {
        return ParameterFormatter.isOneOf(SEPARATORS, ch);
    }

    public boolean isAlwaysUseQuotes() {
        return this.alwaysUseQuotes;
    }

    public void setAlwaysUseQuotes(boolean alwaysUseQuotes) {
        this.alwaysUseQuotes = alwaysUseQuotes;
    }

    public static void formatValue(StringBuffer buffer, String value, boolean alwaysUseQuotes) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value buffer may not be null");
        }
        if (alwaysUseQuotes) {
            buffer.append('\"');
            for (int i = 0; i < value.length(); ++i) {
                char ch = value.charAt(i);
                if (ParameterFormatter.isUnsafeChar(ch)) {
                    buffer.append('\\');
                }
                buffer.append(ch);
            }
            buffer.append('\"');
        } else {
            int offset = buffer.length();
            boolean unsafe = false;
            for (int i = 0; i < value.length(); ++i) {
                char ch = value.charAt(i);
                if (ParameterFormatter.isSeparator(ch)) {
                    unsafe = true;
                }
                if (ParameterFormatter.isUnsafeChar(ch)) {
                    buffer.append('\\');
                }
                buffer.append(ch);
            }
            if (unsafe) {
                buffer.insert(offset, '\"');
                buffer.append('\"');
            }
        }
    }

    public void format(StringBuffer buffer, NameValuePair param) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        }
        if (param == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        buffer.append(param.getName());
        String value = param.getValue();
        if (value != null) {
            buffer.append("=");
            ParameterFormatter.formatValue(buffer, value, this.alwaysUseQuotes);
        }
    }

    public String format(NameValuePair param) {
        StringBuffer buffer = new StringBuffer();
        this.format(buffer, param);
        return buffer.toString();
    }
}

