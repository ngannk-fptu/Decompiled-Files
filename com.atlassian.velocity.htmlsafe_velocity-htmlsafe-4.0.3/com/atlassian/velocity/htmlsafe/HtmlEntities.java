/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe;

public class HtmlEntities {
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
                    return HtmlEntities.encodeHeavy(text, j);
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
}

