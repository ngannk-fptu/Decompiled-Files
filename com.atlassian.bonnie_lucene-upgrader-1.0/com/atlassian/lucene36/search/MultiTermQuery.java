/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanQuery;
import com.atlassian.lucene36.search.ConstantScoreQuery;
import com.atlassian.lucene36.search.FilteredTermEnum;
import com.atlassian.lucene36.search.MultiTermQueryWrapperFilter;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.ScoringRewrite;
import com.atlassian.lucene36.search.TermQuery;
import com.atlassian.lucene36.search.TopTermsRewrite;
import java.io.IOException;
import java.io.Serializable;

public abstract class MultiTermQuery
extends Query {
    protected RewriteMethod rewriteMethod = CONSTANT_SCORE_AUTO_REWRITE_DEFAULT;
    transient int numberOfTerms = 0;
    public static final RewriteMethod CONSTANT_SCORE_FILTER_REWRITE = new RewriteMethod(){

        public Query rewrite(IndexReader reader, MultiTermQuery query) {
            ConstantScoreQuery result = new ConstantScoreQuery(new MultiTermQueryWrapperFilter<MultiTermQuery>(query));
            result.setBoost(query.getBoost());
            return result;
        }

        protected Object readResolve() {
            return CONSTANT_SCORE_FILTER_REWRITE;
        }
    };
    public static final RewriteMethod SCORING_BOOLEAN_QUERY_REWRITE = ScoringRewrite.SCORING_BOOLEAN_QUERY_REWRITE;
    public static final RewriteMethod CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE = ScoringRewrite.CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE;
    public static final RewriteMethod CONSTANT_SCORE_AUTO_REWRITE_DEFAULT = new ConstantScoreAutoRewrite(){

        public void setTermCountCutoff(int count) {
            throw new UnsupportedOperationException("Please create a private instance");
        }

        public void setDocCountPercent(double percent) {
            throw new UnsupportedOperationException("Please create a private instance");
        }

        protected Object readResolve() {
            return CONSTANT_SCORE_AUTO_REWRITE_DEFAULT;
        }
    };

    protected abstract FilteredTermEnum getEnum(IndexReader var1) throws IOException;

    @Deprecated
    public int getTotalNumberOfTerms() {
        return this.numberOfTerms;
    }

    @Deprecated
    public void clearTotalNumberOfTerms() {
        this.numberOfTerms = 0;
    }

    @Deprecated
    protected void incTotalNumberOfTerms(int inc) {
        this.numberOfTerms += inc;
    }

    public final Query rewrite(IndexReader reader) throws IOException {
        return this.rewriteMethod.rewrite(reader, this);
    }

    public RewriteMethod getRewriteMethod() {
        return this.rewriteMethod;
    }

    public void setRewriteMethod(RewriteMethod method) {
        this.rewriteMethod = method;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Float.floatToIntBits(this.getBoost());
        result = 31 * result;
        return result += this.rewriteMethod.hashCode();
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
        MultiTermQuery other = (MultiTermQuery)obj;
        if (Float.floatToIntBits(this.getBoost()) != Float.floatToIntBits(other.getBoost())) {
            return false;
        }
        return this.rewriteMethod.equals(other.rewriteMethod);
    }

    public static class ConstantScoreAutoRewrite
    extends com.atlassian.lucene36.search.ConstantScoreAutoRewrite {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
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
        protected void addClause(BooleanQuery topLevel, Term term, float boost) {
            ConstantScoreQuery q = new ConstantScoreQuery(new TermQuery(term));
            q.setBoost(boost);
            topLevel.add(q, BooleanClause.Occur.SHOULD);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
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
        protected void addClause(BooleanQuery topLevel, Term term, float boost) {
            TermQuery tq = new TermQuery(term);
            tq.setBoost(boost);
            topLevel.add(tq, BooleanClause.Occur.SHOULD);
        }
    }

    public static abstract class RewriteMethod
    implements Serializable {
        public abstract Query rewrite(IndexReader var1, MultiTermQuery var2) throws IOException;

        protected FilteredTermEnum getTermsEnum(IndexReader reader, MultiTermQuery query) throws IOException {
            return query.getEnum(reader);
        }
    }
}

