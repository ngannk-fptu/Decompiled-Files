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
import org.apache.lucene.index.AtomicReader;

public class LuceneMaxScoreFunctionFactory
implements LuceneScoreFunctionFactory {
    private final Collection<LuceneScoreFunctionFactory> functionFactories;

    public LuceneMaxScoreFunctionFactory(Collection<? extends LuceneScoreFunctionFactory> functionFactories) {
        this.functionFactories = ImmutableList.copyOf(functionFactories);
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        ArrayList<LuceneScoreFunction> functions = new ArrayList<LuceneScoreFunction>();
        for (LuceneScoreFunctionFactory factory : this.functionFactories) {
            functions.add(factory.create(reader));
        }
        return docId -> {
            double result = 0.0;
            for (LuceneScoreFunction f : functions) {
                result = Math.max(result, f.apply(docId));
            }
            return result;
        };
    }
}

