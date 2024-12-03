/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.util;

public class StemmerUtil {
    private StemmerUtil() {
    }

    public static boolean startsWith(char[] s, int len, String prefix) {
        int prefixLen = prefix.length();
        if (prefixLen > len) {
            return false;
        }
        for (int i = 0; i < prefixLen; ++i) {
            if (s[i] == prefix.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public static boolean endsWith(char[] s, int len, String suffix) {
        int suffixLen = suffix.length();
        if (suffixLen > len) {
            return false;
        }
        for (int i = suffixLen - 1; i >= 0; --i) {
            if (s[len - (suffixLen - i)] == suffix.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public static boolean endsWith(char[] s, int len, char[] suffix) {
        int suffixLen = suffix.length;
        if (suffixLen > len) {
            return false;
        }
        for (int i = suffixLen - 1; i >= 0; --i) {
            if (s[len - (suffixLen - i)] == suffix[i]) continue;
            return false;
        }
        return true;
    }

    public static int delete(char[] s, int pos, int len) {
        if (pos < len) {
            System.arraycopy(s, pos + 1, s, pos, len - pos - 1);
        }
        return len - 1;
    }

    public static int deleteN(char[] s, int pos, int len, int nChars) {
        for (int i = 0; i < nChars; ++i) {
            len = StemmerUtil.delete(s, pos, len);
        }
        return len;
    }
}

