/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Comparator;
import java.util.StringTokenizer;
import org.apache.lucene.util.BytesRef;

public abstract class StringHelper {
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

    public static int bytesDifference(BytesRef left, BytesRef right) {
        int len = left.length < right.length ? left.length : right.length;
        byte[] bytesLeft = left.bytes;
        int offLeft = left.offset;
        byte[] bytesRight = right.bytes;
        int offRight = right.offset;
        for (int i = 0; i < len; ++i) {
            if (bytesLeft[i + offLeft] == bytesRight[i + offRight]) continue;
            return i;
        }
        return len;
    }

    private StringHelper() {
    }

    public static Comparator<String> getVersionComparator() {
        return versionComparator;
    }

    public static boolean equals(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        }
        return s1.equals(s2);
    }

    public static boolean startsWith(BytesRef ref, BytesRef prefix) {
        return StringHelper.sliceEquals(ref, prefix, 0);
    }

    public static boolean endsWith(BytesRef ref, BytesRef suffix) {
        return StringHelper.sliceEquals(ref, suffix, ref.length - suffix.length);
    }

    private static boolean sliceEquals(BytesRef sliceToTest, BytesRef other, int pos) {
        if (pos < 0 || sliceToTest.length - pos < other.length) {
            return false;
        }
        int i = sliceToTest.offset + pos;
        int j = other.offset;
        int k = other.offset + other.length;
        while (j < k) {
            if (sliceToTest.bytes[i++] == other.bytes[j++]) continue;
            return false;
        }
        return true;
    }
}

