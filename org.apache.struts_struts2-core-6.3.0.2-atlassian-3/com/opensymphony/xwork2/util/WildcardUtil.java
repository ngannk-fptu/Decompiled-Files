/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import java.util.regex.Pattern;

public class WildcardUtil {
    private static final String theSpecialRegexCharList = ".[]\\?*+{}|()^$";

    public static Pattern compileWildcardPattern(String pattern) {
        StringBuilder buf = new StringBuilder(pattern);
        for (int i = buf.length() - 1; i >= 0; --i) {
            char c = buf.charAt(i);
            if (c == '*' && (i == 0 || buf.charAt(i - 1) != '\\')) {
                buf.insert(i + 1, '?');
                buf.insert(i, '.');
                continue;
            }
            if (c == '*') {
                --i;
                continue;
            }
            if (!WildcardUtil.needsBackslashToBeLiteralInRegex(c)) continue;
            buf.insert(i, '\\');
        }
        return Pattern.compile(buf.toString());
    }

    public static boolean needsBackslashToBeLiteralInRegex(char c) {
        return theSpecialRegexCharList.indexOf(c) >= 0;
    }
}

