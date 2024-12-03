/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import com.atlassian.confluence.search.v2.score.FieldValueFactorFunction;
import java.io.IOException;
import org.apache.lucene.index.AtomicReader;

public class LuceneFactorScoreFunctionFactory
implements LuceneScoreFunctionFactory {
    private final LuceneScoreFunctionFactory sourceFactory;
    private final double factor;
    private final FieldValueFactorFunction.Modifier modifier;

    public LuceneFactorScoreFunctionFactory(LuceneScoreFunctionFactory sourceFactory, double factor, FieldValueFactorFunction.Modifier modifier) {
        this.sourceFactory = sourceFactory;
        this.factor = factor;
        this.modifier = modifier;
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        LuceneScoreFunction source = this.sourceFactory.create(reader);
        return docId -> {
            double value = source.apply(docId);
            return this.modifier.apply(this.factor * value);
        };
    }
}

