/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.text.similarity;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.SimilarityScore;

public class JaroWinklerSimilarity
implements SimilarityScore<Double> {
    protected static int[] matches(CharSequence first, CharSequence second) {
        int i;
        CharSequence min;
        CharSequence max;
        if (first.length() > second.length()) {
            max = first;
            min = second;
        } else {
            max = second;
            min = first;
        }
        int range = Math.max(max.length() / 2 - 1, 0);
        int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;
        block0: for (int mi = 0; mi < min.length(); ++mi) {
            char c1 = min.charAt(mi);
            int xn = Math.min(mi + range + 1, max.length());
            for (int xi = Math.max(mi - range, 0); xi < xn; ++xi) {
                if (matchFlags[xi] || c1 != max.charAt(xi)) continue;
                matchIndexes[mi] = xi;
                matchFlags[xi] = true;
                ++matches;
                continue block0;
            }
        }
        char[] ms1 = new char[matches];
        char[] ms2 = new char[matches];
        int si = 0;
        for (i = 0; i < min.length(); ++i) {
            if (matchIndexes[i] == -1) continue;
            ms1[si] = min.charAt(i);
            ++si;
        }
        si = 0;
        for (i = 0; i < max.length(); ++i) {
            if (!matchFlags[i]) continue;
            ms2[si] = max.charAt(i);
            ++si;
        }
        int halfTranspositions = 0;
        for (int mi = 0; mi < ms1.length; ++mi) {
            if (ms1[mi] == ms2[mi]) continue;
            ++halfTranspositions;
        }
        int prefix = 0;
        for (int mi = 0; mi < Math.min(4, min.length()) && first.charAt(mi) == second.charAt(mi); ++mi) {
            ++prefix;
        }
        return new int[]{matches, halfTranspositions, prefix};
    }

    @Override
    public Double apply(CharSequence left, CharSequence right) {
        double defaultScalingFactor = 0.1;
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        if (StringUtils.equals((CharSequence)left, (CharSequence)right)) {
            return 1.0;
        }
        int[] mtp = JaroWinklerSimilarity.matches(left, right);
        double m = mtp[0];
        if (m == 0.0) {
            return 0.0;
        }
        double j = (m / (double)left.length() + m / (double)right.length() + (m - (double)mtp[1] / 2.0) / m) / 3.0;
        return j < 0.7 ? j : j + 0.1 * (double)mtp[2] * (1.0 - j);
    }
}

