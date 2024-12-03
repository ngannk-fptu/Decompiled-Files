/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import java.util.StringTokenizer;

public final class StringTokenizerUtils {
    public static String[] tokenizeToArray(String string, String string2, boolean bl) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, string2, bl);
        String[] stringArray = new String[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            stringArray[n] = stringTokenizer.nextToken();
            ++n;
        }
        return stringArray;
    }

    public static String[] tokenizeToArray(String string, String string2) {
        return StringTokenizerUtils.tokenizeToArray(string, string2, false);
    }

    public static String[] tokenizeToArray(String string) {
        return StringTokenizerUtils.tokenizeToArray(string, " \t\r\n");
    }
}

