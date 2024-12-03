/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.similarity;

import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

public class JaroWinklerDistance
implements EditDistance<Double> {
    @Deprecated
    public static final int INDEX_NOT_FOUND = -1;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    @Deprecated
    protected static int[] matches(CharSequence first, CharSequence second) {
        return JaroWinklerSimilarity.matches(first, second);
    }

    @Override
    public Double apply(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        return 1.0 - this.similarity.apply(left, right);
    }
}

