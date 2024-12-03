/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoringRewrite;
import org.apache.lucene.search.TopTermsRewrite;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;

public class SpanMultiTermQueryWrapper<Q extends MultiTermQuery>
extends SpanQuery {
    protected final Q query;
    public static final SpanRewriteMethod SCORING_SPAN_QUERY_REWRITE = new SpanRewriteMethod(){
        private final ScoringRewrite<SpanOrQuery> delegate = new ScoringRewrite<SpanOrQuery>(){

            @Override
            protected SpanOrQuery getTopLevelQuery() {
                return new SpanOrQuery(new SpanQuery[0]);
            }

            @Override
            protected void checkMaxClauseCount(int count) {
            }

            @Override
            protected void addClause(SpanOrQuery topLevel, Term term, int docCount, float boost, TermContext states) {
                SpanTermQuery q = new SpanTermQuery(term);
                q.setBoost(boost);
                topLevel.addClause(q);
            }
        };

        @Override
        public SpanQuery rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
            return this.delegate.rewrite(reader, query);
        }
    };

    public SpanMultiTermQueryWrapper(Q query) {
        this.query = query;
        MultiTermQuery.RewriteMethod method = ((MultiTermQuery)query).getRewriteMethod();
        if (method instanceof TopTermsRewrite) {
            int pqsize = ((TopTermsRewrite)method).getSize();
            this.setRewriteMethod(new TopTermsSpanBooleanQueryRewrite(pqsize));
        } else {
            this.setRewriteMethod(SCORING_SPAN_QUERY_REWRITE);
        }
    }

    public final SpanRewriteMethod getRewriteMethod() {
        MultiTermQuery.RewriteMethod m = ((MultiTermQuery)this.query).getRewriteMethod();
        if (!(m instanceof SpanRewriteMethod)) {
            throw new UnsupportedOperationException("You can only use SpanMultiTermQueryWrapper with a suitable SpanRewriteMethod.");
        }
        return (SpanRewriteMethod)m;
    }

    public final void setRewriteMethod(SpanRewriteMethod rewriteMethod) {
        ((MultiTermQuery)this.query).setRewriteMethod(rewriteMethod);
    }

    @Override
    public Spans getSpans(AtomicReaderContext context, Bits acceptDocs, Map<Term, TermContext> termContexts) throws IOException {
        throw new UnsupportedOperationException("Query should have been rewritten");
    }

    @Override
    public String getField() {
        return ((MultiTermQuery)this.query).getField();
    }

    @Override
    public String toString(String field) {
        StringBuilder builder = new StringBuilder();
        builder.append("SpanMultiTermQueryWrapper(");
        builder.append(((Query)this.query).toString(field));
        builder.append(")");
        return builder.toString();
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        Query q = ((MultiTermQuery)this.query).rewrite(reader);
        if (!(q instanceof SpanQuery)) {
            throw new UnsupportedOperationException("You can only use SpanMultiTermQueryWrapper with a suitable SpanRewriteMethod.");
        }
        return q;
    }

    @Override
    public int hashCode() {
        return 31 * ((MultiTermQuery)this.query).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        SpanMultiTermQueryWrapper other = (SpanMultiTermQueryWrapper)obj;
        return ((MultiTermQuery)this.query).equals(other.query);
    }

    public static final class TopTermsSpanBooleanQueryRewrite
    extends SpanRewriteMethod {
        private final TopTermsRewrite<SpanOrQuery> delegate;

        public TopTermsSpanBooleanQueryRewrite(int size) {
            this.delegate = new TopTermsRewrite<SpanOrQuery>(size){

                @Override
                protected int getMaxSize() {
                    return Integer.MAX_VALUE;
                }

                @Override
                protected SpanOrQuery getTopLevelQuery() {
                    return new SpanOrQuery(new SpanQuery[0]);
                }

                @Override
                protected void addClause(SpanOrQuery topLevel, Term term, int docFreq, float boost, TermContext states) {
                    SpanTermQuery q = new SpanTermQuery(term);
                    q.setBoost(boost);
                    topLevel.addClause(q);
                }
            };
        }

        public int getSize() {
            return this.delegate.getSize();
        }

        @Override
        public SpanQuery rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
            return this.delegate.rewrite(reader, query);
        }

        public int hashCode() {
            return 31 * this.delegate.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            TopTermsSpanBooleanQueryRewrite other = (TopTermsSpanBooleanQueryRewrite)obj;
            return this.delegate.equals(other.delegate);
        }
    }

    public static abstract class SpanRewriteMethod
    extends MultiTermQuery.RewriteMethod {
        @Override
        public abstract SpanQuery rewrite(IndexReader var1, MultiTermQuery var2) throws IOException;
    }
}

