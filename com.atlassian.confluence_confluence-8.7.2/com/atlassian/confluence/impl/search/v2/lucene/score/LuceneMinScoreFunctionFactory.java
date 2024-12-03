/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.stream.Streams$FailableStream
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
import org.apache.commons.lang3.stream.Streams;
import org.apache.lucene.index.AtomicReader;

public class LuceneMinScoreFunctionFactory
implements LuceneScoreFunctionFactory {
    private final Collection<LuceneScoreFunctionFactory> functionFactories;

    public LuceneMinScoreFunctionFactory(Collection<? extends LuceneScoreFunctionFactory> functionFactories) {
        this.functionFactories = ImmutableList.copyOf(functionFactories);
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        ArrayList<LuceneScoreFunction> functions = new ArrayList<LuceneScoreFunction>();
        for (LuceneScoreFunctionFactory factory : this.functionFactories) {
            functions.add(factory.create(reader));
        }
        return docId -> new Streams.FailableStream(functions.stream()).map(f -> f.applyOptional(docId)).stream().filter(Optional::isPresent).mapToDouble(Optional::get).min().orElse(1.0);
    }
}

