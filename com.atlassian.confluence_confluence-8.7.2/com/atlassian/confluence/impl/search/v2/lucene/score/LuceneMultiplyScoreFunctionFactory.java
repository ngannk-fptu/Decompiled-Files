/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.lucene.index.AtomicReader
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import org.apache.lucene.index.AtomicReader;

public class LuceneMultiplyScoreFunctionFactory
implements LuceneScoreFunctionFactory {
    private final Collection<LuceneScoreFunctionFactory> functionFactories;

    public LuceneMultiplyScoreFunctionFactory(Collection<? extends LuceneScoreFunctionFactory> functionFactories) {
        this.functionFactories = ImmutableList.copyOf(functionFactories);
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        ArrayList<LuceneScoreFunction> functions = new ArrayList<LuceneScoreFunction>();
        for (LuceneScoreFunctionFactory factory : this.functionFactories) {
            functions.add(factory.create(reader));
        }
        return docId -> {
            double result = 1.0;
            for (LuceneScoreFunction f : functions) {
                Optional<Double> score = f.applyOptional(docId);
                if (!score.isPresent()) continue;
                result *= score.get().doubleValue();
            }
            return result;
        };
    }
}

