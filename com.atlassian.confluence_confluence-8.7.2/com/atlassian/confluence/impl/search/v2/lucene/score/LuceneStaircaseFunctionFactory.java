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
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import org.apache.lucene.index.AtomicReader;

public class LuceneStaircaseFunctionFactory
implements LuceneScoreFunctionFactory {
    private final LuceneScoreFunctionFactory sourceFactory;
    private final double[] input;
    private final double[] output;

    public LuceneStaircaseFunctionFactory(LuceneScoreFunctionFactory sourceFactory, SortedMap<Double, Double> staircases) {
        this.sourceFactory = sourceFactory;
        this.input = new double[staircases.size()];
        this.output = new double[staircases.size()];
        int i = 0;
        for (Map.Entry<Double, Double> entry : staircases.entrySet()) {
            this.input[i] = entry.getKey();
            this.output[i] = entry.getValue();
            ++i;
        }
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        if (this.input.length == 0) {
            return docId -> 1.0;
        }
        LuceneScoreFunction source = this.sourceFactory.create(reader);
        return docId -> {
            double value = source.apply(docId);
            int i = Arrays.binarySearch(this.input, value);
            if (i >= 0) {
                return this.output[i];
            }
            if (i == -1) {
                return 1.0;
            }
            return this.output[-i - 2];
        };
    }
}

