/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.search.FieldCache$Ints
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.search.FieldCache;

public class LuceneIntFieldValueSourceFactory
implements LuceneScoreFunctionFactory {
    private final String fieldName;

    public LuceneIntFieldValueSourceFactory(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        FieldCache.Ints data = FieldCache.DEFAULT.getInts(reader, this.fieldName, false);
        return docId -> data.get(docId);
    }
}

