/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license.util;

@Deprecated
public class StringUtils {
    public static String replaceAll(String str, String oldPattern, String newPattern) {
        if (str == null) {
            return null;
        }
        return str.replace(oldPattern, newPattern);
    }
}

