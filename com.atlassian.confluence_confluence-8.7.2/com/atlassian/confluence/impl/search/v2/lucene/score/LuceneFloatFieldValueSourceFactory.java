/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.search.FieldCache$Floats
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.search.FieldCache;

public class LuceneFloatFieldValueSourceFactory
implements LuceneScoreFunctionFactory {
    private final String fieldName;

    public LuceneFloatFieldValueSourceFactory(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        FieldCache.Floats data = FieldCache.DEFAULT.getFloats(reader, this.fieldName, false);
        return docId -> data.get(docId);
    }
}

