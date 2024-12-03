/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.ScoringRewrite;
import com.atlassian.lucene36.search.TopTermsRewrite;
import com.atlassian.lucene36.search.spans.SpanOrQuery;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.SpanTermQuery;
import com.atlassian.lucene36.search.spans.Spans;
import java.io.IOException;
import java.lang.reflect.Method;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SpanMultiTermQueryWrapper<Q extends MultiTermQuery>
extends SpanQuery {
    protected final Q query;
    private Method getFieldMethod = null;
    private Method getTermMethod = null;
    public static final SpanRewriteMethod SCORING_SPAN_QUERY_REWRITE = new SpanRewriteMethod(){
        private final ScoringRewrite<SpanOrQuery> delegate = new ScoringRewrite<SpanOrQuery>(){

            @Override
            protected SpanOrQuery getTopLevelQuery() {
                return new SpanOrQuery(new SpanQuery[0]);
            }

            @Override
            protected void addClause(SpanOrQuery topLevel, Term term, float boost) {
                SpanTermQuery q = new SpanTermQuery(term);
                q.setBoost(boost);
                topLevel.addClause(q);
            }
        };

        public SpanQuery rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
            return this.delegate.rewrite(reader, query);
        }

        protected Object readResolve() {
            return SCORING_SPAN_QUERY_REWRITE;
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
        try {
            this.getFieldMethod = query.getClass().getMethod("getField", new Class[0]);
        }
        catch (Exception e1) {
            try {
                this.getTermMethod = query.getClass().getMethod("getTerm", new Class[0]);
            }
            catch (Exception e2) {
                try {
                    this.getTermMethod = query.getClass().getMethod("getPrefix", new Class[0]);
                }
                catch (Exception e3) {
                    throw new IllegalArgumentException("SpanMultiTermQueryWrapper can only wrap MultiTermQueries that can return a field name using getField() or getTerm()");
                }
            }
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
    public Spans getSpans(IndexReader reader) throws IOException {
        throw new UnsupportedOperationException("Query should have been rewritten");
    }

    @Override
    public String getField() {
        try {
            if (this.getFieldMethod != null) {
                return (String)this.getFieldMethod.invoke(this.query, new Object[0]);
            }
            assert (this.getTermMethod != null);
            return ((Term)this.getTermMethod.invoke(this.query, new Object[0])).field();
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot invoke getField() or getTerm() on wrapped query.", e);
        }
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
                protected void addClause(SpanOrQuery topLevel, Term term, float boost) {
                    SpanTermQuery q = new SpanTermQuery(term);
                    q.setBoost(boost);
                    topLevel.addClause(q);
                }
            };
        }

        public int getSize() {
            return this.delegate.getSize();
        }

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
        public abstract SpanQuery rewrite(IndexReader var1, MultiTermQuery var2) throws IOException;
    }
}

