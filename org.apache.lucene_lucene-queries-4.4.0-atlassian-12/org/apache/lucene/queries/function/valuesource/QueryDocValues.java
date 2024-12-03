/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexReaderContext
 *  org.apache.lucene.index.ReaderUtil
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.Scorer
 *  org.apache.lucene.search.Weight
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueFloat
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.QueryValueSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueFloat;

class QueryDocValues
extends FloatDocValues {
    final AtomicReaderContext readerContext;
    final Bits acceptDocs;
    final Weight weight;
    final float defVal;
    final Map fcontext;
    final Query q;
    Scorer scorer;
    int scorerDoc;
    boolean noMatches = false;
    int lastDocRequested = Integer.MAX_VALUE;

    public QueryDocValues(QueryValueSource vs, AtomicReaderContext readerContext, Map fcontext) throws IOException {
        super(vs);
        Weight w;
        this.readerContext = readerContext;
        this.acceptDocs = readerContext.reader().getLiveDocs();
        this.defVal = vs.defVal;
        this.q = vs.q;
        this.fcontext = fcontext;
        Weight weight = w = fcontext == null ? null : (Weight)fcontext.get(vs);
        if (w == null) {
            IndexSearcher weightSearcher;
            if (fcontext == null) {
                weightSearcher = new IndexSearcher(ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext));
            } else {
                weightSearcher = (IndexSearcher)fcontext.get("searcher");
                if (weightSearcher == null) {
                    weightSearcher = new IndexSearcher(ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext));
                }
            }
            vs.createWeight(fcontext, weightSearcher);
            w = (Weight)fcontext.get(vs);
        }
        this.weight = w;
    }

    @Override
    public float floatVal(int doc) {
        try {
            if (doc < this.lastDocRequested) {
                if (this.noMatches) {
                    return this.defVal;
                }
                this.scorer = this.weight.scorer(this.readerContext, true, false, this.acceptDocs);
                if (this.scorer == null) {
                    this.noMatches = true;
                    return this.defVal;
                }
                this.scorerDoc = -1;
            }
            this.lastDocRequested = doc;
            if (this.scorerDoc < doc) {
                this.scorerDoc = this.scorer.advance(doc);
            }
            if (this.scorerDoc > doc) {
                return this.defVal;
            }
            return this.scorer.score();
        }
        catch (IOException e) {
            throw new RuntimeException("caught exception in QueryDocVals(" + this.q + ") doc=" + doc, e);
        }
    }

    @Override
    public boolean exists(int doc) {
        try {
            if (doc < this.lastDocRequested) {
                if (this.noMatches) {
                    return false;
                }
                this.scorer = this.weight.scorer(this.readerContext, true, false, this.acceptDocs);
                this.scorerDoc = -1;
                if (this.scorer == null) {
                    this.noMatches = true;
                    return false;
                }
            }
            this.lastDocRequested = doc;
            if (this.scorerDoc < doc) {
                this.scorerDoc = this.scorer.advance(doc);
            }
            return this.scorerDoc <= doc;
        }
        catch (IOException e) {
            throw new RuntimeException("caught exception in QueryDocVals(" + this.q + ") doc=" + doc, e);
        }
    }

    @Override
    public Object objectVal(int doc) {
        try {
            return this.exists(doc) ? Float.valueOf(this.scorer.score()) : null;
        }
        catch (IOException e) {
            throw new RuntimeException("caught exception in QueryDocVals(" + this.q + ") doc=" + doc, e);
        }
    }

    @Override
    public FunctionValues.ValueFiller getValueFiller() {
        return new FunctionValues.ValueFiller(){
            private final MutableValueFloat mval = new MutableValueFloat();

            @Override
            public MutableValue getValue() {
                return this.mval;
            }

            @Override
            public void fillValue(int doc) {
                try {
                    if (QueryDocValues.this.noMatches) {
                        this.mval.value = QueryDocValues.this.defVal;
                        this.mval.exists = false;
                        return;
                    }
                    QueryDocValues.this.scorer = QueryDocValues.this.weight.scorer(QueryDocValues.this.readerContext, true, false, QueryDocValues.this.acceptDocs);
                    QueryDocValues.this.scorerDoc = -1;
                    if (QueryDocValues.this.scorer == null) {
                        QueryDocValues.this.noMatches = true;
                        this.mval.value = QueryDocValues.this.defVal;
                        this.mval.exists = false;
                        return;
                    }
                    QueryDocValues.this.lastDocRequested = doc;
                    if (QueryDocValues.this.scorerDoc < doc) {
                        QueryDocValues.this.scorerDoc = QueryDocValues.this.scorer.advance(doc);
                    }
                    if (QueryDocValues.this.scorerDoc > doc) {
                        this.mval.value = QueryDocValues.this.defVal;
                        this.mval.exists = false;
                        return;
                    }
                    this.mval.value = QueryDocValues.this.scorer.score();
                    this.mval.exists = true;
                }
                catch (IOException e) {
                    throw new RuntimeException("caught exception in QueryDocVals(" + QueryDocValues.this.q + ") doc=" + doc, e);
                }
            }
        };
    }

    @Override
    public String toString(int doc) {
        return "query(" + this.q + ",def=" + this.defVal + ")=" + this.floatVal(doc);
    }
}

