/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import java.io.IOException;
import java.util.Optional;

public interface LuceneScoreFunction {
    public double apply(int var1) throws IOException;

    default public Optional<Double> applyOptional(int docId) throws IOException {
        return Optional.of(this.apply(docId));
    }
}

