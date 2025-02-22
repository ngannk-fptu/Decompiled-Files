/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.similarity;

import java.util.Arrays;
import org.apache.commons.text.similarity.EditDistance;

public class LevenshteinDistance
implements EditDistance<Integer> {
    private static final LevenshteinDistance DEFAULT_INSTANCE = new LevenshteinDistance();
    private final Integer threshold;

    public static LevenshteinDistance getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    private static int limitedCompare(CharSequence left, CharSequence right, int threshold) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
        int n = left.length();
        int m = right.length();
        if (n == 0) {
            return m <= threshold ? m : -1;
        }
        if (m == 0) {
            return n <= threshold ? n : -1;
        }
        if (n > m) {
            CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
        }
        if (m - n > threshold) {
            return -1;
        }
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        int boundary = Math.min(n, threshold) + 1;
        for (int i = 0; i < boundary; ++i) {
            p[i] = i;
        }
        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
        Arrays.fill(d, Integer.MAX_VALUE);
        for (int j = 1; j <= m; ++j) {
            int max;
            char rightJ = right.charAt(j - 1);
            d[0] = j;
            int min = Math.max(1, j - threshold);
            int n2 = max = j > Integer.MAX_VALUE - threshold ? n : Math.min(n, j + threshold);
            if (min > 1) {
                d[min - 1] = Integer.MAX_VALUE;
            }
            int lowerBound = Integer.MAX_VALUE;
            for (int i = min; i <= max; ++i) {
                d[i] = left.charAt(i - 1) == rightJ ? p[i - 1] : 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
                lowerBound = Math.min(lowerBound, d[i]);
            }
            if (lowerBound > threshold) {
                return -1;
            }
            int[] tempD = p;
            p = d;
            d = tempD;
        }
        if (p[n] <= threshold) {
            return p[n];
        }
        return -1;
    }

    private static int unlimitedCompare(CharSequence left, CharSequence right) {
        int i;
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        int n = left.length();
        int m = right.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        if (n > m) {
            CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
        }
        int[] p = new int[n + 1];
        for (i = 0; i <= n; ++i) {
            p[i] = i;
        }
        for (int j = 1; j <= m; ++j) {
            int upperLeft = p[0];
            char rightJ = right.charAt(j - 1);
            p[0] = j;
            for (i = 1; i <= n; ++i) {
                int upper = p[i];
                int cost = left.charAt(i - 1) == rightJ ? 0 : 1;
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
                upperLeft = upper;
            }
        }
        return p[n];
    }

    public LevenshteinDistance() {
        this(null);
    }

    public LevenshteinDistance(Integer threshold) {
        if (threshold != null && threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
        this.threshold = threshold;
    }

    @Override
    public Integer apply(CharSequence left, CharSequence right) {
        if (this.threshold != null) {
            return LevenshteinDistance.limitedCompare(left, right, this.threshold);
        }
        return LevenshteinDistance.unlimitedCompare(left, right);
    }

    public Integer getThreshold() {
        return this.threshold;
    }
}

