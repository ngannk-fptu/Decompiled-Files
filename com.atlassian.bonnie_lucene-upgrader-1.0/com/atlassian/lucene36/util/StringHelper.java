/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.SimpleStringInterner;
import com.atlassian.lucene36.util.StringInterner;
import java.util.Comparator;
import java.util.StringTokenizer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class StringHelper {
    public static StringInterner interner = new SimpleStringInterner(1024, 8);
    private static Comparator<String> versionComparator = new Comparator<String>(){

        @Override
        public int compare(String a, String b) {
            StringTokenizer aTokens = new StringTokenizer(a, ".");
            StringTokenizer bTokens = new StringTokenizer(b, ".");
            while (aTokens.hasMoreTokens()) {
                int aToken = Integer.parseInt(aTokens.nextToken());
                if (bTokens.hasMoreTokens()) {
                    int bToken = Integer.parseInt(bTokens.nextToken());
                    if (aToken == bToken) continue;
                    return aToken < bToken ? -1 : 1;
                }
                if (aToken == 0) continue;
                return 1;
            }
            while (bTokens.hasMoreTokens()) {
                if (Integer.parseInt(bTokens.nextToken()) == 0) continue;
                return -1;
            }
            return 0;
        }
    };

    public static String intern(String s) {
        return interner.intern(s);
    }

    public static final int bytesDifference(byte[] bytes1, int len1, byte[] bytes2, int len2) {
        int len = len1 < len2 ? len1 : len2;
        for (int i = 0; i < len; ++i) {
            if (bytes1[i] == bytes2[i]) continue;
            return i;
        }
        return len;
    }

    private StringHelper() {
    }

    public static Comparator<String> getVersionComparator() {
        return versionComparator;
    }
}

