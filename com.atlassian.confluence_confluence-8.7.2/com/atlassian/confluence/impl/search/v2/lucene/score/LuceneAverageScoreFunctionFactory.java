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
import java.util.List;
import java.util.Optional;
import org.apache.lucene.index.AtomicReader;

public class LuceneAverageScoreFunctionFactory
implements LuceneScoreFunctionFactory {
    private final List<LuceneScoreFunctionFactory> functionFactories;
    private final List<Double> weights;

    public LuceneAverageScoreFunctionFactory(List<? extends LuceneScoreFunctionFactory> functionFactories, List<Double> weights) {
        this.functionFactories = ImmutableList.copyOf(functionFactories);
        this.weights = ImmutableList.copyOf(weights);
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        ArrayList<LuceneScoreFunction> functions = new ArrayList<LuceneScoreFunction>();
        for (LuceneScoreFunctionFactory factory : this.functionFactories) {
            functions.add(factory.create(reader));
        }
        return docId -> {
            double nominator = 0.0;
            double denominator = 0.0;
            for (int i = 0; i < functions.size(); ++i) {
                Optional<Double> score = ((LuceneScoreFunction)functions.get(i)).applyOptional(docId);
                if (!score.isPresent()) continue;
                nominator += this.weights.get(i) * score.get();
                denominator += this.weights.get(i).doubleValue();
            }
            return denominator == 0.0 ? 1.0 : nominator / denominator;
        };
    }
}

