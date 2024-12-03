/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
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
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.queries.function.valuesource.DocFreqValueSource;
import org.apache.lucene.util.BytesRef;

public class TermFreqValueSource
extends DocFreqValueSource {
    public TermFreqValueSource(String field, String val, String indexedField, BytesRef indexedBytes) {
        super(field, val, indexedField, indexedBytes);
    }

    @Override
    public String name() {
        return "termfreq";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        Fields fields = readerContext.reader().fields();
        final Terms terms = fields.terms(this.indexedField);
        return new IntDocValues(this){
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
                this.docs = terms != null ? ((termsEnum = terms.iterator(null)).seekExact(TermFreqValueSource.this.indexedBytes, false) ? termsEnum.docs(null, null) : null) : null;
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
            public int intVal(int doc) {
                try {
                    if (doc < this.lastDocRequested) {
                        this.reset();
                    }
                    this.lastDocRequested = doc;
                    if (this.atDoc < doc) {
                        this.atDoc = this.docs.advance(doc);
                    }
                    if (this.atDoc > doc) {
                        return 0;
                    }
                    return this.docs.freq();
                }
                catch (IOException e) {
                    throw new RuntimeException("caught exception in function " + TermFreqValueSource.this.description() + " : doc=" + doc, e);
                }
            }
        };
    }
}

