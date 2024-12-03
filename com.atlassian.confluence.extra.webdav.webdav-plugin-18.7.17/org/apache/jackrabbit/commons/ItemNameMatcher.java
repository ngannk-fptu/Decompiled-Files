/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import java.util.StringTokenizer;

public final class ItemNameMatcher {
    private static final char WILDCARD_CHAR = '*';
    private static final String OR = "|";

    private ItemNameMatcher() {
    }

    public static boolean matches(String name, String pattern) {
        StringTokenizer st = new StringTokenizer(pattern, OR, false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (!ItemNameMatcher.internalMatches(name, token, 0, 0)) continue;
            return true;
        }
        return false;
    }

    public static boolean matches(String name, String[] nameGlobs) {
        for (String nameGlob : nameGlobs) {
            if (!ItemNameMatcher.internalMatches(name, nameGlob, 0, 0)) continue;
            return true;
        }
        return false;
    }

    private static boolean internalMatches(String s, String pattern, int sOff, int pOff) {
        int pLen = pattern.length();
        int sLen = s.length();
        while (true) {
            if (pOff >= pLen) {
                if (sOff >= sLen) {
                    return true;
                }
                return s.charAt(sOff) == '[';
            }
            if (sOff >= sLen && pattern.charAt(pOff) != '*') {
                return false;
            }
            if (pattern.charAt(pOff) == '*') {
                if (++pOff >= pLen) {
                    return true;
                }
                while (true) {
                    if (ItemNameMatcher.internalMatches(s, pattern, sOff, pOff)) {
                        return true;
                    }
                    if (sOff >= sLen) {
                        return false;
                    }
                    ++sOff;
                }
            }
            if (pOff < pLen && sOff < sLen && pattern.charAt(pOff) != s.charAt(sOff)) {
                return false;
            }
            ++pOff;
            ++sOff;
        }
    }
}

