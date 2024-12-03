/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.util.Comparator;

public class StringComparator
implements Comparator<String> {
    public static final StringComparator COMPARATOR = new StringComparator();

    @Override
    public int compare(String s1, String s2) {
        int n2;
        int n1 = s1.length();
        int min = n1 < (n2 = s2.length()) ? n1 : n2;
        for (int i = 0; i < min; ++i) {
            char c2;
            char c1 = s1.charAt(i);
            if (c1 == (c2 = s2.charAt(i)) || !(c1 <= '\u0080' && c2 <= '\u0080' ? (c1 = StringComparator.toLowerCaseFast(c1)) != (c2 = StringComparator.toLowerCaseFast(c2)) : (c1 = Character.toUpperCase(c1)) != (c2 = Character.toUpperCase(c2)) && (c1 = Character.toLowerCase(c1)) != (c2 = Character.toLowerCase(c2)))) continue;
            return c1 - c2;
        }
        return n1 - n2;
    }

    private static char toLowerCaseFast(char ch) {
        return ch >= 'A' && ch <= 'Z' ? (char)(ch + 97 - 65) : ch;
    }
}

