/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.similarity;

import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.JaccardSimilarity;

public class JaccardDistance
implements EditDistance<Double> {
    private final JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();

    @Override
    public Double apply(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        return 1.0 - this.jaccardSimilarity.apply(left, right);
    }
}

