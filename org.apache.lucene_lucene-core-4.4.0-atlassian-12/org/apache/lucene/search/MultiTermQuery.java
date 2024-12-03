/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.MultiTermQueryWrapperFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoringRewrite;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopTermsRewrite;
import org.apache.lucene.util.AttributeSource;

public abstract class MultiTermQuery
extends Query {
    protected final String field;
    protected RewriteMethod rewriteMethod = CONSTANT_SCORE_AUTO_REWRITE_DEFAULT;
    public static final RewriteMethod CONSTANT_SCORE_FILTER_REWRITE = new RewriteMethod(){

        @Override
        public Query rewrite(IndexReader reader, MultiTermQuery query) {
            ConstantScoreQuery result = new ConstantScoreQuery(new MultiTermQueryWrapperFilter<MultiTermQuery>(query));
            result.setBoost(query.getBoost());
            return result;
        }
    };
    public static final RewriteMethod SCORING_BOOLEAN_QUERY_REWRITE = ScoringRewrite.SCORING_BOOLEAN_QUERY_REWRITE;
    public static final RewriteMethod CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE = ScoringRewrite.CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE;
    public static final RewriteMethod CONSTANT_SCORE_AUTO_REWRITE_DEFAULT = new ConstantScoreAutoRewrite(){

        @Override
        public void setTermCountCutoff(int count) {
            throw new UnsupportedOperationException("Please create a private instance");
        }

        @Override
        public void setDocCountPercent(double percent) {
            throw new UnsupportedOperationException("Please create a private instance");
        }
    };

    public MultiTermQuery(String field) {
        if (field == null) {
            throw new IllegalArgumentException("field must not be null");
        }
        this.field = field;
    }

    public final String getField() {
        return this.field;
    }

    protected abstract TermsEnum getTermsEnum(Terms var1, AttributeSource var2) throws IOException;

    protected final TermsEnum getTermsEnum(Terms terms) throws IOException {
        return this.getTermsEnum(terms, new AttributeSource());
    }

    @Override
    public final Query rewrite(IndexReader reader) throws IOException {
        return this.rewriteMethod.rewrite(reader, this);
    }

    public RewriteMethod getRewriteMethod() {
        return this.rewriteMethod;
    }

    public void setRewriteMethod(RewriteMethod method) {
        this.rewriteMethod = method;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Float.floatToIntBits(this.getBoost());
        result = 31 * result + this.rewriteMethod.hashCode();
        if (this.field != null) {
            result = 31 * result + this.field.hashCode();
        }
        return result;
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
        MultiTermQuery other = (MultiTermQuery)obj;
        if (Float.floatToIntBits(this.getBoost()) != Float.floatToIntBits(other.getBoost())) {
            return false;
        }
        if (!this.rewriteMethod.equals(other.rewriteMethod)) {
            return false;
        }
        return other.field == null ? this.field == null : other.field.equals(this.field);
    }

    public static class ConstantScoreAutoRewrite
    extends org.apache.lucene.search.ConstantScoreAutoRewrite {
    }

    public static final class TopTermsBoostOnlyBooleanQueryRewrite
    extends TopTermsRewrite<BooleanQuery> {
        public TopTermsBoostOnlyBooleanQueryRewrite(int size) {
            super(size);
        }

        @Override
        protected int getMaxSize() {
            return BooleanQuery.getMaxClauseCount();
        }

        @Override
        protected BooleanQuery getTopLevelQuery() {
            return new BooleanQuery(true);
        }

        @Override
        protected void addClause(BooleanQuery topLevel, Term term, int docFreq, float boost, TermContext states) {
            ConstantScoreQuery q = new ConstantScoreQuery(new TermQuery(term, states));
            q.setBoost(boost);
            topLevel.add(q, BooleanClause.Occur.SHOULD);
        }
    }

    public static final class TopTermsScoringBooleanQueryRewrite
    extends TopTermsRewrite<BooleanQuery> {
        public TopTermsScoringBooleanQueryRewrite(int size) {
            super(size);
        }

        @Override
        protected int getMaxSize() {
            return BooleanQuery.getMaxClauseCount();
        }

        @Override
        protected BooleanQuery getTopLevelQuery() {
            return new BooleanQuery(true);
        }

        @Override
        protected void addClause(BooleanQuery topLevel, Term term, int docCount, float boost, TermContext states) {
            TermQuery tq = new TermQuery(term, states);
            tq.setBoost(boost);
            topLevel.add(tq, BooleanClause.Occur.SHOULD);
        }
    }

    public static abstract class RewriteMethod {
        public abstract Query rewrite(IndexReader var1, MultiTermQuery var2) throws IOException;

        protected TermsEnum getTermsEnum(MultiTermQuery query, Terms terms, AttributeSource atts) throws IOException {
            return query.getTermsEnum(terms, atts);
        }
    }
}

