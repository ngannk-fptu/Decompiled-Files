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

public class LuceneGaussDecayFunctionFactory
implements LuceneScoreFunctionFactory {
    private final LuceneScoreFunctionFactory sourceFactory;
    private final DecayParameters params;

    public LuceneGaussDecayFunctionFactory(LuceneScoreFunctionFactory sourceFactory, DecayParameters params) {
        this.sourceFactory = sourceFactory;
        this.params = params;
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        LuceneScoreFunction source = this.sourceFactory.create(reader);
        return docId -> {
            double value = source.apply(docId);
            double lambdaSquare = -Math.pow(this.params.getScale(), 2.0) / (2.0 * Math.log(this.params.getDecay()));
            double distanceFromOrigin = Math.abs(value - this.params.getOrigin());
            double distanceFromOriginAndOffset = Math.max(0.0, distanceFromOrigin - this.params.getOffset());
            return Math.exp(-Math.pow(distanceFromOriginAndOffset, 2.0) / (2.0 * lambdaSquare));
        };
    }
}

