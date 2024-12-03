/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.MultiFields
 *  org.apache.lucene.search.Scorer
 *  org.apache.lucene.util.Bits
 */
package org.apache.lucene.queries.function;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.Bits;

public class ValueSourceScorer
extends Scorer {
    protected final IndexReader reader;
    private int doc = -1;
    protected final int maxDoc;
    protected final FunctionValues values;
    protected boolean checkDeletes;
    private final Bits liveDocs;

    protected ValueSourceScorer(IndexReader reader, FunctionValues values) {
        super(null);
        this.reader = reader;
        this.maxDoc = reader.maxDoc();
        this.values = values;
        this.setCheckDeletes(true);
        this.liveDocs = MultiFields.getLiveDocs((IndexReader)reader);
    }

    public IndexReader getReader() {
        return this.reader;
    }

    public void setCheckDeletes(boolean checkDeletes) {
        this.checkDeletes = checkDeletes && this.reader.hasDeletions();
    }

    public boolean matches(int doc) {
        return (!this.checkDeletes || this.liveDocs.get(doc)) && this.matchesValue(doc);
    }

    public boolean matchesValue(int doc) {
        return true;
    }

    public int docID() {
        return this.doc;
    }

    public int nextDoc() throws IOException {
        do {
            ++this.doc;
            if (this.doc < this.maxDoc) continue;
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        } while (!this.matches(this.doc));
        return this.doc;
    }

    public int advance(int target) throws IOException {
        this.doc = target - 1;
        return this.nextDoc();
    }

    public float score() throws IOException {
        return this.values.floatVal(this.doc);
    }

    public int freq() throws IOException {
        return 1;
    }

    public long cost() {
        return this.maxDoc;
    }
}

