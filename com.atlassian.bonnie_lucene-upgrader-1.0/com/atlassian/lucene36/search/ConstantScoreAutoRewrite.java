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
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ConstantScoreAutoRewrite
extends TermCollectingRewrite<BooleanQuery> {
    public static int DEFAULT_TERM_COUNT_CUTOFF = 350;
    public static double DEFAULT_DOC_COUNT_PERCENT = 0.1;
    private int termCountCutoff = DEFAULT_TERM_COUNT_CUTOFF;
    private double docCountPercent = DEFAULT_DOC_COUNT_PERCENT;

    ConstantScoreAutoRewrite() {
    }

    public void setTermCountCutoff(int count) {
        this.termCountCutoff = count;
    }

    public int getTermCountCutoff() {
        return this.termCountCutoff;
    }

    public void setDocCountPercent(double percent) {
        this.docCountPercent = percent;
    }

    public double getDocCountPercent() {
        return this.docCountPercent;
    }

    @Override
    protected BooleanQuery getTopLevelQuery() {
        return new BooleanQuery(true);
    }

    @Override
    protected void addClause(BooleanQuery topLevel, Term term, float boost) {
        topLevel.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
    }

    @Override
    public Query rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
        Query result;
        int docCountCutoff = (int)(this.docCountPercent / 100.0 * (double)reader.maxDoc());
        int termCountLimit = Math.min(BooleanQuery.getMaxClauseCount(), this.termCountCutoff);
        CutOffTermCollector col = new CutOffTermCollector(reader, docCountCutoff, termCountLimit);
        this.collectTerms(reader, query, col);
        if (col.hasCutOff) {
            return MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE.rewrite(reader, query);
        }
        if (col.pendingTerms.isEmpty()) {
            result = this.getTopLevelQuery();
        } else {
            BooleanQuery bq = this.getTopLevelQuery();
            for (Term term : col.pendingTerms) {
                this.addClause(bq, term, 1.0f);
            }
            result = new ConstantScoreQuery(bq);
            result.setBoost(query.getBoost());
        }
        query.incTotalNumberOfTerms(col.pendingTerms.size());
        return result;
    }

    public int hashCode() {
        int prime = 1279;
        return (int)((long)(1279 * this.termCountCutoff) + Double.doubleToLongBits(this.docCountPercent));
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
        ConstantScoreAutoRewrite other = (ConstantScoreAutoRewrite)obj;
        if (other.termCountCutoff != this.termCountCutoff) {
            return false;
        }
        return Double.doubleToLongBits(other.docCountPercent) == Double.doubleToLongBits(this.docCountPercent);
    }

    private static final class CutOffTermCollector
    implements TermCollectingRewrite.TermCollector {
        int docVisitCount = 0;
        boolean hasCutOff = false;
        final IndexReader reader;
        final int docCountCutoff;
        final int termCountLimit;
        final ArrayList<Term> pendingTerms = new ArrayList();

        CutOffTermCollector(IndexReader reader, int docCountCutoff, int termCountLimit) {
            this.reader = reader;
            this.docCountCutoff = docCountCutoff;
            this.termCountLimit = termCountLimit;
        }

        public boolean collect(Term t, float boost) throws IOException {
            this.pendingTerms.add(t);
            this.docVisitCount += this.reader.docFreq(t);
            if (this.pendingTerms.size() >= this.termCountLimit || this.docVisitCount >= this.docCountCutoff) {
                this.hasCutOff = true;
                return false;
            }
            return true;
        }
    }
}

