/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.ConstIntDocValues;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;

public class DocFreqValueSource
extends ValueSource {
    protected final String field;
    protected final String indexedField;
    protected final String val;
    protected final BytesRef indexedBytes;

    public DocFreqValueSource(String field, String val, String indexedField, BytesRef indexedBytes) {
        this.field = field;
        this.val = val;
        this.indexedField = indexedField;
        this.indexedBytes = indexedBytes;
    }

    public String name() {
        return "docfreq";
    }

    @Override
    public String description() {
        return this.name() + '(' + this.field + ',' + this.val + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        IndexSearcher searcher = (IndexSearcher)context.get("searcher");
        int docfreq = searcher.getIndexReader().docFreq(new Term(this.indexedField, this.indexedBytes));
        return new ConstIntDocValues(docfreq, this);
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        context.put("searcher", searcher);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.indexedField.hashCode() * 29 + this.indexedBytes.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        DocFreqValueSource other = (DocFreqValueSource)o;
        return this.indexedField.equals(other.indexedField) && this.indexedBytes.equals((Object)other.indexedBytes);
    }
}

