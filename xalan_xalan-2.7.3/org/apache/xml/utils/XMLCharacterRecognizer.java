/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

public class XMLCharacterRecognizer {
    public static boolean isWhiteSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }

    public static boolean isWhiteSpace(char[] ch, int start, int length) {
        int end = start + length;
        for (int s = start; s < end; ++s) {
            if (XMLCharacterRecognizer.isWhiteSpace(ch[s])) continue;
            return false;
        }
        return true;
    }

    public static boolean isWhiteSpace(CharSequence buf) {
        int n = buf.length();
        for (int i = 0; i < n; ++i) {
            if (XMLCharacterRecognizer.isWhiteSpace(buf.charAt(i))) continue;
            return false;
        }
        return true;
    }

    @Deprecated
    public static boolean isWhiteSpace(StringBuffer buf) {
        int n = buf.length();
        for (int i = 0; i < n; ++i) {
            if (XMLCharacterRecognizer.isWhiteSpace(buf.charAt(i))) continue;
            return false;
        }
        return true;
    }

    @Deprecated
    public static boolean isWhiteSpace(String s) {
        if (null != s) {
            int n = s.length();
            for (int i = 0; i < n; ++i) {
                if (XMLCharacterRecognizer.isWhiteSpace(s.charAt(i))) continue;
                return false;
            }
        }
        return true;
    }
}

