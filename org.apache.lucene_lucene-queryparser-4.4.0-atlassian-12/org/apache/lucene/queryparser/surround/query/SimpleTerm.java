/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.DistanceSubQuery;
import org.apache.lucene.queryparser.surround.query.SimpleTermRewriteQuery;
import org.apache.lucene.queryparser.surround.query.SpanNearClauseFactory;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;

public abstract class SimpleTerm
extends SrndQuery
implements DistanceSubQuery,
Comparable<SimpleTerm> {
    private boolean quoted;

    public SimpleTerm(boolean q) {
        this.quoted = q;
    }

    boolean isQuoted() {
        return this.quoted;
    }

    public String getQuote() {
        return "\"";
    }

    public String getFieldOperator() {
        return "/";
    }

    public abstract String toStringUnquoted();

    @Override
    @Deprecated
    public int compareTo(SimpleTerm ost) {
        return this.toStringUnquoted().compareTo(ost.toStringUnquoted());
    }

    protected void suffixToString(StringBuilder r) {
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        if (this.isQuoted()) {
            r.append(this.getQuote());
        }
        r.append(this.toStringUnquoted());
        if (this.isQuoted()) {
            r.append(this.getQuote());
        }
        this.suffixToString(r);
        this.weightToString(r);
        return r.toString();
    }

    public abstract void visitMatchingTerms(IndexReader var1, String var2, MatchingTermVisitor var3) throws IOException;

    @Override
    public String distanceSubQueryNotAllowed() {
        return null;
    }

    @Override
    public void addSpanQueries(final SpanNearClauseFactory sncf) throws IOException {
        this.visitMatchingTerms(sncf.getIndexReader(), sncf.getFieldName(), new MatchingTermVisitor(){

            @Override
            public void visitMatchingTerm(Term term) throws IOException {
                sncf.addTermWeighted(term, SimpleTerm.this.getWeight());
            }
        });
    }

    @Override
    public Query makeLuceneQueryFieldNoBoost(String fieldName, BasicQueryFactory qf) {
        return new SimpleTermRewriteQuery(this, fieldName, qf);
    }

    public static interface MatchingTermVisitor {
        public void visitMatchingTerm(Term var1) throws IOException;
    }
}

