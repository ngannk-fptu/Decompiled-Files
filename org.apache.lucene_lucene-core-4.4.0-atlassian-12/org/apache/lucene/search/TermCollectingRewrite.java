/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;

abstract class TermCollectingRewrite<Q extends Query>
extends MultiTermQuery.RewriteMethod {
    TermCollectingRewrite() {
    }

    protected abstract Q getTopLevelQuery() throws IOException;

    protected final void addClause(Q topLevel, Term term, int docCount, float boost) throws IOException {
        this.addClause(topLevel, term, docCount, boost, null);
    }

    protected abstract void addClause(Q var1, Term var2, int var3, float var4, TermContext var5) throws IOException;

    final void collectTerms(IndexReader reader, MultiTermQuery query, TermCollector collector) throws IOException {
        IndexReaderContext topReaderContext = reader.getContext();
        Comparator<BytesRef> lastTermComp = null;
        for (AtomicReaderContext context : topReaderContext.leaves()) {
            BytesRef bytes;
            Terms terms;
            Fields fields = context.reader().fields();
            if (fields == null || (terms = fields.terms(query.field)) == null) continue;
            TermsEnum termsEnum = this.getTermsEnum(query, terms, collector.attributes);
            assert (termsEnum != null);
            if (termsEnum == TermsEnum.EMPTY) continue;
            Comparator<BytesRef> newTermComp = termsEnum.getComparator();
            if (lastTermComp != null && newTermComp != null && newTermComp != lastTermComp) {
                throw new RuntimeException("term comparator should not change between segments: " + lastTermComp + " != " + newTermComp);
            }
            lastTermComp = newTermComp;
            collector.setReaderContext(topReaderContext, context);
            collector.setNextEnum(termsEnum);
            while ((bytes = termsEnum.next()) != null) {
                if (collector.collect(bytes)) continue;
                return;
            }
        }
    }

    static abstract class TermCollector {
        protected AtomicReaderContext readerContext;
        protected IndexReaderContext topReaderContext;
        public final AttributeSource attributes = new AttributeSource();

        TermCollector() {
        }

        public void setReaderContext(IndexReaderContext topReaderContext, AtomicReaderContext readerContext) {
            this.readerContext = readerContext;
            this.topReaderContext = topReaderContext;
        }

        public abstract boolean collect(BytesRef var1) throws IOException;

        public abstract void setNextEnum(TermsEnum var1) throws IOException;
    }
}

