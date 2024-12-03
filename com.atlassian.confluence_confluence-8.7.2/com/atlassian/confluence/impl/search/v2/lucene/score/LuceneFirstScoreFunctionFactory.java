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

public class LuceneFirstScoreFunctionFactory
implements LuceneScoreFunctionFactory {
    private final List<LuceneScoreFunctionFactory> functionFactories;

    public LuceneFirstScoreFunctionFactory(List<? extends LuceneScoreFunctionFactory> functionFactories) {
        this.functionFactories = ImmutableList.copyOf(functionFactories);
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        ArrayList<LuceneScoreFunction> functions = new ArrayList<LuceneScoreFunction>();
        for (LuceneScoreFunctionFactory factory : this.functionFactories) {
            functions.add(factory.create(reader));
        }
        return docId -> {
            for (LuceneScoreFunction function : functions) {
                Optional<Double> score = function.applyOptional(docId);
                if (!score.isPresent()) continue;
                return score.get();
            }
            return 1.0;
        };
    }
}

