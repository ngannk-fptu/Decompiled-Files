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

public class LuceneSumScoreFunctionFactory
implements LuceneScoreFunctionFactory {
    private final Collection<LuceneScoreFunctionFactory> functionFactories;
    private final Collection<Double> constants;

    public LuceneSumScoreFunctionFactory(Collection<? extends LuceneScoreFunctionFactory> functionFactories, Collection<Double> constants) {
        this.functionFactories = ImmutableList.copyOf(functionFactories);
        this.constants = ImmutableList.copyOf(constants);
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
                result += f.apply(docId);
            }
            for (Double constant : this.constants) {
                result += constant.doubleValue();
            }
            return result;
        };
    }
}

