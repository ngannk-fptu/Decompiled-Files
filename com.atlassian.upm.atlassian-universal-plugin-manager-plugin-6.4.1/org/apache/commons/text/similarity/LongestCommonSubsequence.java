/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.similarity;

import org.apache.commons.text.similarity.SimilarityScore;

public class LongestCommonSubsequence
implements SimilarityScore<Integer> {
    private static int[] algorithmB(CharSequence left, CharSequence right) {
        int m = left.length();
        int n = right.length();
        int[][] dpRows = new int[2][1 + n];
        for (int i = 1; i <= m; ++i) {
            int[] temp = dpRows[0];
            dpRows[0] = dpRows[1];
            dpRows[1] = temp;
            for (int j = 1; j <= n; ++j) {
                dpRows[1][j] = left.charAt(i - 1) == right.charAt(j - 1) ? dpRows[0][j - 1] + 1 : Math.max(dpRows[1][j - 1], dpRows[0][j]);
            }
        }
        return dpRows[1];
    }

    private static String algorithmC(CharSequence left, CharSequence right) {
        int m = left.length();
        int n = right.length();
        StringBuilder out = new StringBuilder();
        if (m == 1) {
            char leftCh = left.charAt(0);
            for (int j = 0; j < n; ++j) {
                if (leftCh != right.charAt(j)) continue;
                out.append(leftCh);
                break;
            }
        } else if (n > 0 && m > 1) {
            int mid = m / 2;
            CharSequence leftFirstPart = left.subSequence(0, mid);
            CharSequence leftSecondPart = left.subSequence(mid, m);
            int[] l1 = LongestCommonSubsequence.algorithmB(leftFirstPart, right);
            int[] l2 = LongestCommonSubsequence.algorithmB(LongestCommonSubsequence.reverse(leftSecondPart), LongestCommonSubsequence.reverse(right));
            int k = 0;
            int t = 0;
            for (int j = 0; j <= n; ++j) {
                int s = l1[j] + l2[n - j];
                if (t >= s) continue;
                t = s;
                k = j;
            }
            out.append(LongestCommonSubsequence.algorithmC(leftFirstPart, right.subSequence(0, k)));
            out.append(LongestCommonSubsequence.algorithmC(leftSecondPart, right.subSequence(k, n)));
        }
        return out.toString();
    }

    private static String reverse(CharSequence s) {
        return new StringBuilder(s).reverse().toString();
    }

    @Override
    public Integer apply(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Inputs must not be null");
        }
        int leftSz = left.length();
        int rightSz = right.length();
        if (leftSz == 0 || rightSz == 0) {
            return 0;
        }
        if (leftSz < rightSz) {
            return LongestCommonSubsequence.algorithmB(right, left)[leftSz];
        }
        return LongestCommonSubsequence.algorithmB(left, right)[rightSz];
    }

    @Deprecated
    public CharSequence logestCommonSubsequence(CharSequence left, CharSequence right) {
        return this.longestCommonSubsequence(left, right);
    }

    public CharSequence longestCommonSubsequence(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Inputs must not be null");
        }
        int leftSz = left.length();
        int rightSz = right.length();
        if (leftSz == 0 || rightSz == 0) {
            return "";
        }
        if (leftSz < rightSz) {
            return LongestCommonSubsequence.algorithmC(right, left);
        }
        return LongestCommonSubsequence.algorithmC(left, right);
    }

    @Deprecated
    public int[][] longestCommonSubstringLengthArray(CharSequence left, CharSequence right) {
        int[][] lcsLengthArray = new int[left.length() + 1][right.length() + 1];
        for (int i = 0; i < left.length(); ++i) {
            for (int j = 0; j < right.length(); ++j) {
                if (i == 0) {
                    lcsLengthArray[i][j] = 0;
                }
                if (j == 0) {
                    lcsLengthArray[i][j] = 0;
                }
                lcsLengthArray[i + 1][j + 1] = left.charAt(i) == right.charAt(j) ? lcsLengthArray[i][j] + 1 : Math.max(lcsLengthArray[i + 1][j], lcsLengthArray[i][j + 1]);
            }
        }
        return lcsLengthArray;
    }
}

