/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.util;

public class RegExpUtil {
    private static final String regExpKeywords = "\\!-?*+.^|:{}[]()~";

    public static String convertToRegularExpression(String str) {
        String pattern = "";
        for (int i = 0; i < str.length(); ++i) {
            boolean alreadyEscaped;
            char ch = str.charAt(i);
            boolean bl = alreadyEscaped = i > 0 && str.charAt(i - 1) == '\\';
            if (!alreadyEscaped && regExpKeywords.indexOf(ch) != -1) {
                pattern = pattern + "\\";
            }
            pattern = pattern + ch;
        }
        return pattern;
    }
}

