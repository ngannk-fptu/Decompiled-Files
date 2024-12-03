/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanQuery;
import com.atlassian.lucene36.search.ConstantScoreQuery;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.TermCollectingRewrite;
import com.atlassian.lucene36.search.TermQuery;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ScoringRewrite<Q extends Query>
extends TermCollectingRewrite<Q> {
    public static final ScoringRewrite<BooleanQuery> SCORING_BOOLEAN_QUERY_REWRITE = new ScoringRewrite<BooleanQuery>(){

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

        protected Object readResolve() {
            return SCORING_BOOLEAN_QUERY_REWRITE;
        }
    };
    public static final MultiTermQuery.RewriteMethod CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE = new MultiTermQuery.RewriteMethod(){

        public Query rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
            BooleanQuery bq = SCORING_BOOLEAN_QUERY_REWRITE.rewrite(reader, query);
            if (bq.clauses().isEmpty()) {
                return bq;
            }
            ConstantScoreQuery result = new ConstantScoreQuery(bq);
            result.setBoost(query.getBoost());
            return result;
        }

        protected Object readResolve() {
            return CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE;
        }
    };

    public Q rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
        Object result = this.getTopLevelQuery();
        int[] size = new int[1];
        this.collectTerms(reader, query, new TermCollectingRewrite.TermCollector((Query)result, query, size){
            final /* synthetic */ Query val$result;
            final /* synthetic */ MultiTermQuery val$query;
            final /* synthetic */ int[] val$size;
            {
                this.val$result = query;
                this.val$query = multiTermQuery;
                this.val$size = nArray;
            }

            public boolean collect(Term t, float boost) throws IOException {
                ScoringRewrite.this.addClause(this.val$result, t, this.val$query.getBoost() * boost);
                this.val$size[0] = this.val$size[0] + 1;
                return true;
            }
        });
        query.incTotalNumberOfTerms(size[0]);
        return result;
    }
}

