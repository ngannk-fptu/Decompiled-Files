/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package org.apache.commons.text.similarity;

import org.apache.commons.lang3.Validate;
import org.apache.commons.text.similarity.SimilarityScore;

public class SimilarityScoreFrom<R> {
    private final SimilarityScore<R> similarityScore;
    private final CharSequence left;

    public SimilarityScoreFrom(SimilarityScore<R> similarityScore, CharSequence left) {
        Validate.isTrue((similarityScore != null ? 1 : 0) != 0, (String)"The edit distance may not be null.", (Object[])new Object[0]);
        this.similarityScore = similarityScore;
        this.left = left;
    }

    public R apply(CharSequence right) {
        return this.similarityScore.apply(this.left, right);
    }

    public CharSequence getLeft() {
        return this.left;
    }

    public SimilarityScore<R> getSimilarityScore() {
        return this.similarityScore;
    }
}

