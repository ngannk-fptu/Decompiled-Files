/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import com.atlassian.confluence.search.v2.score.DecayParameters;
import java.io.IOException;
import org.apache.lucene.index.AtomicReader;

public class LuceneLinearDecayFunctionFactory
implements LuceneScoreFunctionFactory {
    private final LuceneScoreFunctionFactory sourceFactory;
    private final DecayParameters params;

    public LuceneLinearDecayFunctionFactory(LuceneScoreFunctionFactory sourceFactory, DecayParameters params) {
        this.sourceFactory = sourceFactory;
        this.params = params;
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        LuceneScoreFunction source = this.sourceFactory.create(reader);
        return docId -> {
            double value = source.apply(docId);
            double s = this.params.getScale() / (1.0 - this.params.getDecay());
            return Math.max(0.0, (s - Math.max(0.0, Math.abs(value - this.params.getOrigin()) - this.params.getOffset())) / s);
        };
    }
}

