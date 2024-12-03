/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.util;

public final class StringUtils {
    public static boolean isBlank(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (Character.isWhitespace(s.charAt(i))) continue;
            return false;
        }
        return true;
    }
}

