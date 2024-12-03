/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import java.io.IOException;
import org.apache.lucene.index.AtomicReader;

public class LuceneConstantScoreFunctionFactory
implements LuceneScoreFunctionFactory {
    private final double weight;

    public LuceneConstantScoreFunctionFactory(double weight) {
        this.weight = weight;
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        return docId -> this.weight;
    }
}

