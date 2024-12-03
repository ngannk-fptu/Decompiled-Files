/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.search.FieldCache$Doubles
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.search.FieldCache;

public class LuceneDoubleFieldValueSourceFactory
implements LuceneScoreFunctionFactory {
    private final String fieldName;

    public LuceneDoubleFieldValueSourceFactory(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        FieldCache.Doubles data = FieldCache.DEFAULT.getDoubles(reader, this.fieldName, false);
        return arg_0 -> ((FieldCache.Doubles)data).get(arg_0);
    }
}

