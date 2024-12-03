/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.similarities.TFIDFSimilarity
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.IDFValueSource;
import org.apache.lucene.queries.function.valuesource.TermFreqValueSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

public class TFValueSource
extends TermFreqValueSource {
    public TFValueSource(String field, String val, String indexedField, BytesRef indexedBytes) {
        super(field, val, indexedField, indexedBytes);
    }

    @Override
    public String name() {
        return "tf";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        Fields fields = readerContext.reader().fields();
        final Terms terms = fields.terms(this.indexedField);
        IndexSearcher searcher = (IndexSearcher)context.get("searcher");
        final TFIDFSimilarity similarity = IDFValueSource.asTFIDF(searcher.getSimilarity(), this.indexedField);
        if (similarity == null) {
            throw new UnsupportedOperationException("requires a TFIDFSimilarity (such as DefaultSimilarity)");
        }
        return new FloatDocValues(this){
            DocsEnum docs;
            int atDoc;
            int lastDocRequested;
            {
                super(vs);
                this.lastDocRequested = -1;
                this.reset();
            }

            public void reset() throws IOException {
                TermsEnum termsEnum;
                this.docs = terms != null ? ((termsEnum = terms.iterator(null)).seekExact(TFValueSource.this.indexedBytes, false) ? termsEnum.docs(null, null) : null) : null;
                if (this.docs == null) {
                    this.docs = new DocsEnum(){

                        public int freq() {
                            return 0;
                        }

                        public int docID() {
                            return Integer.MAX_VALUE;
                        }

                        public int nextDoc() {
                            return Integer.MAX_VALUE;
                        }

                        public int advance(int target) {
                            return Integer.MAX_VALUE;
                        }

                        public long cost() {
                            return 0L;
                        }
                    };
                }
                this.atDoc = -1;
            }

            @Override
            public float floatVal(int doc) {
                try {
                    if (doc < this.lastDocRequested) {
                        this.reset();
                    }
                    this.lastDocRequested = doc;
                    if (this.atDoc < doc) {
                        this.atDoc = this.docs.advance(doc);
                    }
                    if (this.atDoc > doc) {
                        return similarity.tf(0.0f);
                    }
                    return similarity.tf((float)this.docs.freq());
                }
                catch (IOException e) {
                    throw new RuntimeException("caught exception in function " + TFValueSource.this.description() + " : doc=" + doc, e);
                }
            }
        };
    }
}

