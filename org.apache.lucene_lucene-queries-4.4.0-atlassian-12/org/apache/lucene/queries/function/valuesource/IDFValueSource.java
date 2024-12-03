/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.similarities.PerFieldSimilarityWrapper
 *  org.apache.lucene.search.similarities.Similarity
 *  org.apache.lucene.search.similarities.TFIDFSimilarity
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.valuesource.ConstDoubleDocValues;
import org.apache.lucene.queries.function.valuesource.DocFreqValueSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.PerFieldSimilarityWrapper;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

public class IDFValueSource
extends DocFreqValueSource {
    public IDFValueSource(String field, String val, String indexedField, BytesRef indexedBytes) {
        super(field, val, indexedField, indexedBytes);
    }

    @Override
    public String name() {
        return "idf";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        IndexSearcher searcher = (IndexSearcher)context.get("searcher");
        TFIDFSimilarity sim = IDFValueSource.asTFIDF(searcher.getSimilarity(), this.field);
        if (sim == null) {
            throw new UnsupportedOperationException("requires a TFIDFSimilarity (such as DefaultSimilarity)");
        }
        int docfreq = searcher.getIndexReader().docFreq(new Term(this.indexedField, this.indexedBytes));
        float idf = sim.idf((long)docfreq, (long)searcher.getIndexReader().maxDoc());
        return new ConstDoubleDocValues(idf, this);
    }

    static TFIDFSimilarity asTFIDF(Similarity sim, String field) {
        while (sim instanceof PerFieldSimilarityWrapper) {
            sim = ((PerFieldSimilarityWrapper)sim).get(field);
        }
        if (sim instanceof TFIDFSimilarity) {
            return (TFIDFSimilarity)sim;
        }
        return null;
    }
}

