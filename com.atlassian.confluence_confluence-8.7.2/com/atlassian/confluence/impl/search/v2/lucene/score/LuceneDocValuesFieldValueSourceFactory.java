/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.util.BytesRef
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.BytesRef;

public class LuceneDocValuesFieldValueSourceFactory
implements LuceneScoreFunctionFactory {
    private final String fieldName;
    private final Function<byte[], Double> extractor;

    public LuceneDocValuesFieldValueSourceFactory(String fieldName, Function<byte[], Double> extractor) {
        this.fieldName = fieldName;
        this.extractor = extractor;
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        BinaryDocValues docValues = FieldCache.DEFAULT.getTerms(reader, this.fieldName);
        return docId -> {
            BytesRef byteRef = new BytesRef();
            docValues.get(docId, byteRef);
            byte[] bytes = Arrays.copyOfRange(byteRef.bytes, byteRef.offset, byteRef.offset + byteRef.length);
            return this.extractor.apply(bytes);
        };
    }
}

